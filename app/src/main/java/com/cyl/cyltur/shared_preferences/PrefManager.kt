package com.cyl.cyltur.shared_preferences

import android.annotation.SuppressLint
import android.content.Context
import androidx.preference.PreferenceManager

@SuppressLint("ApplySharedPref")
class PrefManager(context: Context, preferencesName: String? = null) {

    private var sharedPreferences = if (preferencesName.isNullOrEmpty()) {
        PreferenceManager.getDefaultSharedPreferences(context)
    } else {
        context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
    }

    var lastUpdate: Long
        get() = sharedPreferences.getLong(LAST_UPDATE, 0L)
        set(lastUpdate) {
            sharedPreferences.edit().putLong(LAST_UPDATE, lastUpdate).commit()
        }

    var mapType: Int
        get() = sharedPreferences.getInt(MAP_TYPE, 1)
        set(mapType) {
            sharedPreferences.edit().putInt(MAP_TYPE, mapType).commit()
        }


    companion object {
        private const val LAST_UPDATE = "LAST_UPDATE"
        private const val MAP_TYPE = "MAP_TYPE"
    }
}
