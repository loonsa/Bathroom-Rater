package com.example.bathroomrater

import android.content.Context
import android.content.SharedPreferences

class LocalPersistentDataHandler {
    private var pref: SharedPreferences
    private var editor: SharedPreferences.Editor

    constructor(context: Context) {
        pref = context.getSharedPreferences(
            context.packageName + "_preferences",
            Context.MODE_PRIVATE )
        editor = pref.edit()
    }

    fun getFavorites(): MutableSet<String> {
        val favorites: MutableSet<String> =
            pref.getStringSet("favorites", mutableSetOf<String>()) ?: mutableSetOf()
        return favorites
    }

    fun addFavorite(bathroomId: String) {
        val favorites: MutableSet<String> = getFavorites()
        favorites.add(bathroomId)
        editor.putStringSet("favorites", favorites)
        editor.commit()
    }

    fun removeFavorite(bathroomId: String) {
        val favorites: MutableSet<String> = getFavorites()
        favorites.remove(bathroomId)
        editor.putStringSet("favorites", favorites)
        editor.commit()
    }

    fun filterStatus(filter: String): Boolean {
        //filter should be either
        //topOnly, favoritesOnly, or genderNeutralOnly
        val filterStatus: Boolean = pref.getBoolean(filter, false)
        return filterStatus
    }

    fun setShowTopOnly(userPreference: Boolean) {
        editor.putBoolean("topOnly", userPreference)
        editor.commit()
    }

    fun setShowFavoritesOnly(userPreference: Boolean) {
        editor.putBoolean("favoritesOnly", userPreference)
        editor.commit()
    }

    fun setShowGenderNeutralOnly(userPreference: Boolean) {
        editor.putBoolean("genderNeutralOnly", userPreference)
        editor.commit()
    }
}