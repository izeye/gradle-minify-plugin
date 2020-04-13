package org.padler.gradle.minify

import org.gradle.api.tasks.TaskAction
import org.padler.gradle.minify.minifier.CssMinifier
import org.padler.gradle.minify.minifier.JsMinifier

open class MinifyTask : MinifyTaskBase() {
    @TaskAction
    fun minify() {
        if (!extension.cssSrcDir.isEmpty() && !extension.cssDstDir.isEmpty()) {
            val cssMinifier = CssMinifier()
            val minifierOptions = cssMinifier.minifierOptions
            minifierOptions.createSoureMaps = extension.createCssSourceMaps
            minifierOptions.originalFileNames = extension.originalFileNames
            cssMinifier.minify(extension.cssSrcDir, extension.cssDstDir)
        }
        if (!extension.jsSrcDir.isEmpty() && !extension.jsDstDir.isEmpty()) {
            val jsMinifier = JsMinifier()
            val minifierOptions = jsMinifier.minifierOptions
            minifierOptions.createSoureMaps = extension.createJsSourceMaps
            minifierOptions.originalFileNames = extension.originalFileNames
            jsMinifier.minify(extension.jsSrcDir, extension.jsDstDir)
        }
    }
}
