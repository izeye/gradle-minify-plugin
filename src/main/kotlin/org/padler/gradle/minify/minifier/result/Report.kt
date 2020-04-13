package org.padler.gradle.minify.minifier.result

import java.util.*

class Report {
    val errors: MutableList<Error> = ArrayList()
    val warnings: MutableList<Warning> = ArrayList()
    fun add(error: Error) {
        errors.add(error)
    }

    fun add(warning: Warning) {
        warnings.add(warning)
    }
}
