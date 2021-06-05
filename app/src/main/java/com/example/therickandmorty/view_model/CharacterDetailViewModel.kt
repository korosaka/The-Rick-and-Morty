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
    var statusMessage: MutableLiveData<String> = MutableLiveData()

    private val characterDetailRepo: CharacterDetailRepository
    private val characterImageRepo: CharacterImageRepository

    init {
        liveCharacterDetail = MutableLiveData(characterDetail)
        characterDetailRepo = CharacterDetailRepository()
        characterImageRepo = CharacterImageRepository()
        statusMessage.value = "default"
    }

    fun fetchCharacterDetail() {

        if (characterId == null) {
            changeStatusMessage("Failed fetching ! (NON-ID)")
            return
        }
        val id = characterId!!

        viewModelScope.launch(Dispatchers.IO) {
            // TASK-1: fetching character data except image
            changeStatusMessage("Fetching character data....")
            characterDetail =
                characterDetailRepo.fetchCharacterDetail(id)
            if (characterDetail == null) {
                changeStatusMessage("Failed fetching character data")
                return@launch
            }
            viewModelScope.launch(Dispatchers.Main) {
                liveCharacterDetail.value = characterDetail
            }

            // TASK-2: fetching image
            val imageUrl = characterDetail?.imageUrl
            if (imageUrl == null) {
                changeStatusMessage("Failed fetching image ! (NON-URL)")
                return@launch
            }
            changeStatusMessage("Fetching character image....")
            characterDetail?.image = characterImageRepo.fetchImage(imageUrl)
            if (characterDetail?.image == null) {
                changeStatusMessage("Failed fetching image !")
                return@launch
            }
            viewModelScope.launch(Dispatchers.Main) {
                liveCharacterDetail.value = characterDetail
                changeStatusMessage("Finished all fetching task !!")
            }
        }
    }

    private fun changeStatusMessage(message: String) {
        viewModelScope.launch(Dispatchers.Main) {
            statusMessage.value = message
        }
    }
}