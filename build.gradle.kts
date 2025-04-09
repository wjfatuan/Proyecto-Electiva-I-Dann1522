// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Plugin for Android application. Applied to app modules.
    alias(libs.plugins.android.application) apply false
    
    // Plugin for Android libraries. Applied to Android library modules.
    alias(libs.plugins.android.library) apply false
    
    // Plugin for Kotlin in Android. Applied to modules that use Kotlin.
    alias(libs.plugins.kotlin.android) apply false
}

// Additional configurations for your Flickzy project
subprojects {
    afterEvaluate { project ->
        if (project.hasProperty("android")) {
            // Configure the Kotlin compiler for Android subprojects if needed
            project.extensions.getByType(com.android.build.gradle.internal.dsl.BaseAppModuleExtension).apply {
                compileSdkVersion 33 // Define the SDK version to compile against
                defaultConfig {
                    applicationId = "com.flickzy.app" // Change this to your app's package name
                    minSdkVersion 21 // Set the minimum supported SDK version
                    targetSdkVersion 33 // Set the target SDK version
                    versionCode 1
                    versionName "1.0"
                }
            }
        }
    }
}
