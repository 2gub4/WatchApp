package com.jsdr.watchapp.data.firebase

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
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

    private fun getRatingId(mediaId: Int, isMovie: Boolean): String {
        return if (isMovie) "movie_$mediaId" else "tv_$mediaId"
    }

    object Users {

        suspend fun getCurrentUser(userId: String): User? {
            return try {
                val snap = firestoreDb.collection("users").document(userId).get().await()
                snap.toObject(User::class.java)
            } catch (_: Exception) {
                //Log.e("WatchApp Firestore", "Could not get current user data")
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
                val batch = firestoreDb.batch()
                val userRef = firestoreDb.collection("users").document(user.uid)
                batch.set(userRef, user)
                val listsRef = userRef.collection("lists")
                batch.set(listsRef.document(DataSeeder.favouritesTemplate.id), DataSeeder.favouritesTemplate)
                batch.set(listsRef.document(DataSeeder.watchedTemplate.id), DataSeeder.watchedTemplate)
                batch.set(listsRef.document(DataSeeder.bucketlistTemplate.id), DataSeeder.bucketlistTemplate)
                batch.commit().await()
            } catch (e: Exception) {
                Log.e("WatchAppFirestore", "Error creating user with default lists", e)
            }
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
                val user = snap.toObject(User::class.java)
                val averageRating = Ratings.getAverageRating(userId) ?: 0.0
                mapOf(
                    "watchedMovies" to (user?.watchedMoviesCount?.toDouble() ?: 0.0),
                    "watchedSeries" to (user?.watchedTvSeriesCount?.toDouble() ?: 0.0),
                    "totalFavourites" to (user?.favouritesCount?.toDouble() ?: 0.0),
                    "totalRatings" to (user?.ratingsCount?.toDouble() ?: 0.0),
                    "totalLists" to (user?.totalListCount?.toDouble() ?: 0.0),
                    "averageRating" to averageRating
                )
            } catch (e: Exception) {
                //Log.e("WatchApp Firestore", "Could not get user stats for user: $userId", e)
                emptyMap()
            }
        }

        suspend fun getUserLists(userId: String): List<UserList> {
            if (userId.isEmpty()) return emptyList()
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

        suspend fun getUsernames(): List<String> {
            return try {
                val snap = firestoreDb.collection("users").get().await()
                snap.documents.mapNotNull { document ->
                    document.getString("username")
                }
            }
            catch (e: Exception) {
                Log.e("WatchAppFirestore", "Could not receive list of usernames", e)
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
                if (gender == "Pan" || gender == "Pani") {
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
                val listRef = firestoreDb.collection("users").document(userId).collection("lists").document(listId)
                val userRef = firestoreDb.collection("users").document(userId)
                firestoreDb.runTransaction { transaction ->
                    val snapshot = transaction.get(listRef)
                    if (snapshot.exists()) {
                        transaction.delete(listRef)
                        transaction.update(userRef, "totalListCount", FieldValue.increment(-1L))
                    }
                }.await()
            } catch (e: Exception) {
                Log.e("WatchApp Firestore", "Could not delete user list with id: $listId", e)
            }
        }

//        suspend fun changeListName(userId: String, listId: String, newName: String) {
//            try {
//                val listsRef = firestoreDb.collection("users").document(userId).collection("lists")
//                //dokończyć
//                val listToChange = listsRef.document(listId)
//                listToChange.update("name", newName).await()
//            }
//        }


        suspend fun getListsContainingMedia(userId: String, mediaId: Int, isMovie: Boolean): List<String> {
            if (userId.isEmpty()) return emptyList()
            return try {
                val arrayField = if (isMovie) "movies" else "series"
                val snapshot = firestoreDb.collection("users")
                    .document(userId)
                    .collection("lists")
                    .whereArrayContains(arrayField, mediaId)
                    .get()
                    .await()
                snapshot.documents.map { it.id }
            } catch (e: Exception) {
                Log.e("WatchAppFirestore", "Could not receive Lists", e)
                emptyList()
            }
        }
    }

    object Ratings {
        suspend fun getMediaRating(userId: String, mediaId: Int, isMovie: Boolean): Rating? {
            if (userId.isEmpty()) return null
            val docId = getRatingId(mediaId, isMovie)
            return try {
                val result = firestoreDb.collection("users").document(userId)
                    .collection("ratings").document(docId).get().await()
                result.toObject(Rating::class.java)
            } catch (_: Exception) {
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
                    transaction.update(userRef, "ratingsCount", FieldValue.increment(1L))
                }.await()
            } catch (e: Exception) {
                Log.e("MovieFirestore", "Could not add media rating", e)
                throw e
            }
        }

        suspend fun deleteMediaRating(userId: String, mediaId: Int, isMovie: Boolean) {
            val docId = getRatingId(mediaId, isMovie)
            val ratingRef = firestoreDb.collection("users").document(userId).collection("ratings").document(docId)
            val userRef = firestoreDb.collection("users").document(userId)
            try {
                firestoreDb.runTransaction { transaction ->
                    val snapshot = transaction.get(ratingRef)
                    if (snapshot.exists()) {
                        transaction.delete(ratingRef)
                        transaction.update(userRef, "ratingsCount", FieldValue.increment(-1L))
                    }
                }.await()
            } catch (e: Exception) {
                Log.e("WatchAppFirestore", "Could not delete media rating", e)
                throw e
            }
        }

        suspend fun updateMediaRating(userId: String, mediaId: Int, isMovie: Boolean, newRating: Rating) {
            val docId = getRatingId(mediaId, isMovie)
            val ratingRef = firestoreDb.collection("users").document(userId).collection("ratings").document(docId)
            ratingRef.set(newRating, SetOptions.merge()).await()
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
            try {
                firestoreDb.runTransaction { transaction ->
                    val listSnapshot = transaction.get(listRef)
                    val currentArray = listSnapshot.get(arrayField) as? List<Number> ?: emptyList()
                    val isAlreadyInList = currentArray.any { it.toInt() == mediaId }
                    if (!isAlreadyInList) {
                        transaction.update(listRef, arrayField, FieldValue.arrayUnion(mediaId))
                        when (listId) {
                            "watched" -> {
                                val counterField = if (isMovie) "watchedMoviesCount" else "watchedTvSeriesCount"
                                transaction.update(userRef, counterField, FieldValue.increment(1L))
                                val bucketlistRef = userRef.collection("lists").document("bucketlist")
                                transaction.update(bucketlistRef, arrayField, FieldValue.arrayRemove(mediaId))
                            }
                            "favourites" -> {
                                transaction.update(userRef, "favouritesCount", FieldValue.increment(1L))
                            }
                        }
                    }
                }.await()
            } catch (e: Exception) {
                Log.e("WatchAppFirestore", "Could not add media to list: $listId", e)
                throw e
            }
        }

        suspend fun removeMediaFromList(userId: String, listId: String, mediaId: Int, isMovie: Boolean) {
            val userRef = firestoreDb.collection("users").document(userId)
            val listRef = userRef.collection("lists").document(listId)
            val arrayField = if (isMovie) "movies" else "series"

            try {
                firestoreDb.runTransaction { transaction ->
                    val listSnapshot = transaction.get(listRef)
                    val currentArray = listSnapshot.get(arrayField) as? List<Number> ?: emptyList()
                    val isInList = currentArray.any { it.toInt() == mediaId }
                    if (isInList) {
                        transaction.update(listRef, arrayField, FieldValue.arrayRemove(mediaId))
                        when (listId) {
                            "watched" -> {
                                val counterField = if (isMovie) "watchedMoviesCount" else "watchedTvSeriesCount"
                                transaction.update(userRef, counterField, FieldValue.increment(-1L))
                            }
                            "favourites" -> {
                                transaction.update(userRef, "favouritesCount", FieldValue.increment(-1L))
                            }
                        }
                    }
                }.await()
            } catch (e: Exception) {
                Log.e("WatchAppFirestore", "Could not remove media from list: $listId", e)
                throw e
            }
        }
    }
}