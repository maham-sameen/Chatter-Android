import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)


    // kotlinx serialization
    alias(libs.plugins.kotlin.plugin.serialization)

    // google service
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example.chatter"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.chatter"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
            freeCompilerArgs.add("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
        }
    }
    buildFeatures {
        compose = true
    }
    // ✅ Correct place for resource exclusion
    packaging {
        resources {
            excludes += "META-INF/native-image/**"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/INDEX.LIST"
            excludes += "META-INF/LICENSE*"
            excludes += "META-INF/*.kotlin_module"
        }
    }


}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.lifecycle.process)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)




    //feature_contacts
    implementation(libs.country.picker.kmp)
    // CameraX
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    // Zxing
    implementation(libs.core)
    // phone validation
    implementation(libs.libphonenumber)





    // Koin core features
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.ktor)
    implementation(libs.koin.slf4j.logger)
    implementation(libs.koin.androidx.startup)
    implementation(libs.koin.compose.viewmodel.navigation)
    implementation(libs.koin.compose.viewmodel)


    // Room
    implementation (libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    // Kotlin Extensions and Coroutines support for Room
    implementation (libs.androidx.room.ktx)



    // firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.storage)



    // ktor
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.websockets)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.content.negotiation)



    // mongo db
    implementation(libs.mongodb.driver.kotlin.coroutine)


    // coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)


    // stream
    implementation(libs.bundles.stream)
    implementation(libs.stream.video.android.ui.compose)


    // app start up
    implementation(libs.androidx.startup.runtime)


    // stream sdk with firebase cloud messaging
    implementation(libs.stream.android.push.firebase)

    // preference datastore
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.serialization.protobuf)




    //tink
   // implementation(libs.tink)
    implementation(libs.tink.android)



    // worker
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.koin.androidx.workmanager)


    // for security means used for the hashing
    implementation(libs.commons.codec)



    //paging
    implementation(libs.paging.compose)
    implementation(libs.room.paging)

    // Add the dependency for the Firebase AI Logic library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation(libs.firebase.ai)

    implementation("com.google.android.material:material:1.12.0")

}
