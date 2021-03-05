package ru.skillbranch.gameofthrones.data.local

import androidx.room.*
import io.reactivex.Single
import ru.skillbranch.gameofthrones.data.local.entities.House
import ru.skillbranch.gameofthrones.data.local.entities.HouseWithCharacters

@Dao
interface HouseDao {

    @Query("SELECT * FROM houses")
    fun getAllHouses(): Single<List<House>>

    @Query("SELECT * FROM houses WHERE name IN (:houseNames)")
    fun getNeedHouses(houseNames: List<String>): Single<List<House>>

    @Transaction
    @Query("SELECT * FROM houses WHERE name IN (:houseNames)")
    fun getNeedHouseWithCharacters(houseNames: List<String>): Single<List<HouseWithCharacters>>

    @Transaction
    @Query("SELECT * FROM houses WHERE shortName = :shortName")
    fun getHouseWithCharacters(shortName: String): Single<HouseWithCharacters>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(houses: List<House>): Single<List<Long>>

    @Query("SELECT COUNT(*) FROM houses")
    fun getCount(): Single<Int>

    @Query("DELETE FROM houses")
    fun deleteAll(): Single<Int>

}