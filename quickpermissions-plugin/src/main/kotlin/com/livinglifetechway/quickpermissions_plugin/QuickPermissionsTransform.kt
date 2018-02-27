package com.livinglifetechway.quickpermissions_plugin

import com.android.build.api.transform.*
import com.google.common.base.Joiner
import com.google.common.base.Strings
import com.google.common.collect.Sets
import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main
import org.gradle.api.GradleException
import org.gradle.api.Project
import java.io.File

class QuickPermissionsTransform(private val project: Project) : Transform() {

    override fun transform(transformInvocation: TransformInvocation?) {

        val files = arrayListOf<File>()
        val classpathFiles = arrayListOf<File>()
        val logger = project.logger

        val outputProvider: TransformOutputProvider? = transformInvocation?.outputProvider
        // clean
//        outputProvider?.deleteAll()

        log("Output Provider: ")

        //  Referenced Inputs to classpath
        transformInvocation?.referencedInputs?.forEach {
            it.directoryInputs.forEach {
                log("REF DIR INPUT" + it.file.toString())
                classpathFiles.add(it.file)
            }

            it.jarInputs.forEach {
                if (it.file.name.contains("quickpermissions-annotations") || it.file.name.contains("aspectjrt")) {
                    log("REF JAR INPUT" + it.file.toString())
                    classpathFiles.add(it.file)
                }
            }
        }

        // Scope inputs
        transformInvocation?.inputs?.forEach {
            it.directoryInputs.forEach {
                log("DIR INPUT: " + it.file.toString())
                files.add(it.file)
            }
            it.jarInputs.forEach {
                if (it.file.name.contains("quickpermissions-annotations") || it.file.name.contains("aspectjrt")) {
                    log("JAR INPUT" + it.file.toString())
                    files.add(it.file)
                }
            }
        }

        // Evaluate class paths
        val inpath: String = Joiner.on(File.pathSeparator).join(files)
        val classpath: String = Joiner.on(File.pathSeparator).join(
                classpathFiles.map { it.absolutePath })
        val bootpath: String = Joiner.on(File.pathSeparator).join(project.androidModule?.bootClasspath)
        val output: File? = outputProvider?.getContentLocation("main", outputTypes, Sets.immutableEnumSet(QualifiedContent.Scope.PROJECT), Format.DIRECTORY)

        // Weaving args
        val args = mutableListOf(
                "-verbose",
                "-source", "1.7",
                "-target", "1.7",
                "-showWeaveInfo",
                "-inpath", inpath,
                "-d", output?.absolutePath,
                "-bootclasspath", bootpath)

        // Append classpath argument if any
        if (!Strings.isNullOrEmpty(classpath)) {
            args.add("-classpath")
            args.add(classpath)
        }

        // run aspectj
        val handler = MessageHandler(true)
        Main().run(args.toTypedArray(), handler)

        handler.getMessages(null, true).forEach { message ->
            when {
                IMessage.ERROR.isSameOrLessThan(message.kind) -> {
                    logger.error(message.message, message.thrown)
                    throw GradleException(message.message, message.thrown)
                }
                IMessage.WARNING.isSameOrLessThan(message.kind) -> logger.warn(message.message)
                IMessage.DEBUG.isSameOrLessThan(message.kind) -> logger.info(message.message)
                else -> logger.debug(message.message)
            }
        }

    }

    override fun getName(): String = "QuickPermissions"

    override fun getInputTypes(): Set<QualifiedContent.ContentType> {
        return Sets.immutableEnumSet(QualifiedContent.DefaultContentType.CLASSES)
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope>? {
        return Sets.immutableEnumSet(QualifiedContent.Scope.PROJECT, QualifiedContent.Scope.EXTERNAL_LIBRARIES)
    }

    override fun getReferencedScopes(): MutableSet<in QualifiedContent.Scope>? {
        return Sets.immutableEnumSet(
                QualifiedContent.Scope.SUB_PROJECTS,
                QualifiedContent.Scope.EXTERNAL_LIBRARIES
        )
    }

    override fun isIncremental(): Boolean = true

    fun log(str: String) {
        println("-------------- QP-PLUGIN: $str --------------")
    }

}