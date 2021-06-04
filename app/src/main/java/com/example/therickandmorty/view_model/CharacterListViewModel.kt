package com.example.therickandmorty.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.therickandmorty.model.Common
import com.example.therickandmorty.model.character.CharacterHeadline
import com.example.therickandmorty.repository.CharacterImageRepository
import com.example.therickandmorty.repository.CharactersApiRepository
import com.example.therickandmorty.repository.CharactersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CharacterListViewModel : ViewModel() {

    private val charactersApiRepository = CharactersApiRepository()
    private val charactersRepository = CharactersRepository()
    private val characterImageRepository = CharacterImageRepository()

    private var characters: MutableList<CharacterHeadline> = mutableListOf()
    var liveCharacters: MutableLiveData<MutableList<CharacterHeadline>> = MutableLiveData()

    init {
        liveCharacters = MutableLiveData(characters)
        fetchCharacters()
    }

    private fun fetchCharacters() {

        viewModelScope.launch(Dispatchers.IO) {

            // TASK-1: fetching characters API URL
            val charactersApi =
                charactersApiRepository.fetchCharactersApiUrl()
                    ?: return@launch //TODO error handling
            Common.charactersApiUrl = charactersApi // to use in other Activity

            // TASK-2: fetching characters data(except image)
            characters = charactersRepository.fetchCharacters(charactersApi)
                ?: return@launch //TODO error handling
            viewModelScope.launch(Dispatchers.Main) {
                liveCharacters.value = characters
            }

            // TASK-3: fetching character images
            for (i in characters.indices) {
                viewModelScope.launch(Dispatchers.IO) {
                    val urlStr = characters[i].imageUrl
                    val image = characterImageRepository.fetchImage(urlStr)
                    viewModelScope.launch(Dispatchers.Main) {
                        characters[i].image = image
                        liveCharacters.value = characters
                    }
                }
            }
        }
    }

    fun getCharacter(index: Int) = liveCharacters.value?.get(index)
    fun getCharacterCount() = liveCharacters.value?.size ?: 0
}