package com.jsdr.watchapp.data.firebase

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.SetOptions
import com.jsdr.watchapp.data.models.entities.Rating
import com.jsdr.watchapp.data.models.entities.User
import com.jsdr.watchapp.data.models.entities.UserList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

object WatchAppFirestore {
    val firestoreDb by lazy { Firebase.firestore }

    private fun updateCounter(batch: WriteBatch, userId: String, counter: String /*can be changed to enum*/, increment: Boolean) {
        val userProfileRef = firestoreDb.collection("users").document(userId)
        batch.update(userProfileRef, "${counter}Count", FieldValue.increment(1))
    }

    private fun getRatingId(mediaId: Int, isMovie: Boolean): String {
        return if (isMovie) "movie_$mediaId" else "tv_$mediaId"
    }

    object Users {

        suspend fun getCurrentUser(userId: String): User? {
            return try {
                val snap = firestoreDb.collection("users").document(userId).get().await()
                snap.toObject(User::class.java)
            } catch (_: Exception) {
                Log.e("WatchApp Firestore", "Could not get current user data")
                null
            }
        }

        fun getCurrentUserFlow(userId: String): Flow<User?> {
            return firestoreDb.collection("users").document(userId)
                .snapshots()
                .map { snapshot -> snapshot.toObject(User::class.java) }
        }

        suspend fun addUser(user: User) {
            try {
                firestoreDb.collection("users").document(user.uid).set(user).await() }
            catch (e: Exception) { Log.e("WatchAppFirestore", "Could not add user", e) }
        }

        suspend fun deleteUser(userId: String) {
            try {
                firestoreDb.collection("users").document(userId).delete().await() }
            catch (e: Exception) { Log.e("WatchAppFirestore", "Could not delete user", e) }

        }

