package ru.mtsteta.flixnet.fakeRepo

import ru.mtsteta.flixnet.movies.MovieDto

class MoviesDataSourceImpl : MoviesDataSource {

    val genreList = listOf<String>(
        "Боевик",
        "Комедия",
        "Приключения",
        "Мультик",
        "Криминал",
        "Документальное",
        "Драма",
        "Семейный",
        "Фантастика",
        "Исторический",
        "Ужасы",
        "Научная фантастика",
        "Рельное ТВ",
    )

    override fun getMovies() = listOf(
        MovieDto(
            title = "Гнев человеческий",
            description = "Эйч — загадочный и холодный на вид джентльмен, но внутри него пылает жажда справедливости. Преследуя...",
            rateScore = 3,
            ageLimit = 18,
            genre = genreList.random(),
            imageUrl = "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/5JP9X5tCZ6qz7DYMabLmrQirlWh.jpg"
        ),
        MovieDto(
            title = "Мортал Комбат",
            description = "Боец смешанных единоборств Коул Янг не раз соглашался проиграть за деньги. Он не знает о своем наследии...",
            rateScore = 5,
            ageLimit = 18,
            genre = genreList.random(),
            imageUrl = "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/pMIixvHwsD5RZxbvgsDSNkpKy0R.jpg"
        ),
        MovieDto(
            title = "Упс... Приплыли!",
            description = "От Великого потопа зверей спас ковчег. Но спустя полгода скитаний они готовы сбежать с него куда угодно...",
            rateScore = 5,
            ageLimit = 6,
            genre = genreList.random(),
            imageUrl = "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/546RNYy9Wi5wgboQ7EtD6i0DY5D.jpg"
        ),
        MovieDto(
            title = "The Box",
            description = "Уличный музыкант знакомится с музыкальным продюсером, и они вдвоём отправляются в путешествие...",
            rateScore = 4,
            ageLimit = 12,
            genre = genreList.random(),
            imageUrl = "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/fq3DSw74fAodrbLiSv0BW1Ya4Ae.jpg"
        ),
        MovieDto(
            title = "Сага о Дэнни Эрнандесе",
            description = "Tekashi69 или Сикснайн — знаменитый бруклинский рэпер с радужными волосами — прогремел...",
            rateScore = 2,
            ageLimit = 18,
            genre = genreList.random(),
            imageUrl = "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/5xXGQLVtTAExHY92DHD9ewGmKxf.jpg"
        ),
        MovieDto(
            title = "Пчелка Майя",
            description = "Когда упрямая пчелка Майя и ее лучший друг Вилли спасают принцессу-муравьишку, начинается сказочное...",
            rateScore = 4,
            ageLimit = 0,
            genre = genreList.random(),
            imageUrl = "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/xltjMeLlxywym14NEizl0metO10.jpg"
        ),
        MovieDto(
            title = "Круэлла",
            description = "Невероятно одаренная мошенница по имени Эстелла решает сделать себе имя в мире моды.",
            rateScore = 4,
            ageLimit = 12,
            genre = genreList.random(),
            imageUrl = "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/hUfyYGP9Xf6cHF9y44JXJV3NxZM.jpg"
        ),
        MovieDto(
            title = "Чёрная вдова",
            description = "Чёрной Вдове придется вспомнить о том, что было в её жизни задолго до присоединения к команде Мстителей",
            rateScore = 3,
            ageLimit = 16,
            genre = genreList.random(),
            imageUrl = "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/mbtN6V6y5kdawvAkzqN4ohi576a.jpg"
        ),
    )
}