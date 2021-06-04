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
import com.example.therickandmorty.repository.characters.CharactersRepository
import com.example.therickandmorty.repository.characters.CharactersRepositoryInterface
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

    init {
        filteringWord.value = Common.EMPTY_STRING
        liveCharacters = MutableLiveData(characters)

        /**
         * the repo can be switched with test data
         */
        charactersRepository = CharactersRepository()
//        charactersRepository = DummyCharactersRepository() // test for a large amount of character

        fetchCharacters()
    }

    private fun fetchCharacters() {

        viewModelScope.launch(Dispatchers.IO) {

            // TASK-1: fetching characters API URL
            val charactersApi =
                charactersApiRepository.fetchCharactersApiUrl()
                    ?: return@launch //TODO error action should be shown
            Common.charactersApiUrl = charactersApi // to use in other Activity

            // TASK-2: fetching characters data(except image)
            characters = charactersRepository.fetchCharacters(charactersApi)
                ?: return@launch //TODO error action should be shown
            if (characters.size == 0) return@launch
            viewModelScope.launch(Dispatchers.Main) {
                liveCharacters.value = characters
            }

            // TASK-3: fetching character images
            var runningTaskCount = 0
            var doneTaskCount = 0
            val runningTaskLimit = 5
            val waitingTime = 100.toLong()
            val usingMainThreadInterval = 1000.toLong()
            var isFetchingImages = true
            val mutex = Mutex()

            /**
             * to avoid to occupy Main-Thread too frequently,
             * do the process with time interval
             * Doing the process every time fetching a image will cause a bug of ignoring ui events
             */
            viewModelScope.launch(Dispatchers.IO) {
                while (isFetchingImages) {
                    delay(usingMainThreadInterval)
                    viewModelScope.launch(Dispatchers.Main) {
                        liveCharacters.value = characters
                    }
                }
            }

            for (i in characters.indices) {
                /**
                 * to prevent lots of tasks from running at the same time,
                 * until the number of running tasks is under the limit,
                 * never launch another task
                 */
                while (runningTaskCount > runningTaskLimit) {
                    delay(waitingTime)
                }

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

                    /**
                     * without mutex, counting massive number(ex: 10,000) can not be done correctly
                     * Ref: https://kotlinlang.org/docs/shared-mutable-state-and-concurrency.html#mutual-exclusion
                     */
                    mutex.withLock {
                        doneTaskCount++ // even if fetching image is failed, count up
                    }
                    if (doneTaskCount == characters.size) {
                        isFetchingImages = false
                        println("test: every task has been done!")
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