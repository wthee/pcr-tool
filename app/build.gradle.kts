plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

val name = "1.0.0"
val code = 100

android {

    compileSdk = 30
    buildToolsVersion = "31.0.0-rc4"
    flavorDimensions("version")

    defaultConfig {
        applicationId = "cn.wthee.pcrtool"
        minSdk = 21
        targetSdk = 30
        versionCode = code
        versionName = name


        buildConfigField("int", "SQLITE_VERSION", code.toString())

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
    //kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${rootProject.extra["kotlin_version"]}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0-RC-native-mt")
    //compose
    implementation("androidx.compose.runtime:runtime:${rootProject.extra["compose_version"]}")
    implementation("androidx.compose.runtime:runtime-livedata:${rootProject.extra["compose_version"]}")
    implementation("androidx.compose.ui:ui:${rootProject.extra["compose_version"]}")
    implementation("androidx.compose.ui:ui-tooling:${rootProject.extra["compose_version"]}")
    implementation("androidx.compose.material:material:${rootProject.extra["compose_version"]}")
    implementation("androidx.compose.foundation:foundation:${rootProject.extra["compose_version"]}")
    implementation("androidx.compose.compiler:compiler:${rootProject.extra["compose_version"]}")
    //material
    implementation("com.google.android.material:material:1.3.0")
    implementation("androidx.compose.material:material-icons-extended:${rootProject.extra["compose_version"]}")
    //ktx
//    implementation("androidx.activity:activity-compose:1.3.0-alpha08")
    implementation("androidx.preference:preference-ktx:1.1.1")

    //Accompanist
    val accompanistVersion = "0.10.0"
    implementation("com.google.accompanist:accompanist-coil:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-pager:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-pager-indicators:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-flowlayout:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-insets:$accompanistVersion")
    //coil-gif
    implementation("io.coil-kt:coil-gif:1.2.1")

    //DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0-beta01")

    //Hilt
    implementation("com.google.dagger:hilt-android:${rootProject.extra["hilt_version"]}")
    kapt("com.google.dagger:hilt-android-compiler:${rootProject.extra["hilt_version"]}")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0-alpha02")

    //Lifecycle
    val lifecycleVersion = "2.3.1"
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha05")

    //Navigation
    implementation("androidx.navigation:navigation-compose:2.4.0-alpha01")

    //okio
    implementation("com.squareup.okio:okio:2.10.0")

    //Paging3
    implementation("androidx.paging:paging-runtime:3.0.0")
    implementation("androidx.paging:paging-compose:1.0.0-alpha09")

    //Retrofit
    val retrofitVersion = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")

    //Room
    val roomVersion = "2.3.0-rc01"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    //startup
    implementation("androidx.startup:startup-runtime:1.0.0")

    //umeng
    implementation("com.umeng.umsdk:common:9.3.8")
    implementation("com.umeng.umsdk:asms:1.2.3")
    implementation("com.umeng.umsdk:apm:1.2.0")

    //Work
    val workVersion = "2.7.0-alpha03"
    implementation("androidx.work:work-runtime-ktx:$workVersion")


    implementation(files("libs\\commons-compress-1.19.jar"))
    implementation(files("libs\\dec-0.1.2.jar"))

}