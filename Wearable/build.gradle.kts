import com.android.build.api.dsl.DefaultConfig
import java.util.Locale
import java.util.Properties

plugins {
    kotlin("kapt")
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.roborazzi)
}

android {
    compileSdk = 34

    namespace = "com.example.android.wearable.datalayer"

    defaultConfig {
        applicationId = "com.example.android.wearable.datalayer"
        versionCode = 1
        versionName = "1.0"
        minSdk = 26
        targetSdk = 34
        implementPaths()
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.majorVersion
        freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val composeBom = platform(libs.androidx.compose.bom)

    // General compose dependencies
    implementation(composeBom)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Compose for Wear OS Dependencies
    implementation(libs.wear.compose.material)
    implementation(libs.wear.compose.foundation)
    implementation(libs.playservices.wearable)
    implementation(libs.androidx.ui.test.manifest)

    // Horologist for correct Compose layout
    implementation(libs.horologist.compose.layout)
    implementation(libs.horologist.compose.material)
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // Gson
    implementation(libs.gson)

    // Dagger
    implementation(libs.dagger)
    kapt(libs.dagger.compiler)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)

    // Tiles
    implementation("com.google.android.horologist:horologist-tiles:0.6.20")
    implementation("androidx.wear.tiles:tiles:1.5.0-alpha02")
    implementation("androidx.wear.protolayout:protolayout:1.3.0-alpha02")
    implementation("androidx.wear.protolayout:protolayout-material:1.3.0-alpha02")
    implementation("androidx.wear.protolayout:protolayout-expression:1.3.0-alpha02")
    implementation("com.google.guava:guava:33.3.1-android")

    // Models
    implementation(project(":common"))

    // Preview Tooling
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.wear.compose.ui.tooling)

    // Testing
    testImplementation(libs.androidx.ui.test.junit4)
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.roborazzi.rule)
    testImplementation(libs.test.ext.junit)
    testImplementation(libs.horologist.roboscreenshots) {
        exclude(group = "com.github.QuickBirdEng.kotlin-snapshot-testing")
    }

    androidTestImplementation(libs.test.ext.junit)
    androidTestImplementation(libs.test.espresso.core)
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(composeBom)

    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(composeBom)
}

fun DefaultConfig.implementPaths() {
    val properties = Properties()
    properties.load(project.rootProject.file("local.properties").inputStream())
    properties.forEach {
        if((it.key as? String)?.startsWith("path") == true) {
            buildConfigField(
                type = "String",
                name = (it.key as String).uppercase(Locale.getDefault()).replace('.', '_'),
                value = "\"${it.value}\""
            )
        }
    }
}
