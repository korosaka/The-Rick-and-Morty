package com.example.therickandmorty.model

import com.example.therickandmorty.model.entity.CharactersApiEntity
import retrofit2.Call
import retrofit2.http.GET

interface CharacterApi {
    @GET("api")
    fun fetchCharactersURL(): Call<CharactersApiEntity>
}