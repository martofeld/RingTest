// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.0.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.72")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven { setUrl("https://androidx.dev/snapshots/builds/6613339/artifacts/repository") }
    }
}