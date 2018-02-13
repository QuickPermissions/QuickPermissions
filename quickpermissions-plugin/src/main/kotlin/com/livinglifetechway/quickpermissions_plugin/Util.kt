package com.livinglifetechway.quickpermissions_plugin

import com.android.build.gradle.*
import org.gradle.api.Project

val Project.androidModule: TestedExtension?
    get() {
        // return default app extension if not found any specific
        var module: TestedExtension? = null

        if (project.plugins.hasPlugin(AppPlugin::class.java)) {
            module = project.extensions.getByType(AppExtension::class.java)
        } else if (project.plugins.hasPlugin(LibraryPlugin::class.java)) {
            module = project.extensions.getByType(LibraryExtension::class.java)
        } else if (project.plugins.hasPlugin(FeaturePlugin::class.java)) {
            module = project.extensions.getByType(FeatureExtension::class.java)
        } else if (project.plugins.hasPlugin(InstantAppPlugin::class.java)) {
            module = project.extensions.getByType(FeatureExtension::class.java)
        }

        // return specific module
        return module
    }