package ru.skillbranch.gameofthrones.ui.characters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.internal.immutableListOf
import ru.skillbranch.devintensive.extensions.mutableLiveData
import ru.skillbranch.gameofthrones.AppConfig
import ru.skillbranch.gameofthrones.data.remote.res.HouseRes
import ru.skillbranch.gameofthrones.repositories.RootRepository

class HousesViewModel : ViewModel() {

    private val allHouses: MutableLiveData<List<HouseRes>> = mutableLiveData(immutableListOf())

    init {
        RootRepository.getNeedHouses(*AppConfig.NEED_HOUSES) {
            allHouses.value = it
        }
    }

    fun getAllHouses(): LiveData<List<HouseRes>> = allHouses

}