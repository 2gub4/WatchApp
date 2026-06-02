package com.jsdr.watchapp.data.firebase

import com.jsdr.watchapp.data.models.entities.Rating
import com.jsdr.watchapp.data.models.entities.User
import com.jsdr.watchapp.data.models.entities.UserList

class DataSeeder {
    private val testUsr = User(
        uid = "test_user",
        email = "test@example.com",
        username = "test_user_123",
        pfpPath = "pfp.png",
        birthYear = 2004,
        gender = "male",
        registrationDate = null,
        watchedMoviesCount = 0,
        watchedTvSeriesCount = 0,
        favouritesCount = 0,
        ratingsCount = 0,
        totalListCount = 0,
        moviesAddedToListsCount = 0,
        averageRating = 0.0
    )

    private val favouritesTemplate = UserList(
        id = "favourites",
        name = "Ulubione",
        description = "Filmy i seriale, które wyjątkowo doceniłeś",
        movies = listOf(11),
        series = listOf(76479)
    )

    private val bucketlistTemplate = UserList(
        id = "bucketlist",
        name = "Kupka Wstydu",
        description = "Filmy i seriale, które już dawno powinieneś był obejrzeć",
        movies = listOf(1228710),
        series = listOf(220102)
    )

    private val watchedTemplate = UserList(
        id = "watched",
        name = "Obejrzane",
        description = "Filmy i seriale, które już obejrzałeś",
        movies = listOf(11, 803796),
        series = listOf(76479)
    )

    private val customListTest = UserList(
        name = "Guilty Pleasures",
        description = "Słabe produkcje, dobra zabawa",
        movies = listOf(1022690),
        series = emptyList()
    )

    private val ratingTest = Rating(
        "11",
        7.0,
        7.0,
        8.0,
        5.0,
        7.0,
        "fajny film, ale trochę się zestarzał"
    )

    fun performInitialSeeding() {

    }
}