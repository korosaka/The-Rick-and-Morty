package com.example.therickandmorty.repository

import com.example.therickandmorty.model.Common
import com.example.therickandmorty.model.api.CharacterApi
import com.example.therickandmorty.model.character.CharacterDetail
import com.example.therickandmorty.model.entity.CharacterDetailEntity
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CharacterDetailRepository {

    fun fetchCharacterDetail(characterId: String): CharacterDetail? {
        return try {
            val response = executeAPI(characterId)
            extractCharacterDetail(response.body()!!)
        } catch (e: Exception) {
            println("error on fetchCharacterDetail: $e")
            null
        }
    }

    private fun extractCharacterDetail(entity: CharacterDetailEntity): CharacterDetail {
        return CharacterDetail(
            entity.name,
            entity.status,
            entity.species,
            entity.type,
            entity.gender,
            entity.image,
            null
        )
    }

    private fun executeAPI(characterId: String): Response<CharacterDetailEntity> {
        val service = restClient().create(CharacterApi::class.java)
        val apiUrlStr = Common.charactersApiUrl + "/" + characterId
        return service.fetchCharacterDetail(apiUrlStr).execute()
    }

    private fun restClient(): Retrofit {
        /**
         * even when a whole URL is passed to the api function, baseUrl must be set
         */
        return Retrofit.Builder()
            .baseUrl(Common.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}