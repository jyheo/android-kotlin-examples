package com.example.internet

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.lifecycle.*
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.lang.StringBuilder
import java.net.URL
import javax.net.SocketFactory
import javax.net.ssl.HttpsURLConnection

data class Owner(val login: String)
data class Repo(val name: String, val owner: Owner, val url: String)
data class Contributor(val login: String, val contributions: Int)

interface RestApi {
    @GET("users/{user}/repos")
    suspend fun listRepos(@Path("user") user: String): List<Repo>
    /*
    [
        {
            "id": 74595421,
            "node_id": "MDEwOlJlcG9zaXRvcnk3NDU5NTQyMQ==",
            "name": "2ndProject",
            "full_name": "jyheo/2ndProject",
            "private": false,
            "owner": {
                "login": "jyheo",
                "id": 4907532,
                 ... 생략 ...
            },
            "html_url": "https://github.com/jyheo/2ndProject",
            "description": null,
            "fork": true,
            "url": "https://api.github.com/repos/jyheo/2ndProject",
            ... 생략 ...
        },
        {
            ... 생략 ...
        },
        ... 생략 ...
    ]
    */

    @GET("/repos/{owner}/{repo}/contributors")
    suspend fun contributors(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): List<Contributor>
}

class MyViewModel(private val context: Context) : ViewModel() {
    private val baseURL = "https://api.github.com/"
    private val api: RestApi = with(Retrofit.Builder()) {
        baseUrl(baseURL)
        addConverterFactory(GsonConverterFactory.create())
        build()
    }.create(RestApi::class.java)

    val response = MutableLiveData<String>()
    val responseBy = MutableLiveData<String>()
    val responseImg = MutableLiveData<Bitmap?>()

    fun refreshRestrofit(userName: String) {
        viewModelScope.launch {
            try {
                //val c = api.contributors("square", "retrofit")
                val repos = api.listRepos(userName)
                responseBy.value = "retrofit : ${repos.size} repositories"
                response.value = StringBuilder().apply {
                    repos.forEach {
                        append(it.name)
                        append(" - ")
                        append(it.owner.login)
                        append("\n")
                    }
                }.toString()
            } catch (e: Exception) {
                responseBy.value = "retrofit : Error"
                response.value = "Failed to connect to the server ${e.message}"
            }
        }
    }

    fun refreshJavaSocket() {
        viewModelScope.launch {
            try {
                val sock = withContext(Dispatchers.IO) {
                    SocketFactory.getDefault().createSocket("google.com", 80)
                }
                val r = withContext(Dispatchers.IO) {
                    //val sock = Socket("naver.com", 80)
                    val istream = sock.getInputStream()
                    val ostream = sock.getOutputStream()
                    ostream.write("GET / \r\n".toByteArray())
                    ostream.flush()
                    istream.readBytes()
                }
                responseBy.value = "Java Socket : Succeeded to connect to the server"
                response.value = r.decodeToString()
                withContext(Dispatchers.IO) {
                    sock.close()
                }
            } catch (e: Exception) {
                responseBy.value = "Java Socket"
                response.value = "Failed to connect to the server ${e.message}"
            }
        }

    }

    fun refreshBitmapByHttpsLib(url: String) {
        viewModelScope.launch {
            val conn = withContext(Dispatchers.IO) {
                URL(url).openConnection() as HttpsURLConnection
            }
            val rImg = withContext(Dispatchers.IO) {
                val istream = conn.inputStream
                BitmapFactory.decodeStream(istream)
            }
            responseBy.value = "HttpsURLConnection"
            response.value = "Succeeded to download the image"
            responseImg.value = rImg
            withContext(Dispatchers.IO) {
                conn.disconnect()
            }
        }
        // https://developer.android.com/reference/java/net/HttpURLConnection
    }

    fun refreshBitmapByVolley(url: String) {
        val queue = Volley.newRequestQueue(context)

        // Request a image response from the provided URL.
        val imageRequest = ImageRequest(
            url,
            { res ->
                responseBy.value = "Volley"
                response.value = "Succeeded to download the image."
                responseImg.value = res  // set bitmap to LiveData
            },
            0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565, {
                responseBy.value = "Volley"
                response.value = "Failed to download the image."
                responseImg.value = null
            }
        )
        queue.add(imageRequest)

        // for String response, you can use:
        // val stringRequest = StringRequest(url, responseListener, errorListener)
        // queue.add(stringRequest)
    }
}

class MyViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MyViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            MyViewModel(context) as T
        else
            throw IllegalArgumentException()
    }
}
