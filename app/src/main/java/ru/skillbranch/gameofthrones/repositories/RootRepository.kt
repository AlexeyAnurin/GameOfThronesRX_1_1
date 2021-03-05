package ru.skillbranch.gameofthrones.repositories

import android.text.TextUtils
import androidx.annotation.VisibleForTesting
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ru.skillbranch.gameofthrones.App
import ru.skillbranch.gameofthrones.AppConfig
import ru.skillbranch.gameofthrones.data.local.AppDatabase
import ru.skillbranch.gameofthrones.data.local.entities.Character
import ru.skillbranch.gameofthrones.data.local.entities.CharacterFull
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.data.local.entities.RelativeCharacter
import ru.skillbranch.gameofthrones.data.remote.res.CharacterRes
import ru.skillbranch.gameofthrones.data.remote.res.HouseRes
import ru.skillbranch.gameofthrones.extensions.*

//AA Один репозиторий, т.к. object - синглтон
object RootRepository {

    //AA? Зачем этот TAG
    private val TAG = RootRepository::class.java.simpleName
    private val SYNC_PAGE_SIZE = 50
    //AA свойство db в object RootRepository через fun init(AppDatabase) получает значение "Room.databaseBuilder(context, AppDatabase::class.java, "database").build()" из App.kt
    lateinit var db: AppDatabase
    var compositeDisposable = CompositeDisposable()

    fun init(database: AppDatabase) {
        db = database
    }

