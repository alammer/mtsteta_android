package ru.mtsteta.flixnet.database

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun toListOfStrings(actors: String?): List<String>? {
        return actors?.split("|")
    }

    @TypeConverter
    fun fromListOfStrings(actorsList: List<String>?): String? {
        return actorsList?.joinToString("|")
    }

}