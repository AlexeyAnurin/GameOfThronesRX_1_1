package ru.skillbranch.gameofthrones.data.remote.res

//AA   поля одного дома по ссылке https://www.anapioficeandfire.com/api/houses
data class HouseRes(
    val url: String,
    val name: String,
    val region: String,
    val coatOfArms: String,
    val words: String,
    val titles: List<String> = listOf(),
    val seats: List<String> = listOf(),
    val currentLord: String,
    val heir: String,
    val overlord: String,
    val founded: String,
    val founder: String,
    val diedOut: String,
    val ancestralWeapons: List<String> = listOf(),
    val cadetBranches: List<String> = listOf(),
    val swornMembers: List<String> = listOf()
)