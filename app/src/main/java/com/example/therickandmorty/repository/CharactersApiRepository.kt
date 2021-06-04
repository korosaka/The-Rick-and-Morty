package com.example.therickandmorty.repository

import com.example.therickandmorty.model.api.CharacterApi
import com.example.therickandmorty.model.Common
import com.example.therickandmorty.model.entity.CharactersApiEntity
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CharactersApiRepository {

    fun fetchCharactersApiUrl(): String? {
        return try {
            val response = executeAPI()
            response.body()!!.characters
        } catch (e: Exception) {
            println("error on fetchCharactersApiUrl: $e")
            null
        }
    }

    private fun executeAPI(): Response<CharactersApiEntity> {
        val service = restClient().create(CharacterApi::class.java)
        return service.fetchCharactersURL().execute()
    }

    private fun restClient(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Common.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}