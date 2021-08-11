package ru.mtsteta.flixnet.database

import androidx.room.*

@Entity(tableName = "movie_table")
data class Movie(
    @PrimaryKey(autoGenerate = true)
    var movieId: Long = 0L,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "genre")
    var genre: String,

    @ColumnInfo(name = "rating")
    var rateScore: Int = 0,

    @ColumnInfo(name = "age_limit")
    var ageLimit: Int = 18,

    @ColumnInfo(name = "poster")
    var imageUrl: String,

    @ColumnInfo(name = "actors")
    @TypeConverters(Converters::class)
    var actorsList: List<String>? = null
)

@Entity(tableName = "actor_table")
data class Actor(
    @PrimaryKey(autoGenerate = true)
    var actorId: Long = 0L,

    @ColumnInfo(name = "actor")
    val actor: String,

    @ColumnInfo(name = "poster")
    var imageUrl: String,

    @ColumnInfo(name = "filmography")
    @TypeConverters(Converters::class)
    var moviesList: List<String>? = null
)

@Entity(primaryKeys = ["movieId", "actorId"])
data class MovieActorXRef(
    val movie: Long,
    val actorId: Long
)

data class MovieCast(
    @Embedded val movie: Movie,
    @Relation(
        parentColumn = "movieId",
        entityColumn = "actorId",
        associateBy = Junction(MovieActorXRef::class)
    )
    val actors: List<Actor>
)

data class ActorMovies(
    @Embedded val actor: Actor,
    @Relation(
        parentColumn = "actorId",
        entityColumn = "movieId",
        associateBy = Junction(MovieActorXRef::class)
    )
    val movies: List<Movie>
)



