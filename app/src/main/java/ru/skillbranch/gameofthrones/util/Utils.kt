package ru.skillbranch.gameofthrones.util

import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.data.remote.res.HouseRes

object Utils {

    val STARK = "House Stark of Winterfell"
    val LANNISTER = "House Lannister of Casterly Rock"
    val TARGARYEN = "House Targaryen of King's Landing"
    val GREYJOY = "House Greyjoy of Pyke"
    val TYRELL = "House Tyrell of Highgarden"
    val BARATHEON = "House Baratheon of Dragonstone"
    val MARTELL = "House Nymeros Martell of Sunspear"

    val HOUSE_SHORTS = mapOf(
        STARK to "Stark",
        LANNISTER to "Lannister",
        TARGARYEN to "Targaryen",
        GREYJOY to "Greyjoy",
        TYRELL to "Tyrell",
        BARATHEON to "Baratheon",
        MARTELL to "Martell"
    )

    val HOUSE_ORDER = mapOf(
        STARK to 0,
        LANNISTER to 1,
        TARGARYEN to 2,
        GREYJOY to 4,
        TYRELL to 6,
        BARATHEON to 3,
        MARTELL to 5
    )

    fun getHouseShortName(name: String): String {
        return HOUSE_SHORTS[name] ?: name
    }
    
    fun getUrlId(url: String): String {
        return url.substring(url.lastIndexOf("/") + 1)
    }

    fun getHouseIconRes(name: String) = when (name) {
        "Stark" -> R.drawable.stark_icon
        "Lannister" -> R.drawable.lanister_icon
        "Targaryen" -> R.drawable.targaryen_icon
        "Greyjoy" -> R.drawable.greyjoy_icon
        "Tyrell" -> R.drawable.tyrel_icon
        "Baratheon" -> R.drawable.baratheon_icon
        "Martell" -> R.drawable.martel_icon
        else -> R.drawable.stark_icon
    }

    fun getOrderedHouses(list: List<HouseRes>) = list.sortedWith(Comparator { h1, h2 ->
        HOUSE_ORDER[h1.name]!!.minus(HOUSE_ORDER[h2.name]!!)
    })

    val COLOR_PRIMARY = 0
    val COLOR_DARK = 1
    val COLOR_ACCENT = 2
    fun getHouseColor(name: String, id: Int): Int {
        val colors = when(name) {
            "Stark" -> listOf(R.color.stark_primary, R.color.stark_dark, R.color.stark_accent)
            "Lannister" -> listOf(
                R.color.lannister_primary,
                R.color.lannister_dark,
                R.color.lannister_accent
            )
            "Targaryen" -> listOf(
                R.color.targaryen_primary,
                R.color.targaryen_dark,
                R.color.targaryen_accent
            )
            "Greyjoy" -> listOf(
                R.color.greyjoy_primary,
                R.color.greyjoy_dark,
                R.color.greyjoy_accent
            )
            "Tyrell" -> listOf(R.color.tyrel_primary, R.color.tyrel_dark, R.color.tyrel_accent)
            "Baratheon" -> listOf(
                R.color.baratheon_primary,
                R.color.baratheon_dark,
                R.color.baratheon_accent
            )
            "Martell" -> listOf(R.color.martel_primary, R.color.martel_dark, R.color.martel_accent)
            else -> listOf(R.color.stark_primary, R.color.stark_dark, R.color.stark_accent)
        }
        return colors!![id]
    }

    fun getHouseImageRes(name: String) = when (name) {
        "Stark" -> R.drawable.stark_coast_of_arms
        "Lannister" -> R.drawable.lannister__coast_of_arms
        "Targaryen" -> R.drawable.targaryen_coast_of_arms
        "Greyjoy" -> R.drawable.greyjoy_coast_of_arms
        "Tyrell" -> R.drawable.tyrel_coast_of_arms
        "Baratheon" -> R.drawable.baratheon
        "Martell" -> R.drawable.martel_coast_of_arms
        else -> R.drawable.stark_coast_of_arms
    }

}