import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
    id("de.mannodermaus.android-junit5")
}

android {
    compileSdkVersion(29)
    buildToolsVersion = "29.0.3"

    defaultConfig {
        applicationId = "com.mfeldsztejn.ringtest"
        minSdkVersion(21)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    testBuildType = "debug"

    sourceSets {
        getByName("test") {
            java.srcDirs("$projectDir/src/testShared/java")
        }
        getByName("androidTest") {
            java.srcDirs("$projectDir/src/testShared/java")
        }
    }

    packagingOptions {
        exclude("META-INF/*")
    }
}

val roomVersion = "2.3.0-alpha01"
val koinVersion = "2.1.5"
val navigationVersion = "2.3.0-rc01"
val glideVersion = "4.11.0"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${KotlinCompilerVersion.VERSION}")
    implementation("androidx.core:core-ktx:1.3.0")
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("com.google.android.material:material:1.3.0-alpha01")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("androidx.fragment:fragment-ktx:1.3.0-alpha06")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")
    implementation("androidx.palette:palette-ktx:1.0.0")
    implementation("androidx.preference:preference-ktx:1.1.1")
    implementation("jp.wasabeef:recyclerview-animators:3.0.0")

    // DI
    implementation("org.koin:koin-android:$koinVersion")
    implementation("org.koin:koin-androidx-viewmodel:$koinVersion")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")

    // Paging
    implementation("androidx.paging:paging-runtime:3.0.0-alpha02")

    // Async
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.7")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.7.2")

    // Room
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    // Glide
    implementation("com.github.bumptech.glide:glide:$glideVersion")
    kapt("com.github.bumptech.glide:compiler:$glideVersion")

    debugImplementation("androidx.test.espresso:espresso-idling-resource:3.2.0")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
    testImplementation("io.mockk:mockk:1.10.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.3.7")
    testImplementation("androidx.paging:paging-common:3.0.0-SNAPSHOT")
    testImplementation("com.willowtreeapps.assertk:assertk:0.21")

    androidTestImplementation("androidx.test:rules:1.3.0-rc01")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.3.7")
    androidTestImplementation("io.mockk:mockk-android:1.10.0")
    androidTestImplementation("androidx.test:runner:1.2.0")
    androidTestImplementation("androidx.test:core:1.2.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.2.0")
    androidTestImplementation("org.koin:koin-test:$koinVersion") {
        // koin includes mockito and we use mockk
        exclude("org.mockito")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}