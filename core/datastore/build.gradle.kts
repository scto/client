plugins {
	id("looker.android.library")
	id("looker.hilt.work")
}

android {
	namespace = "com.looker.core.datastore"
	compileSdk = Android.compileSdk
	defaultConfig.minSdk = Android.minSdk

	buildTypes {
		release {
			isMinifyEnabled = true
			proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
		}
		create("alpha") {
			initWith(getByName("debug"))
			isMinifyEnabled = true
		}
	}
	kotlin.jvmToolchain(17)
	buildFeatures {
		buildConfig = false
		aidl = false
		renderScript = false
		shaders = false
		resValues = false
	}
}

dependencies {
	modules(Modules.coreCommon)
	coroutines()
	implementation(Datastore.datastore)
}