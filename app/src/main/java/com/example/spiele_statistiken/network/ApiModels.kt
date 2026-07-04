package com.example.spiele_statistiken.network

import com.google.gson.annotations.SerializedName

// Request
data class ApiRequest(
    val cmd: String,
    @SerializedName("gruppen_id") val gruppenId: Long? = null,
    val id: Long? = null,
    val name: String? = null,
    val kennwort: String? = null,
    val vorname: String? = null,
    val nachname: String? = null,
    val gewinnmodus: String? = null,
    @SerializedName("runden_relevant") val rundenRelevant: Int? = null,
    @SerializedName("spiel_typ_id") val spielTypId: Long? = null,
    val datum: String? = null,
    val startzeit: String? = null,
    val endzeit: String? = null,
    @SerializedName("anzahl_spiele") val anzahlSpiele: Int? = null,
    val teilnehmer: List<TeilnehmerRequest>? = null,
    val code: String? = null,
    val email: String? = null
)

data class TeilnehmerRequest(
    @SerializedName("spieler_id") val spielerId: Long,
    val punkte: Int
)

// Responses
data class GruppeResponse(
    val id: Long,
    val name: String,
    val freigeschaltet: Boolean = false,
    val nachricht: String? = null,
    val hinweis: String? = null,
    val email: String? = null
)

data class GruppenErgebnis(
    val erfolg: Boolean,
    val nachricht: String,
    val gruppenId: Long? = null,
    val freigeschaltet: Boolean = false
)

data class SpielerResponse(
    val id: Long,
    @SerializedName("gruppen_id") val gruppenId: Long,
    val vorname: String,
    val nachname: String = ""
)

data class SpielTypResponse(
    val id: Long,
    @SerializedName("gruppen_id") val gruppenId: Long,
    val name: String,
    val gewinnmodus: String,
    @SerializedName("runden_relevant") val rundenRelevant: Int
)

data class EventResponse(
    val id: Long,
    @SerializedName("gruppen_id") val gruppenId: Long,
    @SerializedName("spiel_typ_id") val spielTypId: Long?,
    val datum: String,
    val startzeit: String,
    val endzeit: String,
    @SerializedName("anzahl_spiele") val anzahlSpiele: Int,
    val teilnehmer: List<TeilnehmerResponse>
)

data class TeilnehmerResponse(
    @SerializedName("spieler_id") val spielerId: Long,
    val vorname: String,
    val nachname: String,
    val punkte: Int
)

// Response = die Antwort vom Server
data class NachrichtResponse(
    val nachricht: String? = null,
    val fehler: String? = null,
    val id: Long? = null,
    val freigeschaltet: Boolean = false,
    val hinweis: String? = null
)

// Aufbereitete Response für das UI
data class AktionsErgebnis(
    val erfolg: Boolean,
    val nachricht: String
)
