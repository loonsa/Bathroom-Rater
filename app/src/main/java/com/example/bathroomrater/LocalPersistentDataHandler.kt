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

    fun isShowGenderNeutralOnly(): Boolean {
        val genderNeutralOnly: Boolean = pref.getBoolean("genderNeutralOnly", false)
        return genderNeutralOnly
    }

    fun setShowGenderNeutralOnly(userPreference: Boolean) {
        editor.putBoolean("genderNeutralOnly", userPreference)
        editor.commit()
    }

    fun getSortOrderPref(): String {
        val sortOrder: String = pref.getString("sortOrder", "highest_rated") ?: "highest_rated"
        return sortOrder
    }

    fun setSortOrderPref(userPreference: String) {
        editor.putString("sortOrder", userPreference)
        editor.commit()
    }

}