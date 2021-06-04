package com.example.therickandmorty.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.therickandmorty.model.Common
import com.example.therickandmorty.repository.CharactersApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CharacterListViewModel : ViewModel() {

    private val charactersApiRepository = CharactersApiRepository()

    init {
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

            // fetching character images
        }

    }
}