package com.jsdr.watchapp.data.firebase

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.jsdr.watchapp.data.models.entities.Rating
import com.jsdr.watchapp.data.models.entities.User
import com.jsdr.watchapp.data.models.entities.UserList
import kotlinx.coroutines.tasks.await

//val testUsr = User(
//    uid = "test_user",
//    email = "test@example.com",
//    username = "test_user_123",
//    pfpPath = "pfp.png",
//    watchedMoviesCount = 0,
//    watchedTvSeriesCount = 0,
//    favouritesCount = 0,
//    ratingsCount = 0
//)
//
//val favouritesTemplate = UserList(
//    name = "Ulubione",
//    description = "Filmy i seriale, które wyjątkowo doceniłeś",
//    movies = emptyList(),
//    series = emptyList()
//)
//
//val bucketlistTemplate = UserList(
//    name = "Kupka Wstydu",
//    description = "Filmy i seriale, które już dawno powinieneś był obejrzeć",
//    movies = emptyList(),
//    series = emptyList()
//)
//
//val watchedTemplate = UserList(
//    name = "Obejrzane",
//    description = "Filmy i seriale, które już obejrzałeś",
//    movies = emptyList(),
//    series = emptyList()
//)
//
//val customListTest = UserList(
//    name = "Guilty Pleasures",
//    description = "Słabe produkcje, dobra zabawa",
//    movies = listOf(1022690),
//    series = listOf()
//)
//
//val ratingTest = Rating(
//    "11",
//    7.0,
//    7.0,
//    8.0,
//    5.0,
//    7.0
//)


object WatchAppFirestore {
    val firestoreDb by lazy { Firebase.firestore }

//    suspend fun initialSeeding() {
//        try {
//            firestoreDb.collection("users").document(testUsr.uid).set(testUsr).await()
//            firestoreDb.collection("users").document(testUsr.uid).collection("lists").document("favourites").set(favouritesTemplate).await()
//            firestoreDb.collection("users").document(testUsr.uid).collection("lists").document("bucketlist").set(bucketlistTemplate).await()
//            Ratings.addMovieRating(testUsr.uid, ratingTest)
//            Movies.addMovieToFavourites(testUsr.uid, 11)
//            Movies.addMovieToBucketlist(testUsr.uid, 11)
//            Lists.createUserList(testUsr.uid, customListTest)
//            Movies.addMovieToListByListName(testUsr.uid, "Guilty Pleasures", 11)
//        } catch (e: Exception) {
//            Log.e("MovieFirestore", "Seeding Failure", e)
//        }
//    }

    object Users {

        suspend fun addUser(user: User) {
            firestoreDb.collection("users")
                .document(user.uid)
                .set(user)
                .await()
        }

        suspend fun deleteUser(userId: String) {
            try {
                firestoreDb.collection("users").document(userId).delete().await()
            } catch (e: Exception) { Log.e("WatchAppFirestore", "Could not delete user", e) }
        }

        suspend fun getUserStats(userId: String): Map<String, Int> {
            return try {
                val snap = firestoreDb.collection("users")
                    .document(userId)
                    .get()
                    .await()
                val watchedMovies = snap.get("watchedMoviesCount") as? Int ?: 0
                val watchedTvSeries = snap.get("watchedTvSeriesCount") as? Int ?: 0
                val favourites = snap.get("favouritesCount") as? Int ?: 0
                val ratings = snap.get("ratingsCount") as? Int ?: 0
                mapOf(
                    "watchedMovies" to watchedMovies,
                    "watchedSeries" to watchedTvSeries,
                    "totalFavourites" to favourites,
                    "totalRatings" to ratings
                )
            } catch (e: Exception) {
                Log.e("Movie Firestore", "Could not get movies watched by user: $userId", e)
                emptyMap()
            }
        }

        suspend fun getUserLists(userId: String): List<UserList> {
            return try {
                val snapshot = firestoreDb.collection("users")
                    .document(userId)
                    .collection("lists")
                    .get()
                    .await()
                snapshot.toObjects(UserList::class.java)
            } catch (e: Exception) {
                Log.e("Movie Firestore", "Could not receive Lists of user: $userId", e)
                emptyList()
            }
        }

