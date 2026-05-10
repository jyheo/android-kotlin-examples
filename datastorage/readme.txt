make a new project in this folder.
The package name must be "com.example.datastorage"

-------------------------------
libs.versions.toml
-------------------------------
[versions]
room = "2.8.4"
ksp = "2.2.10-2.0.2"
datastorePreferences = "1.2.1"

[libraries]
...
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
androidx-room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
androidx-datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastorePreferences" }

[plugins]
...
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }


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
    implementation(libs.androidx.datastore.preferences)
	
    ksp(libs.androidx.room.compiler)

}
