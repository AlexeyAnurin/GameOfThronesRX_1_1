package ru.skillbranch.gameofthrones.extensions

import ru.skillbranch.gameofthrones.data.local.entities.House
import ru.skillbranch.gameofthrones.data.remote.res.HouseRes
import ru.skillbranch.gameofthrones.util.Utils

fun HouseRes.toHouse(): House {
    val h = House(
        Utils.getUrlId(this.url),
        this.name,
        this.region,
        this.coatOfArms,
        this.words,
        this.titles,
        this.seats,
        this.currentLord,
        this.heir,
        this.overlord,
        this.founded,
        this.founder,
        this.diedOut,
        this.ancestralWeapons
    )

    h.shortName = Utils.getHouseShortName(this.name)
    h.cadetBranches = this.cadetBranches
    h.swornMembers = this.swornMembers

    return h
}