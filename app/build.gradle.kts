plugins {
    id("com.android.application")
    id("kotlin-android")
    id("dagger.hilt.android.plugin")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}


hilt {
    enableAggregatingTask = true
}

val composeBom = "2023.10.01"
val composeCompilerVersion = "1.5.3"
val appVersionCode = 374
val appVersionName = "3.7.4"
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
    }

    composeOptions {
        kotlinCompilerExtensionVersion = composeCompilerVersion
    }

}

dependencies {

    implementation("androidx.multidex:multidex:2.0.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.fragment:fragment-ktx:1.6.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${rootProject.extra["kotlinVersion"]}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.preference:preference-ktx:1.2.0")

    //compose BOM
    implementation(platform("androidx.compose:compose-bom:${composeBom}"))
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.compose.material3:material3")

    //Accompanist
    val accompanistVersion = "0.32.0"
    implementation("com.google.accompanist:accompanist-pager:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-pager-indicators:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-flowlayout:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-placeholder-material:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-navigation-animation:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-navigation-material:$accompanistVersion")

    //Browser
    implementation("androidx.browser:browser:1.6.0")

    //Bugly
    implementation("com.tencent.bugly:crashreport:4.1.9.2")

    //Coil
    val coilVersion = "2.4.0"
    implementation("io.coil-kt:coil-compose:$coilVersion")

    //datastore
    implementation("androidx.datastore:datastore-preferences:1.1.0-alpha05")

    //Hilt
    implementation("com.google.dagger:hilt-android:${rootProject.extra["hiltVersion"]}")
    ksp("com.google.dagger:hilt-android-compiler:${rootProject.extra["hiltVersion"]}")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0-rc01")

    //Lifecycle
    val lifecycleVersion = "2.6.2"
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-service:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")

    //Navigation
    implementation("androidx.navigation:navigation-compose:2.7.4")

    //Paging3
    val pagingVersion = "3.2.1"
    implementation("androidx.paging:paging-runtime-ktx:$pagingVersion")
    implementation("androidx.paging:paging-compose:$pagingVersion")

    //Retrofit
    val retrofitVersion = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")

    //Room
    val roomVersion = "2.6.0"
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
    val workVersion = "2.8.1"
    implementation("androidx.work:work-runtime-ktx:$workVersion")

    implementation(files("libs/commons-compress-1.19.jar"))
    implementation(files("libs/dec-0.1.2.jar"))

}