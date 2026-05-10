package com.example.datastorage

import android.content.Context
import android.os.Environment
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import java.io.File

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
// androidx.datastore:datastore-preferences
class MyPrefKey {
    companion object {
        val name_key = stringPreferencesKey("name")
        val student_id_key = stringPreferencesKey("student_id")

    }
}

class MyRepository(private val context: Context) {
    private val myDao = MyDatabase.getDatabase(context).getMyDao()

    val allStudents = myDao.getAllStudentsFlow()

    suspend fun insertStudent(student: Student) = myDao.insertStudent(student)

    suspend fun getStudentsWithEnrollment(id: Int) = myDao.getStudentsWithEnrollment(id)

    suspend fun getClassInfo(id: Int) = myDao.getClassInfo(id)

    suspend fun initialSetup() {
        myDao.insertStudent(Student(1, "james"))
        myDao.insertStudent(Student(2, "john"))
        myDao.insertClass(ClassInfo(1, "c-lang", "Mon 9:00", "E301", 1))
        myDao.insertClass(ClassInfo(2, "android prog", "Tue 9:00", "E302", 1))
        myDao.insertEnrollment(Enrollment(1, 1))
        myDao.insertEnrollment(Enrollment(1, 2))
    }

    private val fileInternal = File(context.filesDir, "appfile1.txt")
    private val fileExternal =
        if (isExternalStorageMounted)
            File(context.getExternalFilesDir(null), "appfile2.txt")
        else
            fileInternal

    var valueInternal: String = readValue(fileInternal) // initialized by readValue
        set(v) {
            field = v
            writeValue(fileInternal, v) // write value whenever update the value
        }

    var valueExternal: String = readValue(fileExternal) // initialized by readValue
        set(v) {
            field = v
            writeValue(fileExternal, v)  // write value whenever update the value
        }

    val myPref : Flow<Preferences> = context.dataStore.data
    suspend fun setPref(key: Preferences.Key<String>, value: String) {
        context.dataStore.edit {
            it[key] = value
        }
    }

    private fun readValue(file: File) : String {
        return try {
            println("$file")
            // Internal Storage - /data/user/0/com.example.fileexample/files/appfile.txt
            // External Storage - /storage/emulated/0/Android/data/com.example.fileexample/files/appfile.txt
            file.readText(Charsets.UTF_8)
        } catch (e: Exception) {
            ""
        }
    }

    private fun writeValue(file: File, value: String) {
        file.writeText(value, Charsets.UTF_8)
    }

    private val isExternalStorageMounted: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return state == Environment.MEDIA_MOUNTED
        }
}