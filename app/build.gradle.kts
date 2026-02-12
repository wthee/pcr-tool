plugins {
    alias(libs.plugins.application)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.serialization)
}


hilt {
    enableAggregatingTask = true
}

val appVersionCode = 392
val appVersionName = "3.9.2"
val appId = "cn.wthee.pcrtool"

android {
//    splits {
//        abi {
//            isEnable = true
//            reset()
//            include("x86", "x86_64")
//            isUniversalApk = true
//        }
//    }

    namespace = appId
    compileSdk = 36
    buildToolsVersion = "36.0.0"
    flavorDimensions += listOf("version")

    defaultConfig {
        applicationId = appId
        minSdk = 23
        targetSdk = 36
        versionCode = appVersionCode
        versionName = appVersionName


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    productFlavors {
        create("official") {
            applicationId = "cn.wthee.pcrtool"
            dimension = "version"
            resValue("string", "app_name", "PCR Tool")
            resValue("color", "colorPrimary", "#5690EF")
            resValue("color", "colorPrimaryDark", "#3F6BB3")
            buildConfigField("boolean", "DEBUG", "false")
        }

        create("beta") {
            applicationId = "cn.wthee.pcrtoolbeta"
            dimension = "version"
            resValue("string", "app_name", "PCR Tool *")
            resValue("color", "colorPrimary", "#D85280")
            resValue("color", "colorPrimaryDark", "#B93E69")
            buildConfigField("boolean", "DEBUG", "true")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

}

dependencies {
    implementation(platform(libs.compose.bom))

    implementation(libs.bundles.androidx)

    implementation(libs.bundles.kotlin)

    // Compose
    implementation(libs.bundles.compose)
    debugImplementation(libs.compose.ui.tooling)

    // Coil
    implementation(libs.bundles.coil)

    // Hilt
    implementation(libs.bundles.hilt)
    ksp(libs.hilt.compiler)

    // Lifecycle
    implementation(libs.bundles.lifecycle)

    // Ktor
    implementation(libs.bundles.ktor)

    // Media3
    implementation(libs.bundles.media3)

    // Navigation
    implementation(libs.navigation.compose)

    // Paging
    implementation(libs.bundles.paging)

    // Room
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)

    // Work
    implementation(libs.work.runtime)

    // Others
    implementation(libs.crashreport)
    implementation(files("libs/commons-compress-1.19.jar"))
    implementation(files("libs/dec-0.1.2.jar"))
}
