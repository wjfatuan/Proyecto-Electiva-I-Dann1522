package com.example.flickzy.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object TokenStore {
    private lateinit var prefs: SharedPreferences
    private const val PREF_NAME = "auth_prefs"
    private const val KEY_PERSISTENT_TOKEN = "persistent_token"

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun savePersistentToken(token: String) {
        prefs.edit {
            putString(KEY_PERSISTENT_TOKEN, token)
        }
    }

    fun getPersistentToken(): String? {
        return prefs.getString(KEY_PERSISTENT_TOKEN, null)
    }

    fun clear() {
        prefs.edit { clear() }
    }
}