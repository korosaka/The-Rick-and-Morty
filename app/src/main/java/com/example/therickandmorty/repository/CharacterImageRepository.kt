package com.example.therickandmorty.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.net.HttpURLConnection
import java.net.URL

class CharacterImageRepository {
    fun fetchImage(urlStr: String): Bitmap? {
        var image: Bitmap? = null
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL(urlStr)
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.readTimeout = 10000
            urlConnection.connectTimeout = 20000
            urlConnection.requestMethod = "GET"
            urlConnection.instanceFollowRedirects = false
            urlConnection.setRequestProperty("Accept-Language", "en")

            urlConnection.connect()
            val inst = urlConnection.inputStream
            val bitmap = BitmapFactory.decodeStream(inst)
            image = bitmap
            inst.close()
        } catch (e: Exception) {
            println("test: $e")
        } finally {
            urlConnection?.disconnect()
            return image
        }
    }
}