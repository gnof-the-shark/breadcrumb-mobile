kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    // --- AJOUT POUR IOS ---
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    // -----------------------
    
    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.gpx.parser)
            implementation(libs.garmin.ciq.sdk)
            implementation(libs.ktor.client.cio.jvm)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.ktor.client.auth)
            implementation(libs.androidx.fragment)
        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.no.arg)
            implementation(libs.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.reorderable)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.navigation)
            implementation(libs.ktor.server.netty)
            implementation(libs.ktor.server.core)
            implementation(libs.ktor.server.resources)
            implementation(libs.ktor.server.call.logging)
            implementation(libs.ktor.server.status.pages)
            implementation(libs.ktor.server.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.resources)
            implementation(libs.logback.classic)
            implementation(libs.napier)
            implementation(libs.uuid)
            implementation(libs.skiko.core)
            implementation(libs.play.services.location)
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
        }

        // --- AJOUT POUR LES SOURCES IOS ---
        val iosMain by creating {
            dependsOn(commonMain.get())
        }
    }
}
