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
        classpath("de.mannodermaus.gradle.plugins:android-junit5:1.6.2.0")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
}