package data.network

import java.util.prefs.Preferences

class PreferencesManager {
    private val prefs = Preferences.userRoot().node("auth_app")

    fun saveAuthData(username: String, token: String) {
        prefs.put("username", username)
        prefs.put("token", token)
    }

    fun getUsername(): String = prefs.get("username", "")
    fun getToken(): String = prefs.get("token", "")

    fun clearAuthData() {
        prefs.remove("username")
        prefs.remove("token")
    }
}