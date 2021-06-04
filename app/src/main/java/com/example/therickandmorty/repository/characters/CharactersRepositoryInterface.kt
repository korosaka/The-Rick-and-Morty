package com.example.therickandmorty.repository.characters

import com.example.therickandmorty.model.character.CharacterHeadline

interface CharactersRepositoryInterface {
    fun fetchCharacters(urlStr: String): MutableList<CharacterHeadline>?
}