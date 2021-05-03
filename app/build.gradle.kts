plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

val VERSION_NAME = "1.0.0"
val VERSION_CODE = 100

android {

    compileSdk = 30
    buildToolsVersion = "31.0.0-rc2"
    flavorDimensions("version")

    defaultConfig {
        applicationId = "cn.wthee.pcrtool"
        minSdk = 21
        targetSdk = 30
        versionCode = VERSION_CODE
        versionName = VERSION_NAME


        buildConfigField("int", "SQLITE_VERSION", VERSION_CODE.toString())

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
            manifestPlaceholders["icon"] = "@mipmap/ic_launcher"
            resValue("string", "app_name", "PCR Tool")
        }

        create("beta") {
            applicationId = "cn.wthee.pcrtoolbeta"
            dimension = "version"
            manifestPlaceholders["icon"] = "@drawable/ic_star"
            resValue("string", "app_name", "PCR Tool βeta")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        useIR = true
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = rootProject.extra["compose_version"] as String
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${rootProject.extra["kotlin_version"]}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.3")

    implementation("androidx.compose.runtime:runtime:${rootProject.extra["compose_version"]}")
    implementation("androidx.compose.runtime:runtime-livedata:${rootProject.extra["compose_version"]}")
    implementation("androidx.compose.ui:ui:${rootProject.extra["compose_version"]}")
    implementation("androidx.compose.ui:ui-tooling:${rootProject.extra["compose_version"]}")
    implementation("androidx.compose.material:material:${rootProject.extra["compose_version"]}")
    implementation("androidx.compose.foundation:foundation:${rootProject.extra["compose_version"]}")
    implementation("androidx.compose.compiler:compiler:${rootProject.extra["compose_version"]}")

    implementation("com.google.android.material:material:1.3.0")
    implementation("androidx.compose.material:material-icons-extended:${rootProject.extra["compose_version"]}")

    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.activity:activity-ktx:1.2.2")
    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.activity:activity-compose:1.3.0-alpha06")
    implementation("androidx.preference:preference-ktx:1.1.1")

    //Accompanist
    val accompanist_version = "0.8.1"
    implementation("com.google.accompanist:accompanist-coil:$accompanist_version")
    implementation("com.google.accompanist:accompanist-pager:$accompanist_version")
    implementation("com.google.accompanist:accompanist-pager-indicators:$accompanist_version")
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanist_version")
    implementation("com.google.accompanist:accompanist-flowlayout:$accompanist_version")
    //coil-gif
    implementation("io.coil-kt:coil-gif:1.2.0")

    //DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0-alpha08")

    //Hilt
    implementation("com.google.dagger:hilt-android:${rootProject.extra["hilt_version"]}")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0-alpha01")
    kapt("com.google.dagger:hilt-android-compiler:${rootProject.extra["hilt_version"]}")

    //Lifecycle
    val lifecycle_version = "2.3.1"
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha04")

    //Navigation
    implementation("androidx.navigation:navigation-compose:1.0.0-alpha10")

    //okio
    implementation("com.squareup.okio:okio:2.10.0")

    //Palette
    val palette_version = "1.0.0"
    implementation("androidx.palette:palette-ktx:$palette_version")

    //Paging3
    implementation("androidx.paging:paging-compose:1.0.0-alpha08")

    //Retrofit
    val retrofit_version = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofit_version")
    implementation("com.squareup.retrofit2:converter-gson:$retrofit_version")

    //Room
    val room_version = "2.3.0-rc01"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    kapt("androidx.room:room-compiler:$room_version")

    //startup
    implementation("androidx.startup:startup-runtime:1.0.0")

    //umeng
    implementation("com.umeng.umsdk:common:9.3.8")
    implementation("com.umeng.umsdk:asms:1.2.2")
    implementation("com.umeng.umsdk:apm:1.2.0")

    //Work
    val work_version = "2.7.0-alpha02"
    implementation("androidx.work:work-runtime-ktx:$work_version")


    implementation(files("libs\\commons-compress-1.19.jar"))
    implementation(files("libs\\dec-0.1.2.jar"))
    implementation(project(":Material-Calendar-View"))

}