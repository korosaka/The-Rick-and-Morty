package com.example.therickandmorty.model.api

import com.example.therickandmorty.model.entity.CharactersApiEntity
import com.example.therickandmorty.model.entity.CharactersEntity
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface CharacterApi {
    @GET("api")
    fun fetchCharactersURL(): Call<CharactersApiEntity>

    @GET
    fun fetchCharacters(@Url charactersUrlStr: String): Call<CharactersEntity>
}