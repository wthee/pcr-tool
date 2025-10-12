plugins {
    id(libs.plugins.android.get().pluginId)
    id(libs.plugins.application.get().pluginId)
    id(libs.plugins.hilt.get().pluginId)
    id(libs.plugins.ksp.get().pluginId)
    id(libs.plugins.compose.get().pluginId)
    id(libs.plugins.serialization.get().pluginId)
}


hilt {
    enableAggregatingTask = true
}

val appVersionCode = 391
val appVersionName = "3.9.1"
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

    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.multidex)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.android)

    //compose unstable
    implementation(libs.compose.animation)
    implementation(libs.compose.material)
    implementation(libs.compose.material.navigation)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.util)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.runtime.livedata)
    implementation(libs.compose.material3)

    //Browser
    implementation(libs.browser)

    //Bugly
    implementation(libs.crashreport)

    //Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.ktor3)

    //datastore
    implementation(libs.datastore.preferences)

    //Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    //ktor
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.android)

    //Lifecycle
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.service)
    implementation(libs.lifecycle.viewmodel.compose)

    //media3
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.datasource)
    implementation(libs.media3.ui)

    //Navigation
    implementation(libs.navigation.compose)

    //Paging3
    implementation(libs.paging.runtime.ktx)
    implementation(libs.paging.compose)

    //Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.room.paging)
    ksp(libs.room.compiler)

    //splashscreen
    implementation(libs.splashscreen)

    //startup
    implementation(libs.startup.runtime)

    //palette 取色
    implementation(libs.palette.ktx)

    //Work
    implementation(libs.work.runtime)

    implementation(files("libs/commons-compress-1.19.jar"))
    implementation(files("libs/dec-0.1.2.jar"))

}