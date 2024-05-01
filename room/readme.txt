make a new project with the package name, com.example.room
copy these files (app) to the new project.

--------------------------------
build.gradle.kts (in project)
--------------------------------
plugins {
	...
	id("com.google.devtools.ksp") version "1.9.0-1.0.11" apply false
}

-------------------------------
build.gradle.kst (in module)
-------------------------------
plugins {
	...
	id("com.google.devtools.ksp")
}

dependencies {
	...
    val roomVersion = "2.5.2"
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    implementation("androidx.room:room-ktx:$roomVersion")
    testImplementation("androidx.room:room-testing:$roomVersion")
}