import de.codefor.karlsruhe.opensense.build.BuildConfig

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
}

val mapboxApiToken: String by project

android {
    compileSdkVersion(BuildConfig.compileSdkVersion)
    buildToolsVersion(BuildConfig.buildToolsVersion)
    defaultConfig {
        applicationId = "de.codefor.karlsruhe.opensense"
        minSdkVersion(BuildConfig.minSdkVersion)
        targetSdkVersion(BuildConfig.targetSdkVersion)
        versionCode = BuildConfig.versionCode
        versionName = BuildConfig.versionName
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

    implementation("com.android.support:appcompat-v7:${BuildConfig.supportLibVersion}")
    implementation("com.android.support:design:${BuildConfig.supportLibVersion}")
    implementation("com.android.support:recyclerview-v7:${BuildConfig.supportLibVersion}")

    implementation("net.danlew:android.joda:${BuildConfig.jodaVersion}")

    implementation("io.reactivex.rxjava2:rxandroid:${BuildConfig.rxAndroidVersion}")
    implementation("io.reactivex.rxjava2:rxjava:${BuildConfig.rxJavaVersion}")

    implementation("com.squareup.retrofit2:retrofit:${BuildConfig.retrofitVersion}")
    implementation("com.squareup.retrofit2:converter-moshi:${BuildConfig.retrofitVersion}")
    implementation("com.squareup.moshi:moshi-kotlin:1.8.0")
    implementation("com.squareup.retrofit2:adapter-rxjava2:${BuildConfig.retrofitVersion}")

    implementation("com.mapbox.mapboxsdk:mapbox-android-sdk:${BuildConfig.mapboxVersion}")

    implementation("com.androidplot:androidplot-core:${BuildConfig.androidPlotVersion}")

    testImplementation("junit:junit:${BuildConfig.junitVersion}")

    androidTestImplementation("com.android.support.test.espresso:espresso-core:${BuildConfig.espressoVersion}") {
        exclude(group = "com.android.support", module = "support-annotations")
    }
}