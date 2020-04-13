package org.padler.gradle.minify.minifier

import com.google.common.collect.Lists
import com.google.common.css.*
import com.google.common.css.compiler.ast.BasicErrorManager
import com.google.common.css.compiler.ast.ErrorManager
import com.google.common.css.compiler.ast.GssError
import com.google.common.css.compiler.commandline.DefaultCommandLineCompiler
import com.google.common.css.compiler.gssfunctions.DefaultGssFunctionMapProvider
import org.padler.gradle.minify.minifier.options.CSSMinifierOptions
import org.padler.gradle.minify.minifier.result.Error
import org.padler.gradle.minify.minifier.result.Warning
import java.io.File
import java.io.IOException
import java.io.UncheckedIOException
import java.nio.file.Files
import java.util.*

/**
 * Uses closure stylesheets.
 * Implemented with help from https://github.com/marcodelpercio https://github.com/google/closure-stylesheets/issues/101
 */
class CssMinifier : Minifier() {
    override var minifierOptions = CSSMinifierOptions()
        protected set

    override fun minifyFile(srcFile: File, dstFile: File) {
        try {
            val job = createJobDescription(srcFile)
            val exitCodeHandler: ExitCodeHandler = DefaultExitCodeHandler()
            val errorManager = CompilerErrorManager()
            val compiler = ClosureStylesheetCompiler(job, exitCodeHandler, errorManager)
            var sourcemapFile: File? = null
            if (minifierOptions.createSoureMaps) {
                sourcemapFile = File(dstFile.absolutePath + ".map")
            }
            val compilerOutput = compiler.execute(null, sourcemapFile)
            writeToFile(dstFile, compilerOutput)
        } catch (e: IOException) {
            throw UncheckedIOException(e)
        }
    }

    override val minifierName = "CSS Minifier"

    override fun rename(oldName: String): String {
        return oldName.replace(".css", ".min.css")
    }

    @Throws(IOException::class)
    private fun createJobDescription(file: File): JobDescription {
        val builder = JobDescriptionBuilder()
        builder.setInputOrientation(JobDescription.InputOrientation.LTR)
        builder.setOutputOrientation(JobDescription.OutputOrientation.LTR)
        builder.setOutputFormat(JobDescription.OutputFormat.COMPRESSED)
        builder.setCopyrightNotice(null)
        builder.setTrueConditionNames(Lists.newArrayList())
        builder.setAllowDefPropagation(true)
        builder.setAllowUnrecognizedFunctions(true)
        builder.setAllowedNonStandardFunctions(Lists.newArrayList())
        builder.setAllowedUnrecognizedProperties(Lists.newArrayList())
        builder.setAllowUnrecognizedProperties(true)
        builder.setVendor(null)
        builder.setAllowKeyframes(true)
        builder.setAllowWebkitKeyframes(true)
        builder.setProcessDependencies(true)
        builder.setExcludedClassesFromRenaming(Lists.newArrayList())
        builder.setSimplifyCss(true)
        /* sadly the following line is necessary until they introduce support for --allow-duplicate-declarations  */builder.setEliminateDeadStyles(false)
        builder.setCssSubstitutionMapProvider { IdentitySubstitutionMap() }
        builder.setCssRenamingPrefix("")
        builder.setPreserveComments(false)
        builder.setOutputRenamingMapFormat(OutputRenamingMapFormat.JSON)
        builder.setCompileConstants(HashMap())
        val gssFunMapProv: GssFunctionMapProvider = DefaultGssFunctionMapProvider()
        builder.setGssFunctionMapProvider(gssFunMapProv)
        builder.setSourceMapLevel(JobDescription.SourceMapDetailLevel.DEFAULT)
        builder.setCreateSourceMap(minifierOptions.createSoureMaps)
        val fileContents = String(Files.readAllBytes(file.toPath()))
        builder.addInput(SourceCode(file.name, fileContents))
        return builder.jobDescription
    }

    internal inner class CompilerErrorManager : BasicErrorManager() {
        override fun print(msg: String) { // Do nothing to have all errors at the end
        }

        override fun report(error: GssError) {
            report.add(Error(error))
        }

        override fun reportWarning(warning: GssError) {
            report.add(Warning(warning))
        }
    }

    inner class ClosureStylesheetCompiler(job: JobDescription?, exitCodeHandler: ExitCodeHandler?, errorManager: ErrorManager?) : DefaultCommandLineCompiler(job, exitCodeHandler, errorManager) {
        public override fun execute(renameFile: File?, sourcemapFile: File?): String {
            return super.execute(renameFile, sourcemapFile)
        }
    }
}
