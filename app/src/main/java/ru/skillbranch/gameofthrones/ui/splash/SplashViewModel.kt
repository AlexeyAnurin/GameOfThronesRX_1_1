package ru.skillbranch.gameofthrones.ui.splash

import android.app.Application
import android.content.Intent
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ru.skillbranch.gameofthrones.App
import ru.skillbranch.gameofthrones.AppConfig
import ru.skillbranch.gameofthrones.ConnectivityChangeReceiver
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.data.remote.res.HouseRes
import ru.skillbranch.gameofthrones.repositories.RootRepository
import ru.skillbranch.gameofthrones.ui.LiveEvent
import ru.skillbranch.gameofthrones.ui.characters.CharactersListScreen
import ru.skillbranch.gameofthrones.util.Utils


class SplashViewModel(val app: Application) : AndroidViewModel(app) {

    companion object {
        private val TAG = SplashViewModel::class.java.simpleName
        private val PROGRESS_TIME_MIN = 5000
    }

    val loading = MutableLiveData (false)
    val errorEvent = LiveEvent<String> ()
    val compositeDisposable = CompositeDisposable()
    var syncStartTime = 0L // ms

    fun onCreate(activity: AppCompatActivity) {
        RootRepository.isNeedUpdate { needUpdate ->
            if(needUpdate) {
                sync(activity)
            } else {
                openCharactersScreen(activity)
            }
        }
    }

    private fun sync(activity: AppCompatActivity) {
        if(App.connectivityReceiver.getConnectionStatus(app) == ConnectivityChangeReceiver.STATUS_NOT_CONNECTED) {
            errorEvent.value = app.getString(R.string.connection_error)

            App.connectivityReceiver.setListener { status ->
                App.connectivityReceiver.setListener {  }
                if(status != ConnectivityChangeReceiver.STATUS_NOT_CONNECTED) {
                    sync(activity)
                }
            }
            return
        }

        syncStartTime = System.currentTimeMillis()
        loading.value = true

        RootRepository.getAllHouses {
            syncCharacters(activity, it.toMutableList())
        }
    }

    private fun syncCharacters(activity: AppCompatActivity, houseResList: MutableList<HouseRes>) {
        val characterHouseMap = mutableMapOf<String,String>()
        val allCharactersIdSet = mutableSetOf<String>()
        var allCharactersCount = 0
        houseResList.forEach {
            allCharactersCount += it.swornMembers.size
        }
        houseResList.filter { it.name in AppConfig.NEED_HOUSES }.forEach { houseRes ->
            val membersIds = houseRes.swornMembers.map { Utils.getUrlId(it) }
            membersIds.forEach {
                characterHouseMap[it] = Utils.getHouseShortName(houseRes.name)
            }
            allCharactersIdSet.addAll(membersIds)
        }

        doSyncCharacters(activity, characterHouseMap, allCharactersIdSet, houseResList)
    }

    private fun doSyncCharacters(activity: AppCompatActivity, characterHouseMap: Map<String,String>,
                                 ids: Set<String>, houseResList: MutableList<HouseRes>) {
        val d = Observable.fromIterable(ids)
            .flatMap { id ->
                App.api.getCharacter(id)
                    .map {
                        it.houseId = characterHouseMap[Utils.getUrlId(it.url)] ?: ""
                        it
                    }
                    .toObservable()
            }
            .toList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally {
                houseResList.clear()
            }
            .subscribe(
                { characterResList ->
                    RootRepository.insertCharacters(characterResList) {
                        onSynced(activity)
                    }
                },
                {
                    loading.value = false
                    errorEvent.value = "Load characters error: ${it.message}"
                    App.logThrowable(TAG, it)
                }
            )
        compositeDisposable.add(d)
    }

    private fun onSynced(activity: AppCompatActivity) {
        val t = System.currentTimeMillis() - syncStartTime
        if(t >= PROGRESS_TIME_MIN) {
            openCharactersScreen(activity)
        } else {
            Handler().postDelayed({
                openCharactersScreen(activity)
            }, PROGRESS_TIME_MIN - t)
        }
    }

    private fun openCharactersScreen(activity: AppCompatActivity) {
        loading.value = false

        val intent = Intent(activity, CharactersListScreen::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(intent)
        activity.finish()
    }

    override fun onCleared() {
        RootRepository.dispose()
        compositeDisposable.clear()
    }
}