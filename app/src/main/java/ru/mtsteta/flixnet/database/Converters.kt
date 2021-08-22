package ru.mtsteta.flixnet.database

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun toListOfInts(actors: String?): List<Int>? {
        return actors?.split(",")?.map { it.toInt() }
    }

    @TypeConverter
    fun fromListOfInts(actorsList: List<Int>?): String? {
        return actorsList?.joinToString(",")
    }

    @TypeConverter
    fun toListOfStrings(genres: String?): List<String>? {
        return genres?.split("|")
    }

    @TypeConverter
    fun fromListOfStrings(genresList: List<String>?): String? {
        return genresList?.joinToString("|")
    }

}