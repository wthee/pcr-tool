plugins {
    id("com.android.application")
    id("kotlin-android")
    id("dagger.hilt.android.plugin")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("kotlinx-serialization")
}


hilt {
    enableAggregatingTask = true
}

val composeBom = "2024.02.01"
val composeCompilerVersion = "1.5.10"
val appVersionCode = 383
val appVersionName = "3.8.3"
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
    compileSdk = 34
    buildToolsVersion = "34.0.0"
    flavorDimensions += listOf("version")

    defaultConfig {
        applicationId = appId
        minSdk = 23
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = composeCompilerVersion
    }

}

dependencies {

    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${rootProject.extra["kotlinVersion"]}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    //compose BOM
    implementation(platform("androidx.compose:compose-bom:${composeBom}"))
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.ui:ui-util")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.material3:material3")

    //Accompanist
    val accompanistVersion = "0.34.0"
    implementation("com.google.accompanist:accompanist-navigation-material:$accompanistVersion")

    //Browser
    implementation("androidx.browser:browser:1.7.0")

    //Bugly
    implementation("com.tencent.bugly:crashreport:4.1.9.3")

    //Coil
    val coilVersion = "3.0.0-alpha04"
    implementation("io.coil-kt.coil3:coil-compose:$coilVersion")
    implementation("io.coil-kt.coil3:coil-network-ktor:$coilVersion")

    //datastore
    implementation("androidx.datastore:datastore-preferences:1.1.0-beta01")

    //Hilt
    implementation("com.google.dagger:hilt-android:${rootProject.extra["hiltVersion"]}")
    ksp("com.google.dagger:hilt-android-compiler:${rootProject.extra["hiltVersion"]}")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    //ktor
    val ktorVersion = "2.3.8"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-client-android:$ktorVersion")

    //Lifecycle
    val lifecycleVersion = "2.7.0"
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-service:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")

    //media3
    val media3Version = "1.2.1"
    implementation("androidx.media3:media3-exoplayer:$media3Version")
    implementation("androidx.media3:media3-ui:$media3Version")

    //Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

    //Paging3
    val pagingVersion = "3.2.1"
    implementation("androidx.paging:paging-runtime-ktx:$pagingVersion")
    implementation("androidx.paging:paging-compose:$pagingVersion")

    //Room
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    implementation("androidx.room:room-paging:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    //splashscreen
    implementation("androidx.core:core-splashscreen:1.0.1")

    //startup
    implementation("androidx.startup:startup-runtime:1.1.1")

    //palette 取色
    implementation("androidx.palette:palette-ktx:1.0.0")

    //Work
    val workVersion = "2.9.0"
    implementation("androidx.work:work-runtime:$workVersion")

    implementation(files("libs/commons-compress-1.19.jar"))
    implementation(files("libs/dec-0.1.2.jar"))

}