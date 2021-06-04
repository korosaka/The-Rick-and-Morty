package com.example.therickandmorty.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.therickandmorty.model.Common
import com.example.therickandmorty.model.character.CharacterHeadline
import com.example.therickandmorty.repository.CharactersApiRepository
import com.example.therickandmorty.repository.CharactersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CharacterListViewModel : ViewModel() {

    private val charactersApiRepository = CharactersApiRepository()
    private val charactersRepository = CharactersRepository()

    private var characters: MutableList<CharacterHeadline> = mutableListOf()
    var liveCharacters: MutableLiveData<MutableList<CharacterHeadline>> = MutableLiveData()

    init {
        liveCharacters = MutableLiveData(characters)
        fetchCharacters()
    }

    private fun fetchCharacters() {

        viewModelScope.launch(Dispatchers.IO) {

            // fetching characters API URL
            val charactersApi =
                charactersApiRepository.fetchCharactersApiUrl()
                    ?: return@launch //TODO error handling
            Common.charactersApiUrl = charactersApi // to use in other Activity

            // fetching characters data(except image)
            characters = charactersRepository.fetchCharacters(charactersApi) ?: return@launch //TODO error handling
            viewModelScope.launch(Dispatchers.Main) {
                liveCharacters.value = characters
            }

            // fetching character images
        }

    }
}