    /**
     * Получение данных о всех домах
     * @param result - колбек содержащий в себе список данных о домах
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getAllHouses(result : (houses : List<HouseRes>) -> Unit) {
        val remoteHouses = mutableListOf<HouseRes>()
        loadHouses(1, remoteHouses, result)
    }

    private fun loadHouses(page: Int, remoteHouses: MutableList<HouseRes>, result : (houses : List<HouseRes>) -> Unit) {
        val d = App.api.getHouses(page, SYNC_PAGE_SIZE)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { houseResList ->
                    if(houseResList.isNotEmpty()) {
                        remoteHouses.addAll(houseResList)
                        loadHouses(page + 1, remoteHouses, result)
                    } else {
                        insertHouses(remoteHouses) {
                            result.invoke(remoteHouses)
                        }
                    }
                },
                {
                    result.invoke(listOf())
                    App.logThrowable(TAG, it)
                }
            )
        compositeDisposable.add(d)
    }

    /**
     * Получение данных о требуемых домах по их полным именам (например фильтрация всех домов)
     * @param houseNames - массив полных названий домов (смотри AppConfig)
     * @param result - колбек содержащий в себе список данных о домах
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getNeedHouses(vararg houseNames: String, result : (houses : List<HouseRes>) -> Unit) {
        val d = db.houseDao().getNeedHouses(houseNames.toList())
            .map {  houseList ->
                houseList.map { it.toHouseRes() }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    result.invoke(it)
                },
                {
                    App.logThrowable(TAG, it)
                }
            )
        compositeDisposable.add(d)
    }

    /**
     * Получение данных о требуемых домах по их полным именам и персонажах в каждом из домов
     * @param houseNames - массив полных названий домов (смотри AppConfig)
     * @param result - колбек содержащий в себе список данных о доме и персонажей в нем (Дом - Список Персонажей в нем)
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getNeedHouseWithCharacters(vararg houseNames: String, result : (houses : List<Pair<HouseRes, List<CharacterRes>>>) -> Unit) {
        val d = db.houseDao().getNeedHouseWithCharacters(houseNames.toList())
            .map {  houseList ->
                houseList.map { Pair(it.house.toHouseRes(), it.characters.map { it.toCharacterRes() }) }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    result.invoke(it)
                },
                {
                    App.logThrowable(TAG, it)
                }
            )
        compositeDisposable.add(d)
    }

    /**
     * Запись данных о домах в DB
     * @param houses - Список персонажей (модель HouseRes - модель ответа из сети)
     * необходимо произвести трансформацию данных
     * @param complete - колбек о завершении вставки записей db
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun insertHouses(houses : List<HouseRes>, complete: () -> Unit) {
        val d = Observable.just(houses)
            .map { list ->
                list.map { it.toHouse() }
            }
            .flatMap {
                db.houseDao().insert(it)
                    .toObservable()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    complete.invoke()
                },
                {
                    App.logThrowable(TAG, it)
                }
            )
        compositeDisposable.add(d)
    }

    /**
     * Запись данных о пересонажах в DB
     * @param Characters - Список персонажей (модель CharacterRes - модель ответа из сети)
     * необходимо произвести трансформацию данных
     * @param complete - колбек о завершении вставки записей db
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun insertCharacters(Characters : List<CharacterRes>, complete: () -> Unit) {
        val d = Observable.just(Characters)
            .map { list ->
                list.map { it.toCharacter() }
            }
            .flatMap {
                db.characterDao().insert(it)
                    .toObservable()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    complete.invoke()
                },
                {
                    App.logThrowable(TAG, it)
                }
            )
        compositeDisposable.add(d)
    }

    /**
     * При вызове данного метода необходимо выполнить удаление всех записей в db
     * @param complete - колбек о завершении очистки db
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun dropDb(complete: () -> Unit) {
        val d = db.houseDao().deleteAll()
            .flatMap { db.characterDao().deleteAll() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { complete.invoke() }
            .subscribe(
                {

                },
                {
                    App.logThrowable(TAG, it)
                }
            )
        compositeDisposable.add(d)
    }

    /**
     * Поиск всех персонажей по имени дома, должен вернуть список краткой информации о персонажах
     * дома - смотри модель CharacterItem
     * @param name - краткое имя дома (его первычный ключ)
     * @param result - колбек содержащий в себе список краткой информации о персонажах дома
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun findCharactersByHouseName(name : String, result: (Characters : List<CharacterItem>) -> Unit) {
        val d = db.houseDao().getHouseWithCharacters(name)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { houseWithCharacters ->
                    val characterItems = houseWithCharacters.characters.map { it.toCharacterItem() }
                    result.invoke(characterItems)
                },
                {
                    App.logThrowable(TAG, it)
                }
            )
        compositeDisposable.add(d)
    }

    /**
     * Поиск персонажа по его идентификатору, должен вернуть полную информацию о персонаже
     * и его родственных отношения - смотри модель CharacterFull
     * @param id - идентификатор персонажа
     * @param result - колбек содержащий в себе полную информацию о персонаже
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun findCharacterFullById(id : String, result: (Character : CharacterFull) -> Unit) {
        val d = db.characterDao().getWithHouse(id)
            .toObservable()
            .flatMap { characterWithHouse ->
                val father = characterWithHouse.character.father
                if(!TextUtils.isEmpty(father)) {
                    db.characterDao().getById(father)
                        .toObservable()
                        .map {
                            Pair(characterWithHouse, it)
                        }
                        .defaultIfEmpty(
                            Pair(characterWithHouse, Character.getEmptyCharacter())
                        )
                } else {
                    Observable.just(Pair(characterWithHouse, null))
                }
            }
            .flatMap { charWithHouseFatherPair ->
                val character = charWithHouseFatherPair.first.character
                val mother = character.mother
                if(!TextUtils.isEmpty(mother)) {
                    db.characterDao().getById(mother)
                        .toObservable()
                        .map {
                            Triple(charWithHouseFatherPair.first, charWithHouseFatherPair.second, it)
                        }
                        .defaultIfEmpty(
                            Triple(charWithHouseFatherPair.first, charWithHouseFatherPair.second, Character.getEmptyCharacter())
                        )
                } else {
                    Observable.just(Triple(charWithHouseFatherPair.first, charWithHouseFatherPair.second, null))
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    val (charWithHouse, father, mother) = it

                    val fullChar = CharacterFull(
                        charWithHouse.character.id,
                        charWithHouse.character.name,
                        charWithHouse.house.words,
                        charWithHouse.character.born,
                        charWithHouse.character.died,
                        charWithHouse.character.titles,
                        charWithHouse.character.aliases,
                        charWithHouse.house.shortName,
                        if(father != null && !TextUtils.isEmpty(father.id)) RelativeCharacter(father.id, father.name, father.houseId?:"") else null,
                        if(mother != null && !TextUtils.isEmpty(mother.id)) RelativeCharacter(mother.id, mother.name, mother.houseId?:"") else null
                    )
                    result.invoke(fullChar)
                },
                {
                    App.logThrowable(TAG, it)
                }
            )
        compositeDisposable.add(d)
    }

    /**
     * Метод возвращет true если в базе нет ни одной записи, иначе false
     * @param result - колбек о завершении очистки db
     */
    fun isNeedUpdate(result: (isNeed : Boolean) -> Unit){
        val d = db.houseDao().getCount()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    result.invoke(it == 0)
                },
                {
                    App.logThrowable(TAG, it)
                }
            )
        compositeDisposable.add(d)
    }

    fun dispose() {
        compositeDisposable.clear()
        compositeDisposable = CompositeDisposable()
    }

}