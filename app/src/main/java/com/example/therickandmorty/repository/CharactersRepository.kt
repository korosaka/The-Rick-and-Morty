package com.example.therickandmorty.repository

import com.example.therickandmorty.model.api.CharacterApi
import com.example.therickandmorty.model.character.CharacterHeadline
import com.example.therickandmorty.model.Common
import com.example.therickandmorty.model.entity.CharactersEntity
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CharactersRepository {

    fun fetchCharacters(urlStr: String): MutableList<CharacterHeadline>? {
        return try {
            val response = executeAPI(urlStr)
            return extractCharacters(response.body()!!)
        } catch (e: Exception) {
            println("error on fetchCharacters(urlStr: String): $e")
            null
        }
    }

    private fun extractCharacters(entity: CharactersEntity): MutableList<CharacterHeadline> {
        val characterList: MutableList<CharacterHeadline> = mutableListOf()

        for (characterEntity in entity.results) {
            val character =
                CharacterHeadline(
                    characterEntity.id,
                    characterEntity.name,
                    characterEntity.image,
                    null
                )
            characterList.add(character)
        }
        return characterList
    }

    private fun executeAPI(urlStr: String): Response<CharactersEntity> {
        val service = restClient().create(CharacterApi::class.java)
        return service.fetchCharacters(urlStr).execute()
    }

    private fun restClient(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Common.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}