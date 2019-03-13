import de.codefor.karlsruhe.opensense.build.BuildConfig

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
}

val mapboxApiToken: String by project

android {
    compileSdkVersion(28)
    buildToolsVersion("28.0.3")
    defaultConfig {
        applicationId = "de.codefor.karlsruhe.opensense"
        minSdkVersion(19)
        targetSdkVersion(28)
        versionCode = 3
        versionName = "0.3.0"
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "MAPBOX_API_TOKEN", mapboxApiToken)
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${BuildConfig.kotlinVersion}")

    implementation("com.android.support:appcompat-v7:28.0.0")
    implementation("com.android.support:design:28.0.0")
    implementation("com.android.support:recyclerview-v7:28.0.0")

    implementation("net.danlew:android.joda:2.10.1.1")

    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation("io.reactivex.rxjava2:rxjava:2.2.7")

    implementation("com.squareup.retrofit2:retrofit:2.5.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.5.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.8.0")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.5.0")

    implementation("com.mapbox.mapboxsdk:mapbox-android-sdk:7.2.0")

    implementation("com.androidplot:androidplot-core:1.5.6")

    testImplementation("junit:junit:4.12")

    androidTestImplementation("com.android.support.test.espresso:espresso-core:3.0.1") {
        exclude(group = "com.android.support", module = "support-annotations")
    }
}