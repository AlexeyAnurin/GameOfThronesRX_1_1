package ru.skillbranch.gameofthrones.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation

class HouseWithCharacters(

    @Embedded
    val house: House,

    @Relation(parentColumn = "shortName", entityColumn = "houseId")
    val characters: List<Character>
) {

}