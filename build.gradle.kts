import com.soywiz.korge.gradle.*


val exposedVersion: String by project
val jdbcVersion : String by project

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.korge)
}

repositories {
    // Versions after 0.30.1
    // Versions before 0.30.1 is unavailable for now
    mavenCentral()
}

dependencies {
    //jvmImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.2")
    //jvmImplementation("androidx.media:media:1.6.0")
}

korge {
    id = "com.cucumbersaurus.the2048"
    name = "2048"
// To enable all targets at once

    //targetAll()

// To enable targets based on properties/environment variables
    //targetDefault()

// To selectively enable targets

    targetJvm()
    //targetJs()
    targetDesktop()
    //targetIos()
    //targetAndroidIndirect()
    //targetAndroidDirect()
}
