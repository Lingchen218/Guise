plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
    //id("com.android.application") version "8.3.2"
    //id("org.jetbrains.kotlin.android") version "1.9.20"
    // id("org.jetbrains.kotlin.android") version "2.1.20-Beta1"
    //id("org.lsposed.lsplugin.resopt") version "1.1"
    // alias(libs.plugins.resopt)
   id("org.lsposed.lsplugin.apksign") version "1.4"
    id("org.lsposed.lsplugin.apktransform") version "1.2"
    id("org.lsposed.lsplugin.cmaker") version "1.2"
}
val appVerCode = 1
val appVerName: String by rootProject

//apksign {
//    storeFileProperty = "releaseStoreFile"
//    storePasswordProperty = "releaseStorePassword"
//    keyAliasProperty = "releaseKeyAlias"
//    keyPasswordProperty = "releaseKeyPassword"
//}
//apktransform {
//    copy {
//        when (it.buildType) {
//            "release" -> file("${it.name}/WeChatPad_${appVerName}.apk")
//            else -> null
//        }
//    }
//}
//
cmaker {
    default {
        targets("dexhelper")
        abiFilters("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        arguments += "-DANDROID_STL=none"
        cppFlags += "-Wno-c++2b-extensions"
    }

    buildTypes {
        arguments += "-DDEBUG_SYMBOLS_PATH=${layout.buildDirectory.file("symbols/${it.name}").get().asFile.absolutePath}"
    }
}
android {
    namespace = "com.houvven.guise.hook"
    compileSdk = 34
    buildToolsVersion = "34.0.0"
    ndkVersion = "25.1.8937393"
    defaultConfig {
        minSdk = 24
        consumerProguardFiles("consumer-rules.pro")
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
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.majorVersion
    }
    buildFeatures {
        buildConfig = true
        prefab = true
    }

    externalNativeBuild {
        cmake {
            path("src/main/jni/CMakeLists.txt")
            version = "3.22.1+"
        }
    }
}

dependencies {
    //implementation("dev.rikka.ndk.thirdparty:cxx:1.2.0")
    implementation(libs.cxx)
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.20")
    implementation(libs.androidx.core.ktx)

    implementation(libs.yuki.api)
    compileOnly(libs.xposed.api)
    ksp(libs.yuki.ksp.xposed)

    implementation(libs.kotlin.serialization.json)
    implementation(libs.betterandroid.extension.system)
}