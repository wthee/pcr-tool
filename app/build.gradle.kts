plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

val composeVersion = "1.0.1"
val name = "2.0.1"
val code = 201
val sql = 201

android {

    compileSdk = 30
    buildToolsVersion = "31.0.0"
    flavorDimensions("version")

    defaultConfig {
        applicationId = "cn.wthee.pcrtool"
        minSdk = 21
        targetSdk = 30
        versionCode = code
        versionName = name


        buildConfigField("int", "SQLITE_VERSION", sql.toString())

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
            buildConfigField("boolean", "debug", "false")
        }

        create("beta") {
            applicationId = "cn.wthee.pcrtoolbeta"
            dimension = "version"
            manifestPlaceholders["icon"] = "@drawable/ic_star"
            resValue("string", "app_name", "PCR Tool βeta")
            buildConfigField("boolean", "debug", "true")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = composeVersion
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.fragment:fragment:1.3.6")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${rootProject.extra["kotlin_version"]}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.1")
    implementation("androidx.preference:preference-ktx:1.1.1")
    //compose
    implementation("androidx.compose.runtime:runtime:$composeVersion")
    implementation("androidx.compose.runtime:runtime-livedata:$composeVersion")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.foundation:foundation:$composeVersion")
    implementation("androidx.compose.compiler:compiler:$composeVersion")
    //icon
    implementation("androidx.compose.material:material-icons-extended:$composeVersion")

    //Accompanist
    val accompanistVersion = "0.17.0"
    implementation("com.google.accompanist:accompanist-pager:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-pager-indicators:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-flowlayout:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-insets:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-placeholder-material:$accompanistVersion")

    //Coil
    val coilVersion = "1.3.2"
    implementation("io.coil-kt:coil-gif:$coilVersion")
    implementation("io.coil-kt:coil-compose:$coilVersion")

    //Hilt
    implementation("com.google.dagger:hilt-android:${rootProject.extra["hilt_version"]}")
    kapt("com.google.dagger:hilt-android-compiler:${rootProject.extra["hilt_version"]}")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0-alpha03")

    //Lifecycle
    val lifecycleVersion = "2.4.0-alpha02"
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-service:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha07")

    //Navigation
    implementation("androidx.navigation:navigation-compose:2.4.0-alpha05")

    //Paging3
    implementation("androidx.paging:paging-runtime:3.0.0")
    implementation("androidx.paging:paging-compose:1.0.0-alpha12")

    //Retrofit
    val retrofitVersion = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")

    //Room
    val roomVersion = "2.4.0-alpha04"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    implementation("androidx.room:room-paging:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    //startup
    implementation("androidx.startup:startup-runtime:1.0.0")

    //umeng
    implementation("com.umeng.umsdk:common:9.4.2")
    implementation("com.umeng.umsdk:asms:1.4.0")
    implementation("com.umeng.umsdk:apm:1.4.2")

    //Work
    val workVersion = "2.7.0-alpha05"
    implementation("androidx.work:work-runtime-ktx:$workVersion")


    implementation(files("libs\\commons-compress-1.19.jar"))
    implementation(files("libs\\dec-0.1.2.jar"))

}