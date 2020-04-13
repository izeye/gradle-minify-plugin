package org.padler.gradle.minify.minifier

import com.google.javascript.jscomp.SourceMap

class LocationMapping : SourceMap.LocationMapping {
    override fun map(location: String): String? {
        return if (location.contains("/")) location.substring(location.lastIndexOf("/") + 1) else null
    }
}
