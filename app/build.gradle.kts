plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("dagger.hilt.android.plugin")
}

android {

    signingConfigs {
        create("release") {
            storeFile = file("../keystoredetails/release_keystore_certificate.jks")
            storePassword = "Mobility"
            keyPassword = "Mobility"
            keyAlias = "Mobility"
        }
    }


    namespace = "de.fast2work.mobility"
    compileSdk = 35

    defaultConfig {
        applicationId = "de.fast2work.mobility"
        minSdk = 24
        targetSdk = 34
        versionCode = 13
//        versionName = "1.58" //UAT
//        versionName = "1.101"//QA
        versionName = "1.7" //Live

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isDebuggable = true
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
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
        viewBinding = true
        dataBinding = true
    }

    flavorDimensions += "environment"
    productFlavors {
        create("staging") {
            buildConfigField("String", "BASE_URL", "\"http://127.0.0.1:8000/\"")
            dimension = "environment"
        }
        create("qa") {
            buildConfigField("String", "BASE_URL", "\"\"")
            dimension = "environment"
        }
        create("uat") {
            buildConfigField("String", "BASE_URL", "\"\"")
            dimension = "environment"
        }
        create("production") {
            buildConfigField("String", "BASE_URL", "\"\"")
            dimension = "environment"
        }
    }
    android.buildFeatures.buildConfig = true

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    kapt ("androidx.room:room-compiler:2.6.1")

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-process:2.7.0")
    implementation("androidx.activity:activity:1.8.0")
    implementation("com.android.volley:volley:1.2.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation ("androidx.navigation:navigation-ui-ktx:2.5.3")
    implementation("androidx.activity:activity-ktx:1.6.1")
//
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    implementation("androidx.recyclerview:recyclerview:1.3.1")

    implementation(platform("com.google.firebase:firebase-bom:32.8.1"))
    implementation("com.google.firebase:firebase-messaging-ktx")
//    implementation("com.google.firebase:firebase-analytics-ktx")
//    implementation("com.google.firebase:firebase-crashlytics-ktx")

    implementation ("androidx.core:core-splashscreen:1.0.0-beta02")


    //Event Bus(safe broadcast)
    implementation("org.greenrobot:eventbus:3.3.1")

    // Hb retrofit
    implementation("com.github.hbdevmdm:RetroAPI:2.0")

    //Fragment Navigation
    //implementation("com.github.ncapdevi:FragNav:3.3.0")

    //Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.2.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")

//    Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.14.2")
    kapt("com.github.bumptech.glide:compiler:4.14.2")

    implementation("com.google.code.gson:gson:2.10.1")

    //Permission
    implementation("com.github.pankaj89:PermissionHelper:2.3")

    implementation("de.hdodenhof:circleimageview:3.1.0")
//    implementation("com.makeramen:roundedimageview:2.3.0")

//    // Image Crop
    implementation("com.github.Yalantis:uCrop:2.2.8")


//    implementation("com.github.daimajia:AndroidSwipeLayout:v1.1.9")
    implementation("androidx.paging:paging-runtime-ktx:3.2.1")

    // RxJava2 Dependencies
    implementation("io.reactivex.rxjava2:rxjava:2.2.8")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")

    //OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")

    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")


    // Hilt dependencies
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.48")


    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    //lottie animation
    implementation ("com.airbnb.android:lottie:6.0.1")

    implementation ("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")
    //Glide for image loading
    implementation ("com.github.bumptech.glide:glide:4.14.2")



    implementation (project(":fancyflashbarlib"))

    // Lifecycles only (without ViewModel or LiveData)
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    // LiveData
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    // ViewModel
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")

    implementation("com.vanniktech:android-image-cropper:4.5.0")


    // document scan
    implementation ("com.github.zynkware:Document-Scanning-Android-SDK:1.1.1")

    // svg image load
    implementation("io.coil-kt:coil-svg:2.5.0")
    
    implementation ("com.vipulasri:ticketview:1.1.2")

    // d ticket sdk
    implementation ("com.github.themobilitybox:MobilityboxTicketingAndroidSDK:v7.2.0")

    implementation ("androidx.biometric:biometric:1.1.0")
}