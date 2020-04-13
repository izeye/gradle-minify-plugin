package org.padler.gradle.minify

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.gradle.util.GFileUtils
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.nio.file.Files

class MinifyTaskTest {
    @JvmField
    @Rule
    val testProjectDir = TemporaryFolder()

    @Throws(Exception::class)
    private fun setUpTestProject() {
        val buildFile = testProjectDir.newFile("build.gradle")
        val cssDir = testProjectDir.newFolder("css")
        val jsDir = testProjectDir.newFolder("js")
        val cssFile = File(cssDir, "css.css")
        val jsFile = File(jsDir, "js.js")
        cssFile.createNewFile()
        Files.write(jsFile.toPath(), "alert('Hello, world!');".toByteArray())
        val plugin = "plugins { id 'org.padler.gradle.minify' version '1.4.1' }"
        val config = "minification{cssDstDir=\"\$buildDir/dist/css\"\ncssSrcDir=\"\${rootDir}/css\"\njsDstDir=\"\$buildDir/dist/js\"\njsSrcDir=\"\${rootDir}/js\"}"
        GFileUtils.writeFile(plugin + "\n" + config, buildFile)
    }

    @Test
    @Throws(Exception::class)
    fun test() {
        setUpTestProject()
        val result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .withArguments(MinifyPlugin.TASK_NAME, "--stacktrace")
                .build()
        Assert.assertThat(result.task(":" + MinifyPlugin.TASK_NAME)!!.outcome, CoreMatchers.equalTo(TaskOutcome.SUCCESS))
    }
}
