package com.example.therickandmorty.view_model

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.therickandmorty.model.Common
import com.example.therickandmorty.model.DataType
import com.example.therickandmorty.model.character.CharacterHeadline
import com.example.therickandmorty.repository.CharacterImageRepository
import com.example.therickandmorty.repository.CharactersApiRepository
import com.example.therickandmorty.repository.characters.CharactersRepository
import com.example.therickandmorty.repository.characters.CharactersRepositoryInterface
import com.example.therickandmorty.repository.characters.DummyCharactersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class CharacterListViewModel : ViewModel() {

    private val charactersApiRepository = CharactersApiRepository()
    private val charactersRepository: CharactersRepositoryInterface
    private val characterImageRepository = CharacterImageRepository()

    private var characters: MutableList<CharacterHeadline> = mutableListOf()
    var liveCharacters: MutableLiveData<MutableList<CharacterHeadline>> = MutableLiveData()

    var filteringWord: MutableLiveData<String> = MutableLiveData()
    var clickLister: ClickItemListener? = null
    var statusMessage: MutableLiveData<String> = MutableLiveData()

    init {
        filteringWord.value = Common.EMPTY_STRING
        statusMessage.value = "default"
        liveCharacters = MutableLiveData(characters)
        charactersRepository = createCharactersRepository()
        fetchCharacters()
    }

    private fun createCharactersRepository(): CharactersRepositoryInterface {
        return when (Common.dataType) {
            DataType.PRODUCT -> CharactersRepository()
            else -> DummyCharactersRepository()
        }
    }

    private fun fetchCharacters() {

        /**
         * each Task will wait until the previous Task has been done.
         * Task-2 never starts until Task-1 has been finished
         */
        viewModelScope.launch(Dispatchers.IO) {

            // TASK-1: fetching characters API URL
            changeStatusMessage("Fetching characters API....")
            val charactersApi =
                charactersApiRepository.fetchCharactersApiUrl()
            if (charactersApi == null) {
                changeStatusMessage("Failed fetching characters API !")
                return@launch
            }
            Common.charactersApiUrl = charactersApi // to use in other Activity

            // TASK-2: fetching characters data(except image)
            changeStatusMessage("Fetching characters....")
            val fetchedCharacters = charactersRepository.fetchCharacters(charactersApi)
            if (fetchedCharacters == null) {
                changeStatusMessage("Failed fetching characters !")
                return@launch
            }
            characters = fetchedCharacters
            if (characters.size == 0) {
                changeStatusMessage("There is no characters !")
                return@launch
            }
            updateLiveCharacter()

            // TASK-3: fetching character images
            fetchCharacterImages()
        }
    }

    /**
     * these are used for fetching character images
     */
    private var runningTaskCount = 0
    private var doneTaskCount = 0
    private val runningTaskLimit = 5
    private val waitingTime = 100.toLong()
    private val mainThreadInterval = 1000.toLong()
    private var isFetchingImages = true
    private val mutex = Mutex()

    private suspend fun fetchCharacterImages() {
        if (characters.size == 0) return

        changeStatusMessage("Fetching character images....")

        updateUIWithInterval()

        fetchImagesManagingTaskCount()
    }

    private suspend fun fetchImagesManagingTaskCount() {
        for (i in characters.indices) {
            /**
             * to prevent lots of tasks from running at the same time,
             * until the number of running tasks is under the limit,
             * never launch another task
             */
            while (runningTaskCount > runningTaskLimit) delay(waitingTime)

            viewModelScope.launch(Dispatchers.IO) {
                val urlStr = characters[i].imageUrl
                mutex.withLock {
                    runningTaskCount++
                }
                val image = characterImageRepository.fetchImage(urlStr)
                mutex.withLock {
                    runningTaskCount--
                }
                characters[i].image = image

                manageDoneTaskCount()
            }
        }
    }

    private suspend fun manageDoneTaskCount() {
        /**
         * without mutex, counting massive number(ex: 1,000) can not be done correctly
         * Ref: https://kotlinlang.org/docs/shared-mutable-state-and-concurrency.html#mutual-exclusion
         */
        mutex.withLock {
            doneTaskCount++ // even if fetching image is failed, count up
        }
        if (doneTaskCount == characters.size) {
            isFetchingImages = false
            changeStatusMessage("Finished all fetching task !!!")
        }
    }

    private fun updateUIWithInterval() {
        /**
         * to avoid to occupy Main-Thread too frequently,
         * do the process with time interval
         * without interval, doing the process every time fetching a image will cause a bug of ignoring ui events
         */
        viewModelScope.launch(Dispatchers.IO) {
            while (isFetchingImages) {
                delay(mainThreadInterval)
                updateLiveCharacter()
            }
        }
    }

    private fun changeStatusMessage(message: String) {
        viewModelScope.launch(Dispatchers.Main) {
            statusMessage.value = message
        }
    }

    private fun updateLiveCharacter() {
        viewModelScope.launch(Dispatchers.Main) {
            liveCharacters.value = filterCharacters()
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