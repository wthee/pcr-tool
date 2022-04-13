plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}


kapt {
    correctErrorTypes = true
}

hilt {
    enableAggregatingTask = true
}

val composeVersion = "1.2.0-alpha07"
val appVersionCode = 323
val appVersionName = "3.2.3"

android {

    compileSdk = 31
    buildToolsVersion = "31.0.0"
    flavorDimensions += listOf("version")

    defaultConfig {
        applicationId = "cn.wthee.pcrtool"
        minSdk = 23
        targetSdk = 30
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
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
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
            manifestPlaceholders["icon"] = "@mipmap/ic_launcher"
            resValue("string", "app_name", "PCR Tool")
            buildConfigField("boolean", "DEBUG", "false")
        }

        create("beta") {
            applicationId = "cn.wthee.pcrtoolbeta"
            dimension = "version"
            manifestPlaceholders["icon"] = "@drawable/ic_star_pink"
            resValue("string", "app_name", "PCR Tool β")
            buildConfigField("boolean", "DEBUG", "true")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += "-Xjvm-default=all"
        freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = composeVersion
    }

}

dependencies {
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("androidx.fragment:fragment-ktx:1.4.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${rootProject.extra["kotlinVersion"]}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")
    implementation("androidx.preference:preference-ktx:1.2.0")
    //compose
    implementation("androidx.compose.runtime:runtime:$composeVersion")
    implementation("androidx.compose.runtime:runtime-livedata:$composeVersion")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling:$composeVersion")
    implementation("androidx.compose.material3:material3:1.0.0-alpha09")
    implementation("androidx.compose.foundation:foundation:$composeVersion")
    implementation("androidx.compose.compiler:compiler:$composeVersion")
    //icon
    implementation("androidx.compose.material:material-icons-extended:$composeVersion")

    //Accompanist
    val accompanistVersion = "0.24.6-alpha"
    implementation("com.google.accompanist:accompanist-pager:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-pager-indicators:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-flowlayout:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-placeholder-material:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-navigation-animation:$accompanistVersion")

    //Coil
    val coilVersion = "2.0.0-rc02"
    implementation("io.coil-kt:coil-gif:$coilVersion")
    implementation("io.coil-kt:coil-compose:$coilVersion")

    //Hilt
    implementation("com.google.dagger:hilt-android:${rootProject.extra["hiltVersion"]}")
    kapt("com.google.dagger:hilt-android-compiler:${rootProject.extra["hiltVersion"]}")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    //Lifecycle
    val lifecycleVersion = "2.4.0"
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-service:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")

    //Navigation
    implementation("androidx.navigation:navigation-compose:2.4.1")

    //Paging3
    implementation("androidx.paging:paging-runtime-ktx:3.1.1")
    implementation("androidx.paging:paging-compose:1.0.0-alpha14")

    //Parse SDK
    val parseVersion = "3.0.0"
    implementation("com.github.parse-community.Parse-SDK-Android:parse:$parseVersion")
    // for Kotlin extensions support (optional)
    implementation("com.github.parse-community.Parse-SDK-Android:ktx:$parseVersion")
    // for Kotlin coroutines support (optional)
    implementation("com.github.parse-community.Parse-SDK-Android:coroutines:$parseVersion")

    //Retrofit
    val retrofitVersion = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")

    //Room
    val roomVersion = "2.4.0"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    implementation("androidx.room:room-paging:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    //startup
    implementation("androidx.startup:startup-runtime:1.1.1")

    //umeng
    implementation("com.umeng.umsdk:common:9.4.7")
    implementation("com.umeng.umsdk:asms:1.6.0")

    //Work
    val workVersion = "2.7.1"
    implementation("androidx.work:work-runtime-ktx:$workVersion")


    implementation(files("libs\\commons-compress-1.19.jar"))
    implementation(files("libs\\dec-0.1.2.jar"))

}