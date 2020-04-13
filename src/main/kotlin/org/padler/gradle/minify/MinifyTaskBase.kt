package org.padler.gradle.minify

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import java.io.File

open class MinifyTaskBase : DefaultTask() {

    protected var extension = run {
        project.extensions.findByType(MinifyPluginExtension::class.java) ?: MinifyPluginExtension()
    }

    @get:InputDirectory
    @get:Optional
    val jsSrcDir: File? = if (extension.jsSrcDir.isEmpty()) null else File(extension.jsSrcDir)

    @get:InputDirectory
    @get:Optional
    val cssSrcDir: File? = if (extension.cssSrcDir.isEmpty()) null else File(extension.cssSrcDir)

    @get:OutputDirectory
    @get:Optional
    val jsDstDir: File? = if (extension.jsDstDir.isEmpty()) null else File(extension.jsDstDir)

    @get:OutputDirectory
    @get:Optional
    val cssDstDir: File? = if (extension.cssDstDir.isEmpty()) null else File(extension.cssDstDir)

    @get:Input
    @get:Optional
    val createCssSourceMaps: Boolean = extension.createCssSourceMaps

    @get:Input
    @get:Optional
    val createJsSourceMaps: Boolean = extension.createJsSourceMaps

    @get:Input
    @get:Optional
    val originalFileNames: Boolean = extension.originalFileNames

}
