import org.jetbrains.kotlin.fir.declarations.builder.buildScript

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    id("kotlin-kapt")
}

android {
    namespace = "com.example.store_application"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.store_application"
        minSdk = 24
        targetSdk = 34
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{
        viewBinding = true
    }




}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.play.services.base)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.functions)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.circleimageview)
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation(libs.androidx.constraintlayout)
    implementation("me.relex:circleindicator:2.1.6")
    implementation("com.tbuonomo:dotsindicator:4.3")
    implementation("com.hbb20:ccp:2.7.0")
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-database")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.android.volley:volley:1.2.1")
    implementation("com.stripe:stripe-android:20.53.0")
    implementation("com.squareup.picasso:picasso:2.8")
    implementation("com.googlecode.libphonenumber:libphonenumber:8.12.52")
    implementation ("androidx.appcompat:appcompat:1.1.0")
    implementation ("androidx.activity:activity-ktx:1.1.0")


    // Room components
    implementation ("androidx.room:room-ktx:2.6.1")
    kapt ("androidx.room:room-compiler:2.6.1")
    androidTestImplementation ("androidx.room:room-testing:2.6.1")

    // Lifecycle components
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    implementation ("androidx.lifecycle:lifecycle-common-java8:$2.8.7")

}