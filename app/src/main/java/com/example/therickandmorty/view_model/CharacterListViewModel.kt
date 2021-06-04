package com.example.therickandmorty.view_model

import android.text.Editable
import android.text.TextWatcher
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

    var filteringWord: MutableLiveData<String> = MutableLiveData()
    var clickLister: ClickItemListener? = null

    init {
        filteringWord.value = Common.EMPTY_STRING
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
                liveCharacters.value = filterCharacters()
            }

            // TASK-3: fetching character images
            for (i in characters.indices) {
                viewModelScope.launch(Dispatchers.IO) {
                    val urlStr = characters[i].imageUrl
                    val image = characterImageRepository.fetchImage(urlStr)
                    viewModelScope.launch(Dispatchers.Main) {
                        characters[i].image = image
                        liveCharacters.value = filterCharacters()
                    }
                }
            }
        }
    }

    fun createTextChangeListener(): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                liveCharacters.value = filterCharacters()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
    }

    private fun filterCharacters(): MutableList<CharacterHeadline> {
        val filter = filteringWord.value ?: return characters
        if (filter == Common.EMPTY_STRING) return characters

        val filteredList = mutableListOf<CharacterHeadline>()
        for (character in characters) {
            if (character.name.toUpperCase().contains(filter.toUpperCase())) {
                filteredList.add(character)
            }
        }
        return filteredList
    }

    fun getCharacter(index: Int) = liveCharacters.value?.get(index)
    fun getCharacterCount() = liveCharacters.value?.size ?: 0

    fun onClickCharacter(character: CharacterHeadline) {
        clickLister?.onClickItem(character)
    }
}

interface ClickItemListener {
    fun onClickItem(item: CharacterHeadline)
}