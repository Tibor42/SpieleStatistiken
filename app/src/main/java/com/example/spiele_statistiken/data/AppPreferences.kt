package com.example.spiele_statistiken.data

import android.content.Context

class AppPreferences(context: Context) {

    private val prefs = context.getSharedPreferences("spiele_statistiken", Context.MODE_PRIVATE)

    var gruppenId: Long
        get() = prefs.getLong("gruppen_id", -1L)
        set(value) = prefs.edit().putLong("gruppen_id", value).apply()

    var gruppenName: String
        get() = prefs.getString("gruppen_name", "") ?: ""
        set(value) = prefs.edit().putString("gruppen_name", value).apply()

    var syncModus: String
        get() = prefs.getString("sync_modus", "lokal") ?: "lokal"
        set(value) = prefs.edit().putString("sync_modus", value).apply()

    val istOnline: Boolean
        get() = syncModus == "online"

    var istFreigeschaltet: Boolean
        get() = prefs.getBoolean("ist_freigeschaltet", false)
        set(value) = prefs.edit().putBoolean("ist_freigeschaltet", value).apply()

    fun abmelden() {
        prefs.edit().clear().apply()
    }
}

