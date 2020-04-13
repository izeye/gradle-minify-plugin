package org.padler.gradle.minify

open class MinifyPluginExtension {
    var cssDstDir = ""
    var cssSrcDir = ""
    var jsDstDir = ""
    var jsSrcDir = ""
    var createJsSourceMaps = false
    var createCssSourceMaps = false
    var originalFileNames = false
}
