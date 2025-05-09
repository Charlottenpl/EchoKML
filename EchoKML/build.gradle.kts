import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.serialization)
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_1_8)
                }
            }
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "16.0"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "EchoKML"
            isStatic = true
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client.core) // Ktor 核心库
            implementation(libs.ktor.client.content.negotiation) // Ktor 序列化库
            implementation(libs.ktor.serialization.kotlinx.json) // Ktor 序列化库 Json 格式插件
            implementation(libs.ktor.client.logging) // Ktor 日志库
            implementation(libs.kotlinx.serialization.json) // 序列化库

        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)  // Android 引擎
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)  // iOS 引擎
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.echo.demo"
    compileSdk = 35
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}