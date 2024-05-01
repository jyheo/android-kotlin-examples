make a new project in this folder.
The package name must be "com.example.room"

-------------------------------
libs.versions.toml
-------------------------------
[versions]
kotlin = "1.9.21"
ksp = "1.9.21-1.0.15"
room = "2.6.1"

[libraries]
...
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
androidx-room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
androidx-room-testing = { group = "androidx.room", name = "room-testing", version.ref = "room" }

[plugins]
...
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }

--------------------------------
build.gradle.kts (in project)
--------------------------------
plugins {
	...
	alias(libs.plugins.ksp) apply false
}

-------------------------------
build.gradle.kst (in module)
-------------------------------
plugins {
	...
	alias(libs.plugins.ksp)
}

dependencies {
	...
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)

    ksp(libs.androidx.room.compiler)
	
	testImplementation(libs.androidx.room.testing)
}

