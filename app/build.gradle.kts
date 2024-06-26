plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.kursproj"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.kursproj"
        minSdk = 24
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("androidx.room:room-runtime:2.4.0")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.0")
    implementation ("com.google.code.gson:gson:2.8.8")
    implementation ("com.fasterxml.jackson.core:jackson-databind:2.12.6")
    implementation ("com.google.android.material:material:1.4.0")
    implementation("com.google.firebase:firebase-crashlytics-buildtools:2.9.9")
    implementation(files("libs/MPAndroidChart-v3.0.1.jar"))
    implementation("androidx.work:work-runtime:2.9.0")
    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    annotationProcessor ("androidx.room:room-compiler:2.4.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}