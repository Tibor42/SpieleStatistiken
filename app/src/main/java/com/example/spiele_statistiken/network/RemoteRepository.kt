package com.example.spiele_statistiken.network

class RemoteRepository {

    private val api = RetrofitClient.apiService

    // Gruppen
    suspend fun gruppeErstellen(name: String, kennwort: String): GruppeResponse {
        return api.gruppeRequest(
            ApiRequest(cmd = "gruppe_erstellen", name = name, kennwort = kennwort)
        )
    }

    suspend fun gruppeBeitreten(name: String, kennwort: String): GruppeResponse {
        return api.gruppeRequest(
            ApiRequest(cmd = "gruppe_beitreten", name = name, kennwort = kennwort)
        )
    }

    // Spieler
    suspend fun spielerAbrufen(gruppenId: Long): List<SpielerResponse> {
        return api.spielerListeRequest(
            ApiRequest(cmd = "spieler_abrufen", gruppenId = gruppenId)
        )
    }

    suspend fun spielerErstellen(gruppenId: Long, vorname: String, nachname: String): NachrichtResponse {
        return api.request(
            ApiRequest(cmd = "spieler_erstellen", gruppenId = gruppenId, vorname = vorname, nachname = nachname)
        )
    }

    suspend fun spielerAktualisieren(id: Long, vorname: String, nachname: String): NachrichtResponse {
        return api.request(
            ApiRequest(cmd = "spieler_aktualisieren", id = id, vorname = vorname, nachname = nachname)
        )
    }

    suspend fun spielerLoeschen(id: Long, gruppenId: Long): NachrichtResponse {
        return api.request(
            ApiRequest(cmd = "spieler_loeschen", id = id, gruppenId = gruppenId)
        )
    }

    // Spiel-Typen
    suspend fun spielTypenAbrufen(gruppenId: Long): List<SpielTypResponse> {
        return api.spielTypListeRequest(
            ApiRequest(cmd = "spieltypen_abrufen", gruppenId = gruppenId)
        )
    }

    suspend fun spielTypErstellen(gruppenId: Long, name: String, gewinnmodus: String, rundenRelevant: Int): NachrichtResponse {
        return api.request(
            ApiRequest(cmd = "spieltyp_erstellen", gruppenId = gruppenId, name = name, gewinnmodus = gewinnmodus, rundenRelevant = rundenRelevant)
        )
    }

    suspend fun spielTypAktualisieren(id: Long, gruppenId: Long, name: String, gewinnmodus: String, rundenRelevant: Int): NachrichtResponse {
        return api.request(
            ApiRequest(cmd = "spieltyp_aktualisieren", id = id, gruppenId = gruppenId, name = name, gewinnmodus = gewinnmodus, rundenRelevant = rundenRelevant)
        )
    }

    suspend fun spielTypLoeschen(id: Long, gruppenId: Long): NachrichtResponse {
        return api.request(
            ApiRequest(cmd = "spieltyp_loeschen", id = id, gruppenId = gruppenId)
        )
    }

    // Events
    suspend fun eventsAbrufen(gruppenId: Long): List<EventResponse> {
        return api.eventsRequest(
            ApiRequest(cmd = "events_abrufen", gruppenId = gruppenId)
        )
    }

    suspend fun eventErstellen(
        gruppenId: Long,
        spielTypId: Long?,
        datum: String,
        startzeit: String,
        endzeit: String,
        anzahlSpiele: Int,
        teilnehmer: List<TeilnehmerRequest>
    ): NachrichtResponse {
        return api.request(
            ApiRequest(
                cmd = "events_erstellen",
                gruppenId = gruppenId,
                spielTypId = spielTypId,
                datum = datum,
                startzeit = startzeit,
                endzeit = endzeit,
                anzahlSpiele = anzahlSpiele,
                teilnehmer = teilnehmer
            )
        )
    }

    suspend fun eventLoeschen(id: Long, gruppenId: Long): NachrichtResponse {
        return api.request(
            ApiRequest(cmd = "events_loeschen", id = id, gruppenId = gruppenId)
        )
    }
}