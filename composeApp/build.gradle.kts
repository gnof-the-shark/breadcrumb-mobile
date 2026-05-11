import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.concurrent.TimeUnit

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.serialization)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.androidx.room)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    // --- CONFIGURATION IOS ---
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                // Compose Core
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                
                // Utilitaires Multiplatform
                implementation(libs.multiplatform.settings)
                implementation(libs.multiplatform.settings.no.arg)
                implementation(libs.serialization.json)
                implementation(libs.kotlinx.datetime)
                implementation(libs.reorderable)
                implementation(libs.napier)
                implementation(libs.uuid)
                
                // Ktor Client (Cœur commun)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.resources)
                
                // Base de données Room
                implementation(libs.androidx.room.runtime)
                implementation(libs.androidx.sqlite.bundled)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(compose.preview)
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.navigation)
                implementation(libs.androidx.fragment)
                
                // Bibliothèques Android uniquement (JVM)
                implementation(libs.play.services.location)
                implementation(libs.gpx.parser)
                implementation(libs.garmin.ciq.sdk)
                implementation(libs.logback.classic)
                
                // Serveur Ktor (Ne fonctionne pas sur iOS)
                implementation(libs.ktor.server.netty)
                implementation(libs.ktor.server.core)
                implementation(libs.ktor.server.call.logging)
                implementation(libs.ktor.server.status.pages)
                implementation(libs.ktor.server.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)

                // Moteurs de client spécifiques Android
                implementation(libs.ktor.client.cio.jvm)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.ktor.client.auth)
            }
        }

        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                // Moteur Ktor spécifique à iOS (nécessaire pour le réseau sur iPhone)
                implementation("io.ktor:ktor-client-darwin:3.1.1")
            }
        }
    }
}

dependencies {
    // KSP pour Room (Génération de code)
    add("kspCommonMainMetadata", libs.androidx.room.compiler)
    add("kspAndroid", libs.androidx.room.compiler)
    ksp(libs.androidx.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}

// Fonction pour récupérer les infos Git (utilisée dans la config Android)
fun executeCommand(command: String, workingDir: File = rootProject.projectDir, fallbackValue: String = ""): String {
    return try {
        val parts = command.split("\\s".toRegex())
        val proc = ProcessBuilder(*parts.toTypedArray())
            .directory(workingDir)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()

        proc.waitFor(5, TimeUnit.SECONDS)
        val output = proc.inputStream.bufferedReader().readText().trim()
        if (proc.exitValue() != 0) fallbackValue else output
    } catch (e: Exception) {
        fallbackValue
    }
}

val gitCommitCount = executeCommand("git rev-list --count HEAD", fallbackValue = "1").toIntOrNull() ?: 1
val gitVersionName = executeCommand("git describe --tags --dirty --always", fallbackValue = "0.1.0-SNAPSHOT")

android {
    namespace = "com.paul"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "com.paul.breadcrumb"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = gitCommitCount
        versionName = gitVersionName
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/INDEX.LIST"
            excludes += "/META-INF/io.netty.versions.properties"
        }
    }
    
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            manifestPlaceholders["appIcon"] = "@mipmap/iconlarge"
            manifestPlaceholders["appIconRound"] = "@mipmap/iconlarge"
        }
        debug {
            applicationIdSuffix = ".debug"
            manifestPlaceholders["appIcon"] = "@mipmap/iconlargedebug"
            manifestPlaceholders["appIconRound"] = "@mipmap/iconlargedebug"
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
    buildFeatures {
        compose = true
        buildConfig = true
    }
}
