@file:JvmName("MinifyPlugin")

package org.padler.gradle.minify

import org.gradle.api.Plugin
import org.gradle.api.Project

class MinifyPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create(EXTENSION_NAME, MinifyPluginExtension::class.java)
        val minify = project.tasks.create(TASK_NAME, MinifyTask::class.java)
        minify.group = "build"
    }

    companion object {
        const val EXTENSION_NAME = "minification"
        const val TASK_NAME = "minify"
    }
}