        suspend fun getUserRatings(userId: String): List<Rating> {
            return try {
                val snap = firestoreDb.collection("users")
                    .document(userId)
                    .collection("ratings")
                    .get()
                    .await()
                snap.toObjects(Rating::class.java)
            } catch (e: Exception) {
                Log.e("Movie Firestore", "Could not receive ratings of user: $userId", e)
                emptyList()
            }
        }

        object Updates {
            suspend fun updateUsername(userId: String, username: String) {
                firestoreDb.collection("users")
                    .document(userId)
                    .update("username", username)
                    .await()
            }

            suspend fun updateGender(userId: String, gender: String) {
                if (gender == "male" || gender == "female") {
                    firestoreDb.collection("users")
                        .document(userId)
                        .update("gender", gender)
                        .await()
                }
                return
            }

            suspend fun updateBirthYear(userId: String, year: Int) {
                firestoreDb.collection("users")
                    .document(userId)
                    .update("birthYear", year)
                    .await()
            }

            suspend fun updateEmail(userId: String, email: String) {
                firestoreDb.collection("users")
                    .document(userId)
                    .update("email", email)
                    .await()
            }
        }

    }

    object Lists {

        suspend fun createUserList(userId: String, newList: UserList) {
            try {
                // list names must differ so implement name comparison with transaction
                val listsCollection = firestoreDb.collection("users")
                    .document(userId)
                    .collection("lists")
                if (newList.id.isNotEmpty()) {
                    listsCollection.document(newList.id).set(newList).await()
                } else {
                    listsCollection.document().set(newList).await()
                }
            } catch (e: Exception) {
                Log.e("MovieFirestore", "Could not add user list to database", e)
            }
        }

        suspend fun deleteUserList(userId: String, listId: String) {
            if (listId == "favourites" || listId == "bucketlist" || listId == "watched") {
                throw Exception("Illegal Action! Cannot delete default lists")
            }
            try {
                firestoreDb.collection("users")
                    .document(userId)
                    .collection("lists")
                    .document(listId)
                    .delete()
                    .await()
            } catch (e: Exception) {
                Log.e("WatchApp Firestore", "Could not delete user list", e)
            }
        }

        suspend fun getListsContainingMovie(userId: String, movieId: String): List<String> {
            return try {
                val snapshot = firestoreDb.collection("users")
                    .document(userId)
                    .collection("lists")
                    .whereArrayContains("movies", movieId.toInt())
                    .get()
                    .await()
                snapshot.documents.mapNotNull { it.getString("name") }
            } catch (e: Exception) {
                Log.e("Movie Repository", "Could not receive Lists", e)
                emptyList()
            }
        }

    }

    object Ratings {
        suspend fun getMovieRating(userId: String, movieId: String): Rating? {
            return try {
                val result = firestoreDb.collection("users")
                    .document(userId)
                    .collection("ratings")
                    .document(movieId)
                    .get()
                    .await()
                result.toObject(Rating::class.java)
            } catch (e: Exception) {
                Log.e("Movie Repository", "Could not receive Rating", e)
                print("Error: could not access document with rating")
                null
            }
        }

        suspend fun addMovieRating(userId: String, rating: Rating) {
            // may be improved with 'transaction'
            try {
                val snap = firestoreDb.collection("users")
                    .document(userId)
                    .collection("ratings")
                    .document(rating.movieId)
                    .get()
                    .await()
                if (snap.exists()) { throw Exception("Rating for this movie already exists") }
                firestoreDb.collection("users")
                    .document(userId)
                    .collection("ratings")
                    .document(rating.movieId)
                    .set(rating)
                    .await()
            } catch (e: Exception) {
                Log.e("MovieFirestore", "Could not add movie rating", e)
            }
        }

        suspend fun deleteMovieRating(userId: String, movieId: String) {
            try {
                firestoreDb.collection("users")
                    .document(userId)
                    .collection("ratings")
                    .document(movieId)
                    .delete()
                    .await()
            } catch (e: Exception) {
                Log.e("WatchAppFirestore", "Could not remove movie rating", e)
            }
        }

    }

