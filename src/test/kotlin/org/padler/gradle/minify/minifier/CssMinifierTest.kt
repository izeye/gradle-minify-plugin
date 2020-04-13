package org.padler.gradle.minify.minifier

import org.hamcrest.core.Is
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors

class CssMinifierTest {
    @JvmField
    @Rule
    val testProjectDir = TemporaryFolder()

    @Test
    @Throws(Exception::class)
    fun minifyFile() {
        val cssMinifier = CssMinifier()
        val dst = testProjectDir.newFolder("dst")
        cssMinifier.minify("src/test/resources/css", dst.absolutePath)
        val files = Files.list(Paths.get(dst.absolutePath + "/")).collect(Collectors.toList())
        Assert.assertThat(files.size, Is.`is`(2))
        val subDir = files.stream().filter { p: Path? -> p!!.toFile().name.endsWith("sub") }.findFirst().orElse(null)
        val subFiles = Files.list(subDir).collect(Collectors.toList())
        Assert.assertThat(subFiles.size, Is.`is`(1))
    }

    @Test
    @Throws(Exception::class)
    fun minifyFileWithSourceMaps() {
        val cssMinifier = CssMinifier()
        cssMinifier.minifierOptions.createSoureMaps = true
        val dst = testProjectDir.newFolder("dst")
        cssMinifier.minify("src/test/resources/css", dst.absolutePath)
        val files = Files.list(Paths.get(dst.absolutePath + "/")).collect(Collectors.toList())
        Assert.assertThat(files.size, Is.`is`(3))
        val subDir = files.stream().filter { p: Path? -> p!!.toFile().name.endsWith("sub") }.findFirst().orElse(null)
        val subFiles = Files.list(subDir).collect(Collectors.toList())
        Assert.assertThat(subFiles.size, Is.`is`(2))
    }

    @Test
    @Throws(Exception::class)
    fun minifyFileWithError() {
        val cssMinifier = CssMinifier()
        val dst = testProjectDir.newFolder("dst")
        cssMinifier.minify("src/test/resources/errors/css", dst.absolutePath)
        val files = Files.list(Paths.get(dst.absolutePath + "/")).collect(Collectors.toList())
        Assert.assertThat(files.size, Is.`is`(1))
        Assert.assertThat(cssMinifier.report.errors.size, Is.`is`(0))
        Assert.assertThat(cssMinifier.report.warnings.size, Is.`is`(1))
    }
}
