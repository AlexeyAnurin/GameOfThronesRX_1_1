package ru.skillbranch.gameofthrones.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation

class CharacterWithHouse (

    @Embedded
    val character: Character,

    @Relation(parentColumn = "houseId", entityColumn = "shortName")
    val house: House

) {

}