package ru.skillbranch.gameofthrones.extensions

import ru.skillbranch.gameofthrones.AppConfig
import ru.skillbranch.gameofthrones.data.local.entities.House
import ru.skillbranch.gameofthrones.data.remote.res.HouseRes

fun House.toHouseRes() = HouseRes(
    "${AppConfig.BASE_URL}houses/${this.id}",
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
    this.ancestralWeapons,
    this.cadetBranches,
    this.swornMembers
)