    object Movies {

        //suspend fun addCustomMovie(userId: String) {}
        //suspend delete addCustomMovie(userId: String) {}


        // two following methods might be of no use
        fun addMovieToFavourites(userId: String, movieId: Int) {
            try {
                firestoreDb.collection("users")
                    .document(userId)
                    .collection("lists")
                    .document("favourites")
                    .update("movies", FieldValue.arrayUnion(movieId))
            } catch (e: Exception) {
                Log.e("MovieFirestore", "Could not add movie to favourites", e)
            }
        }

        fun addMovieToBucketlist(userId: String, movieId: Int) {
            try {
                firestoreDb.collection("users")
                    .document(userId)
                    .collection("lists")
                    .document("bucketlist")
                    .update("movies", FieldValue.arrayUnion(movieId))
            } catch (e: Exception) {
                Log.e("MovieFirestore", "Could not add movie to bucketlist", e)
            }
        }

        fun addMovieToListById(userId: String, listId: String, movieId: Int) {
            try {
              firestoreDb.collection("users")
                    .document(userId)
                    .collection("lists")
                    .document(listId)
                    .update("movies", FieldValue.arrayUnion(movieId))
            } catch (e: Exception) {
                Log.e("MovieFirestore", "Could not add movie to bucketlist", e)
            }
        }

        suspend fun addMovieToListByListName(userId: String, listName: String, movieId: Int) { //provided that lists are named uniquely
            try {
                val snap = firestoreDb.collection("users")
                    .document(userId)
                    .collection("lists")
                    .whereEqualTo("name", listName)
                    .limit(1)
                    .get()
                    .await()
                if (snap.isEmpty) {
                    throw Exception("No list with such name in database!")
                }
                val listDoc = snap.documents[0].reference
                listDoc.update("movies", FieldValue.arrayUnion(movieId))
            } catch (e: Exception) {
                Log.e("MovieFirestore", "Could not add movie to bucketlist", e)
            }
        }

        suspend fun markMovieAsWatched(userId: String, movieId: Int) {
            try {
                firestoreDb.collection("users")
                    .document(userId)
                    .update(
                        "watchedMoviesCount", FieldValue.increment(1),
                        "watchedMovies", FieldValue.arrayUnion(movieId)
                    ).await()
            } catch (e: Exception) {
                Log.e("Movie Firestore", "Could not mark movie as watched", e)
            }
        }

        suspend fun getWatchedMovies(userId: String): List<Int>? {
            return try {
                val snap = firestoreDb.collection("users")
                    .document(userId)
                    .get()
                    .await()
                if (!snap.exists()) {
                    throw Exception("Could not find such user")
                }
                val watchedList = snap.get("watchedMovies") as? List<*>
                watchedList?.map { it.toString().toInt() }
            } catch (e: Exception) {
                Log.e("Movie Firestore", "Could not get watched movies", e)
                null
            }
        }

        fun removeMovieFromList(userId: String, listId: String, movieId: Int) {
            try {
                firestoreDb.collection("users")
                    .document(userId)
                    .collection("lists")
                    .document(listId)
                    .update("movies", FieldValue.arrayRemove(movieId))
            } catch (e: Exception) {
                Log.e("WatchAppFirestore", "Could not remove movie from list", e)
            }

        }

        suspend fun removeMovieFromListByName(userId: String, listName: String, movieId: Int) {
            try {
                val snap = firestoreDb.collection("users")
                    .document(userId)
                    .collection("lists")
                    .whereEqualTo("name", listName)
                    .get()
                    .await()
                if (snap.isEmpty) {
                    throw Exception("No list with such name in database!")
                }
                val listDoc = snap.documents[0].reference
                listDoc.update("movies", FieldValue.arrayRemove(movieId))
            } catch (e: Exception) {
                Log.e("WatchAppFirestore", "Could not remove movie from list", e)
            }
        }


        //suspend fun removeMovieFromFavourites(userId: String, movieId: Int) {}
        //suspend fun removeMovieFromBucketlist(userId: String, movieId: Int) {}
    }
}
