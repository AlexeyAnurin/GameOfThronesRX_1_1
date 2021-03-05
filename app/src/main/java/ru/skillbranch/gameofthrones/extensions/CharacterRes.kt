package ru.skillbranch.gameofthrones.extensions

import ru.skillbranch.gameofthrones.data.local.entities.Character
import ru.skillbranch.gameofthrones.data.remote.res.CharacterRes
import ru.skillbranch.gameofthrones.util.Utils

fun CharacterRes.toCharacter(): Character {
    val ch = Character(
        Utils.getUrlId(this.url),
        this.name,
        this.gender,
        this.culture,
        this.born,
        this.died,
        this.titles,
        this.aliases,
        Utils.getUrlId(this.father),
        Utils.getUrlId(this.mother),
        this.spouse
    )

    ch.houseId = this.houseId

    return ch
}