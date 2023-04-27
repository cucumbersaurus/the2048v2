import com.soywiz.korge.gradle.*

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.korge)
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
