package ru.skillbranch.gameofthrones.data.local

import androidx.room.*
import io.reactivex.Maybe
import io.reactivex.Single
import ru.skillbranch.gameofthrones.data.local.entities.Character
import ru.skillbranch.gameofthrones.data.local.entities.CharacterWithHouse

//AA    Описываются методы для работы с базой данных
//AA    @Query - аннотация для SQL запроса
@Dao
interface CharacterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(houses: List<Character>): Single<List<Long>>

    @Query("SELECT * FROM characters WHERE id = :id")
    fun getById(id: String): Maybe<Character>

    @Transaction
    @Query("SELECT * FROM characters WHERE id = :id")
    fun getWithHouse(id: String): Single<CharacterWithHouse>

    @Transaction
    @Query("DELETE FROM characters")
    fun deleteAll(): Single<Int>

}