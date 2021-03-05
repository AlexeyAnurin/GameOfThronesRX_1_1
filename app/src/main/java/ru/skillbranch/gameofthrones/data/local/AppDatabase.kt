package ru.skillbranch.gameofthrones.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.skillbranch.gameofthrones.data.local.entities.Character
import ru.skillbranch.gameofthrones.data.local.entities.House
import ru.skillbranch.gameofthrones.data.local.entities.StringListConverter

//AA  DataBase - основной класс для работы с БД. Он абстрактный и наследуется от RoomDatabase
//AA  в параметрах DataBase указать нужные классы, отмеченные аннотацией Entity и версию базы.
//AA  Character::class - :: ссылка на класс
//AA  Class<?>[] entities(); - The list of entities included in the database. Each entity turns into a table in the database.
@Database(entities = [Character::class, House::class], version = 1)
@TypeConverters(StringListConverter::class)

abstract class AppDatabase : RoomDatabase() {
    //AA  Описаны абстрактные методы для получения Dao-объектов. Их классы отмечены аннотацией @Entity.
    //AA?  Реализация методов создаётся автоматически. Когда? Файлы %NAME%_Impl
    abstract fun characterDao(): CharacterDao
    abstract fun houseDao(): HouseDao
}