package org.padler.gradle.minify.minifier

import org.gradle.api.GradleException
import org.hamcrest.CoreMatchers
import org.hamcrest.core.Is
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors

class JsMinifierTest {
    @JvmField
    @Rule
    val testProjectDir = TemporaryFolder()

    @Test
    @Throws(Exception::class)
    fun minifyFile() {
        val jsMinifier = JsMinifier()
        val dst = testProjectDir.newFolder("dst")
        jsMinifier.minify("src/test/resources/js", dst.absolutePath)
        val files = Files.list(Paths.get(dst.absolutePath + "/")).collect(Collectors.toList())
        Assert.assertThat(files.size, Is.`is`(2))
        val jsFile = files.stream().filter { path: Path? -> path!!.toFile().isFile }.findFirst().orElse(null)
        Assert.assertThat(jsFile!!.toFile().absolutePath, CoreMatchers.containsString("min.js"))
        val subDir = files.stream().filter { p: Path? -> p!!.toFile().name.endsWith("sub") }.findFirst().orElse(null)
        val subFiles = Files.list(subDir).collect(Collectors.toList())
        Assert.assertThat(subFiles.size, Is.`is`(1))
    }

    @Test
    @Throws(Exception::class)
    fun minifyFileWithoutRenaming() {
        val jsMinifier = JsMinifier()
        jsMinifier.minifierOptions.originalFileNames = true
        val dst = testProjectDir.newFolder("dst")
        jsMinifier.minify("src/test/resources/js", dst.absolutePath)
        val files = Files.list(Paths.get(dst.absolutePath + "/")).collect(Collectors.toList())
        Assert.assertThat(files.size, Is.`is`(2))
        val jsFile = files.stream().filter { path: Path? -> path!!.toFile().isFile }.findFirst().orElse(null)
        Assert.assertThat(jsFile!!.toFile().absolutePath, CoreMatchers.not(CoreMatchers.containsString("min.js")))
        val subDir = files.stream().filter { p: Path? -> p!!.toFile().name.endsWith("sub") }.findFirst().orElse(null)
        val subFiles = Files.list(subDir).collect(Collectors.toList())
        Assert.assertThat(subFiles.size, Is.`is`(1))
    }

    @Test
    @Throws(Exception::class)
    fun minifyFileWithSourceMaps() {
        val jsMinifier = JsMinifier()
        jsMinifier.minifierOptions.createSoureMaps = true
        val dst = testProjectDir.newFolder("dst")
        jsMinifier.minify("src/test/resources/js", dst.absolutePath)
        val files = Files.list(Paths.get(dst.absolutePath + "/")).collect(Collectors.toList())
        Assert.assertThat(files.size, Is.`is`(3))
        val subDir = files.stream().filter { p: Path? -> p!!.toFile().name.endsWith("sub") }.findFirst().orElse(null)
        val subFiles = Files.list(subDir).collect(Collectors.toList())
        Assert.assertThat(subFiles.size, Is.`is`(2))
    }

    @Test
    @Throws(Exception::class)
    fun minifyFileWithError() {
        val jsMinifier = JsMinifier()
        jsMinifier.minifierOptions.createSoureMaps = true
        val dst = testProjectDir.newFolder("dst")
        try {
            jsMinifier.minify("src/test/resources/errors/js", dst.absolutePath)
            Assert.fail("expected exception")
        } catch (e: GradleException) {
            val files = Files.list(Paths.get(dst.absolutePath + "/")).collect(Collectors.toList())
            Assert.assertThat(files.size, Is.`is`(1))
            Assert.assertThat(jsMinifier.report.errors.size, Is.`is`(1))
            Assert.assertThat(jsMinifier.report.warnings.size, Is.`is`(0))
        }
    }

    @Test
    @Throws(Exception::class)
    fun minifyEmptyFile() {
        val jsMinifier = JsMinifier()
        val src = testProjectDir.newFolder("empty")
        testProjectDir.newFile("empty/empty.js")
        val dst = testProjectDir.newFolder("dst")
        jsMinifier.minify(src.absolutePath, dst.absolutePath)
        val files = Files.list(Paths.get(dst.absolutePath + "/")).collect(Collectors.toList())
        Assert.assertThat(files.size, Is.`is`(1))
    }
}
