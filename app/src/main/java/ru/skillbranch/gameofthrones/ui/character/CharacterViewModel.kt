package ru.skillbranch.gameofthrones.ui.character

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.skillbranch.devintensive.extensions.mutableLiveData
import ru.skillbranch.gameofthrones.data.local.entities.CharacterFull
import ru.skillbranch.gameofthrones.repositories.RootRepository

class CharacterViewModel : ViewModel() {

    private val character: MutableLiveData<CharacterFull> = mutableLiveData()

    fun onId(id: String) {
        RootRepository.findCharacterFullById(id) {
            character.value = it
        }
    }

    fun getCharacter(): LiveData<CharacterFull> = character

}