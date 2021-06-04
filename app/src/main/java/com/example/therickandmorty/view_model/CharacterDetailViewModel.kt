package com.example.therickandmorty.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.therickandmorty.model.character.CharacterDetail
import com.example.therickandmorty.repository.CharacterDetailRepository
import com.example.therickandmorty.repository.CharacterImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CharacterDetailViewModel : ViewModel() {

    var characterId: String? = null
    private var characterDetail: CharacterDetail? = null
    var liveCharacterDetail: MutableLiveData<CharacterDetail?> = MutableLiveData()

    private val characterDetailRepo: CharacterDetailRepository
    private val characterImageRepo: CharacterImageRepository

    init {
        liveCharacterDetail = MutableLiveData(characterDetail)
        characterDetailRepo = CharacterDetailRepository()
        characterImageRepo = CharacterImageRepository()
    }

    fun fetchCharacterDetail() {
        val id = characterId ?: return // TODO error display

        viewModelScope.launch(Dispatchers.IO) {
            // TASK-1: fetching character data except image
            characterDetail =
                characterDetailRepo.fetchCharacterDetail(id) ?: return@launch // TODO error display
            viewModelScope.launch(Dispatchers.Main) {
                liveCharacterDetail.value = characterDetail
            }

            // TASK-2: fetching image
            val imageUrl = characterDetail?.imageUrl ?: return@launch // TODO error display
            characterDetail?.image = characterImageRepo.fetchImage(imageUrl)
            viewModelScope.launch(Dispatchers.Main) {
                liveCharacterDetail.value = characterDetail
            }
        }
    }
}