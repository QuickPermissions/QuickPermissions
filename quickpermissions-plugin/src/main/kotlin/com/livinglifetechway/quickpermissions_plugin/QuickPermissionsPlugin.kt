package com.livinglifetechway.quickpermissions_plugin

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.FeaturePlugin
import com.android.build.gradle.InstantAppPlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class QuickPermissionsPlugin : Plugin<Project> {

    override fun apply(project: Project?) {

        // check for the android plugins
        // app, lib, feature, instant app
        // it should be one of the above
        project?.let {

            if (project.plugins.hasPlugin(AppPlugin::class.java)
                    || project.plugins.hasPlugin(LibraryPlugin::class.java)
                    || project.plugins.hasPlugin(FeaturePlugin::class.java)
                    || project.plugins.hasPlugin(InstantAppPlugin::class.java)) {

                // register a transformer
                project.androidModule?.registerTransform(QuickPermissionsTransform(project))

                // add necessary dependencies
                project.dependencies.add("implementation", "org.aspectj:aspectjrt:1.8.13")
                project.dependencies.add("api", "com.github.quickpermissions:quickpermissions-annotations:0.3.1")

            } else {
                // throw exception it doesn't work on any other module
                throw IllegalStateException("'com.android.application', 'com.android.library', " +
                        "'com.android.feature' or 'com.android.instantapp' plugin required.")
            }
        }

    }

}