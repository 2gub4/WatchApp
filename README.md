To run this project you need to complete the following steps:

## Firebase
1. Create a Google Cloud account and a new **Firebase project**.
2. Register an Android app in your Firebase project using the package name: `com.jsdr.watchapp` and nickname of your choice.
3. Download the `google-services.json` configuration file and place it inside the `app/` directory.
4. Adjust the `build.gradle.kts` files according to the instructions provided in the Firebase Console.
5. **Enable Cloud Firestore** in the Firebase Console and configure the basic security rules.
6. **Enable Firebase Authentication** in the Firebase Console and choose Email/Password from Native Providers.

## TMDB API
1. Create an account on [TMDB (The Movie Database)](https://www.themoviedb.org/).
2. Request an API key via your profile settings.
3. Open `app/src/main/java/com/jsdr/watchapp/data/api/TmdbApi.kt` and replace the `[API_KEY]` placeholder in the `API_KEY` property with your generated key.
