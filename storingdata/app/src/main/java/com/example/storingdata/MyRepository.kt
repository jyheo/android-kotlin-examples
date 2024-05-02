package com.example.storingdata

import android.content.Context
import android.os.Environment
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import java.io.File

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class MyPrefKey {
    companion object {
        val name_key = stringPreferencesKey("name")
        val student_id_key = stringPreferencesKey("student_id")

    }
}
class MyRepository(private val context: Context) {
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

    val myPref : LiveData<Preferences> = context.dataStore.data.asLiveData()
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