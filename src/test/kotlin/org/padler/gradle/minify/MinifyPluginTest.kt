package org.padler.gradle.minify

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test

class MinifyPluginTest {
    @Test
    fun should_add_task_to_project() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("org.padler.gradle.minify")
        Assert.assertTrue(project.tasks.getByName("minify") is MinifyTask)
    }
}
