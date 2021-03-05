package ru.skillbranch.gameofthrones.extensions

import ru.skillbranch.gameofthrones.AppConfig
import ru.skillbranch.gameofthrones.data.local.entities.Character
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.data.remote.res.CharacterRes

fun Character.toCharacterItem(): CharacterItem {
    return CharacterItem(
        this.id,
        this.houseId!!,
        this.name,
        this.titles,
        this.aliases
    )
}

fun Character.toCharacterRes(): CharacterRes {
    return CharacterRes(
        "${AppConfig.BASE_URL}characters/${this.id}",
        this.name,
        this.gender,
        this.culture,
        this.born,
        this.died,
        this.titles,
        this.aliases,
        this.father,
        this.mother,
        this.spouse,
        listOf(),
        listOf(),
        listOf(),
        listOf()
    )
}