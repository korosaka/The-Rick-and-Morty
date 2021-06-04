package com.example.therickandmorty.repository.characters

import com.example.therickandmorty.model.character.CharacterHeadline

class DummyCharactersRepository : CharactersRepositoryInterface {

    override fun fetchCharacters(urlStr: String): MutableList<CharacterHeadline>? {
        val dummyList: MutableList<CharacterHeadline> = mutableListOf()
        for (i in 0..500) {
            dummyList.add(createRandomCharacter())
        }
        return dummyList
    }

    private fun createRandomCharacter(): CharacterHeadline {
        val id = (1..20).random().toString()
        return CharacterHeadline(
            id,
            "Test Character (ID: $id)",
            "https://rickandmortyapi.com/api/character/avatar/$id.jpeg",
            null
        )
    }


}