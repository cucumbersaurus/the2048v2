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
    jvmImplementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    jvmImplementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    jvmImplementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    jvmImplementation("org.jetbrains.exposed:exposed-jodatime:$exposedVersion")
    jvmImplementation("org.xerial:sqlite-jdbc:$jdbcVersion")
    jvmImplementation("org.slf4j:slf4j-simple:1.7.25")
    //jvmImplementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
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
	//targetDesktop()
	//targetIos()
	//targetAndroidIndirect() // targetAndroidDirect()
}
