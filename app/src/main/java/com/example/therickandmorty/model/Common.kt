package com.example.therickandmorty.model

class Common {
    companion object {
        var charactersApiUrl: String? = null
        var dataType = DataType.PRODUCT
        const val BASE_URL = "https://rickandmortyapi.com/"

        const val EMPTY_STRING = ""
        const val CHARACTER_ID_KEY = "character_id"
    }
}