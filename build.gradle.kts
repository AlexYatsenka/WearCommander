
// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
  dependencies {
    classpath(libs.kotlin.gradle.plugin)

    // NOTE: Do not place your application dependencies here; they belong
    // in the individual module build.gradle files
  }
}

plugins {
    kotlin("kapt") version "1.9.0" apply false
    alias(libs.plugins.com.diffplug.spotless) apply(false)
    alias(libs.plugins.com.android.application) apply(false)
    alias(libs.plugins.roborazzi) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
}

subprojects {
  apply(plugin = "com.diffplug.spotless")
  configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
      target("**/*.kt")
      targetExclude("bin/**/*.kt")

      ktlint(libs.versions.ktlint.get())
      licenseHeaderFile(rootProject.file("../spotless/copyright.kt"))
    }

    kotlinGradle {
      target("*.gradle.kts")
      ktlint()
    }
  }
}
