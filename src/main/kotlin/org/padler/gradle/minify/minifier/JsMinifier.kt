package org.padler.gradle.minify.minifier

import com.google.common.collect.ImmutableList
import com.google.javascript.jscomp.*
import org.padler.gradle.minify.minifier.options.JSMinifierOptions
import org.padler.gradle.minify.minifier.result.Error
import org.padler.gradle.minify.minifier.result.Warning
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter

class JsMinifier : Minifier() {

    override var minifierOptions = JSMinifierOptions()

    private val options = CompilerOptions()

    @Throws(IOException::class)
    override fun minifyFile(srcFile: File, dstFile: File) {
        val compiler = Compiler()
        OutputStreamWriter(FileOutputStream(dstFile)).use { iosW ->
            val externs = AbstractCommandLineRunner.getBuiltinExterns(CompilerOptions().environment)
            val sourceFile = SourceFile.fromFile(srcFile.absolutePath)
            var sourcemapFile: File? = null
            if (minifierOptions.createSoureMaps) {
                sourcemapFile = File(dstFile.absolutePath + ".map")
                options.setSourceMapOutputPath(sourcemapFile.absolutePath)
                options.setSourceMapLocationMappings(listOf(LocationMapping()))
            }
            val result = compiler.compile(externs, ImmutableList.of(sourceFile), options)
            if (result.success) {
                iosW.write(compiler.toSource())
                if (minifierOptions.createSoureMaps) {
                    val sourceMapContent = StringBuilder()
                    result.sourceMap.appendTo(sourceMapContent, dstFile.name)
                    writeToFile(sourcemapFile!!, sourceMapContent.toString())
                }
            } else {
                for (error in result.errors) {
                    report.add(Error(error))
                }
                for (warning in result.warnings) {
                    report.add(Warning(warning))
                }
            }
            iosW.flush()
        }
    }

    override val minifierName = "JS Minifier"

    override fun rename(oldName: String): String {
        return oldName.replace(".js", ".min.js")
    }

    init {
        CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(options)
        WarningLevel.QUIET.setOptionsForWarningLevel(options)
    }
}