        suspend fun getUserStats(userId: String): Map<String, Double> {
            return try {
                val snap = firestoreDb.collection("users")
                    .document(userId)
                    .get()
                    .await()
                val watchedMovies = snap.get("watchedMoviesCount") as? Double ?: 0.0
                val watchedTvSeries = snap.get("watchedTvSeriesCount") as? Double ?: 0.0
                val favourites = snap.get("favouritesCount") as? Double ?: 0.0
                val ratings = snap.get("ratingsCount") as? Double ?: 0.0
                val lists = snap.get("totalListCount") as? Double ?: 0.0
                val averageRating = Ratings.getAverageRating(userId) ?: 0.0
                mapOf(
                    "watchedMovies" to watchedMovies,
                    "watchedSeries" to watchedTvSeries,
                    "totalFavourites" to favourites,
                    "totalRatings" to ratings,
                    "totalLists" to lists,
                    "averageRating" to averageRating
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
                Log.e("WatchAppFirestore", "Could not receive ratings of user: $userId", e)
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
            val listsCollection = firestoreDb.collection("users").document(userId).collection("lists")
            val userRef = firestoreDb.collection("users").document(userId)
            val existingLists = listsCollection.whereEqualTo("name", newList.name).get().await()
            if (!existingLists.isEmpty) {
                throw Exception("List with such name '${newList.name}' already exists!")
            }
            val batch = firestoreDb.batch()
            val listDocRef = if (newList.id.isNotEmpty()) listsCollection.document(newList.id) else listsCollection.document()
            val listToSave = newList.copy(id = listDocRef.id)
            batch.set(listDocRef, listToSave)
            batch.update(userRef, "totalListCount", FieldValue.increment(1))
            batch.commit().await()
        }

        suspend fun deleteUserList(userId: String, listId: String) {
            if (listId in listOf("favourites", "bucketlist", "watched")) {
                throw Exception("Illegal Action! Cannot delete default lists")
            }
            try {
                val batch = firestoreDb.batch()
                val listRef = firestoreDb.collection("users").document(userId).collection("lists").document(listId)
                val userRef = firestoreDb.collection("users").document(userId)

                batch.delete(listRef)
                batch.update(userRef, "totalListCount", FieldValue.increment(-1)) // Zmniejszamy licznik

                batch.commit().await()
            } catch (e: Exception) {
                Log.e("WatchApp Firestore", "Could not delete user list", e)
            }
        }

        suspend fun getListsContainingMedia(userId: String, mediaId: Int, isMovie: Boolean): List<String> {
            return try {
                val arrayField = if (isMovie) "movies" else "series"
                val snapshot = firestoreDb.collection("users")
                    .document(userId)
                    .collection("lists")
                    .whereArrayContains(arrayField, mediaId)
                    .get()
                    .await()
                snapshot.documents.mapNotNull { it.getString("name") }
            } catch (e: Exception) {
                Log.e("WatchAppFirestore", "Could not receive Lists", e)
                emptyList()
            }
        }
    }

    object Ratings {
        suspend fun getMediaRating(userId: String, mediaId: Int, isMovie: Boolean): Rating? {
            val docId = getRatingId(mediaId, isMovie)
            return try {
                val result = firestoreDb.collection("users").document(userId)
                    .collection("ratings").document(docId).get().await()
                result.toObject(Rating::class.java)
            } catch (e: Exception) {
                null
            }
        }
        suspend fun addMediaRating(userId: String, mediaId: Int, isMovie: Boolean, rating: Rating) {
            val docId = getRatingId(mediaId, isMovie)
            val ratingRef = firestoreDb.collection("users").document(userId).collection("ratings").document(docId)
            val userRef = firestoreDb.collection("users").document(userId)
            try {
                firestoreDb.runTransaction { transaction ->
                    val snapshot = transaction.get(ratingRef)
                    if (snapshot.exists()) {
                        throw Exception("Rating for this media already exists! Use update instead.")
                    }
                    transaction.set(ratingRef, rating)
                    transaction.update(userRef, "ratingsCount", FieldValue.increment(1))
                }.await()
            } catch (e: Exception) {
                Log.e("MovieFirestore", "Could not add media rating", e)
                throw e
            }
        }

        suspend fun updateMediaRating(userId: String, mediaId: Int, isMovie: Boolean, newRating: Rating) {
            val docId = getRatingId(mediaId, isMovie)
            val ratingRef = firestoreDb.collection("users").document(userId).collection("ratings").document(docId)
            ratingRef.set(newRating, SetOptions.merge()).await()
        }

        suspend fun deleteMediaRating(userId: String, mediaId: Int, isMovie: Boolean) {
            val docId = getRatingId(mediaId, isMovie)
            val batch = firestoreDb.batch()
            val ratingRef = firestoreDb.collection("users").document(userId).collection("ratings").document(docId)
            val userRef = firestoreDb.collection("users").document(userId)
            batch.delete(ratingRef)
            batch.update(userRef, "ratingsCount", FieldValue.increment(-1))
            batch.commit().await()
        }

        suspend fun getAverageRating(userId: String): Double? {
            return try {
                val snap = firestoreDb.collection("users")
                    .document(userId)
                    .collection("ratings")
                    .get()
                    .await()
                if (snap.isEmpty) return 0.0
                val sum = snap.documents.sumOf { it.getDouble("overallRating") ?: 0.0 }
                sum / snap.documents.size
            } catch (e: Exception) {
                Log.e("WatchApp Firestore", "Could not access user ratings of user $userId", e)
                null
            }
        }
    }

    object Media {

        //suspend fun addCustomMedia(userId: String) {}
        //suspend delete addCustomMedia(userId: String) {}

        suspend fun addMediaToList(userId: String, listId: String, mediaId: Int, isMovie: Boolean) {
            val userRef = firestoreDb.collection("users").document(userId)
            val listRef = userRef.collection("lists").document(listId)
            val arrayField = if (isMovie) "movies" else "series"
            val batch = firestoreDb.batch()
            batch.update(listRef, arrayField, FieldValue.arrayUnion(mediaId))
            when (listId) {
                "watched" -> {
                    val counterField = if (isMovie) "watchedMoviesCount" else "watchedTvSeriesCount"
                    batch.update(userRef, counterField, FieldValue.increment(1L))
                }
                "favourites" -> {
                    batch.update(userRef, "favouritesCount", FieldValue.increment(1L))
                }
            }
            try {
                batch.commit().await()
            } catch (e: Exception) {
                Log.e("WatchAppFirestore", "Could not add media to list: $listId", e)
                throw e
            }
        }

        suspend fun addMediaToListByName(userId: String, listName: String, mediaId: Int, isMovie: Boolean) {
            val snap = firestoreDb.collection("users").document(userId)
                .collection("lists").whereEqualTo("name", listName).limit(1).get().await()
            if (snap.isEmpty) throw Exception("No list with name '$listName' in database!")
            val listId = snap.documents[0].id
            addMediaToList(userId, listId, mediaId, isMovie)
        }

        suspend fun removeMediaFromList(userId: String, listId: String, mediaId: Int, isMovie: Boolean) {
            val userRef = firestoreDb.collection("users").document(userId)
            val listRef = userRef.collection("lists").document(listId)
            val arrayField = if (isMovie) "movies" else "series"
            val batch = firestoreDb.batch()
            batch.update(listRef, arrayField, FieldValue.arrayRemove(mediaId))
            when (listId) {
                "watched" -> {
                    val counterField = if (isMovie) "watchedMoviesCount" else "watchedTvSeriesCount"
                    batch.update(userRef, counterField, FieldValue.increment(-1L))
                }
                "favourites" -> {
                    batch.update(userRef, "favouritesCount", FieldValue.increment(-1L))
                }
            }
            try {
                batch.commit().await()
            } catch (e: Exception) {
                Log.e("WatchAppFirestore", "Could not remove media from list: $listId", e)
                throw e
            }
        }

        suspend fun removeMediaFromListByName(userId: String, listName: String, mediaId: Int, isMovie: Boolean) {
            val snap = firestoreDb.collection("users").document(userId)
                .collection("lists").whereEqualTo("name", listName).limit(1).get().await()
            if (snap.isEmpty) throw Exception("No list with name '$listName' in database!")
            val listId = snap.documents[0].id
            removeMediaFromList(userId, listId, mediaId, isMovie)
        }
    }
}