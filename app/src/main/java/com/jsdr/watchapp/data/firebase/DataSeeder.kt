package com.jsdr.watchapp.data.firebase

import com.jsdr.watchapp.data.models.entities.Rating
import com.jsdr.watchapp.data.models.entities.User
import com.jsdr.watchapp.data.models.entities.UserList
import com.jsdr.watchapp.data.repository.WatchAppRepository
import kotlin.collections.emptyList

object DataSeeder {
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
        //moviesAddedToListsCount = 0,
        //averageRating = 0.0
    )

    private val mediaForSeeding: Map<Int, Boolean> = mapOf(
        1339713 to true,
        1304313 to true,
        687163 to true,
        1083381 to true,
        1226863 to true,
        124364 to false,
        76479 to false,
        220102 to false,
        1399 to false,
        1911 to false,
        95479 to false
    )

    val favouritesTemplate = UserList(
        id = "favourites",
        name = "Ulubione",
        description = "Filmy i seriale, które wyjątkowo doceniłeś",
        movies = emptyList(),
        series = emptyList()
    )

    val bucketlistTemplate = UserList(
        id = "bucketlist",
        name = "Kupka Wstydu",
        description = "Filmy i seriale, które już dawno powinieneś był obejrzeć",
        movies = emptyList(),
        series = emptyList()
    )

    val watchedTemplate = UserList(
        id = "watched",
        name = "Obejrzane",
        description = "Filmy i seriale, które już obejrzałeś",
        movies = emptyList(),
        series = emptyList()
    )

    val ratedTemplate = UserList(
        id = "rated",
        name = "Ocenione",
        description = "Filmy i seriale, które oceniłeś",
        movies = emptyList(),
        series = emptyList()
    )

    private val customListTest = UserList(
        name = "Guilty Pleasures",
        description = "Słabe produkcje, dobra zabawa",
        movies = emptyList(),
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

    suspend fun performInitialSeeding() {
        WatchAppFirestore.Users.addUser(testUsr)
        WatchAppFirestore.Lists.createUserList(testUsr.uid, customListTest)
        WatchAppFirestore.Lists.createUserList(testUsr.uid, bucketlistTemplate)
        WatchAppFirestore.Lists.createUserList(testUsr.uid, favouritesTemplate)
        WatchAppFirestore.Lists.createUserList(testUsr.uid, watchedTemplate)
        WatchAppFirestore.Ratings.addMediaRating(testUsr.uid, 11, true, ratingTest)
        WatchAppFirestore.Media.addMediaToList(testUsr.uid, "favourites", 11, true)
        WatchAppFirestore.Media.addMediaToList(testUsr.uid, "favourites", 60625, false)
        WatchAppFirestore.Media.addMediaToList(testUsr.uid, "bucketlist", 60625, false)
        WatchAppFirestore.Media.addMediaToList(testUsr.uid, "watched", 11, true)
    }

    suspend fun testUserDataUpdates() {
        WatchAppFirestore.Users.Updates.updateUsername(testUsr.uid, "test_user_456")
        WatchAppFirestore.Users.Updates.updateGender(testUsr.uid, "female")
        WatchAppFirestore.Users.Updates.updateBirthYear(testUsr.uid, 1999)
        WatchAppFirestore.Users.Updates.updateEmail(testUsr.uid, "newtest@email-example.com")
    }

    suspend fun seedMedia() {
        for (media in mediaForSeeding) {
            WatchAppFirestore.Media.addMediaToList(testUsr.uid, "favourites", media.key, media.value)
            WatchAppFirestore.Media.addMediaToList(testUsr.uid, "bucketlist", media.key, media.value)
            WatchAppFirestore.Media.addMediaToList(testUsr.uid, "watched", media.key, media.value)
        }
    }

    suspend fun testUserRegistration() {
        WatchAppRepository.Auth.registerUser("jakub.sosna@o2.pl", "test123", "2gub4")
    }
}