// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.google.devtools.ksp") version "1.9.0-1.0.12" apply false
}

buildscript {
    val kotlinVersion by extra("1.9.10")
    val hiltVersion by extra("2.48.1")

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.android.tools.build:gradle:8.2.0-rc01")
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle.kts files
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
