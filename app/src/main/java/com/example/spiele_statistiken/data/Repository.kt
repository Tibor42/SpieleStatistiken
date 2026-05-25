package com.example.spiele_statistiken.data

import kotlinx.coroutines.flow.Flow

class Repository(private val db: AppDatabase) {

    private val spielerDao = db.spielerDao()
    private val eventDao = db.spielEventDao()

    private val spielTypDao = db.spielTypDao()

    // Spieler
    fun getAlleSpieler(): Flow<List<Spieler>> = spielerDao.getAlleSpieler()

    suspend fun spielerHinzufuegen(vorname: String, nachname: String = "") {
        spielerDao.insert(Spieler(vorname = vorname, nachname = nachname))
    }

    suspend fun spielerLoeschen(spieler: Spieler) {
        spielerDao.delete(spieler)
    }

    // Events
    fun getAlleEvents(): Flow<List<SpielEvent>> = eventDao.getAlleEvents()

    fun getTeilnehmerFuerEvent(eventId: Long): Flow<List<SpielEventTeilnehmer>> =
        eventDao.getTeilnehmerFuerEvent(eventId)

    fun getAlleTeilnehmer() : Flow<List<SpielEventTeilnehmer>> = eventDao.getAlleTeilnehmer()

    suspend fun eventHinzufuegen(
        datum: String,
        anzahlSpiele: Int,
        startzeit: String = "",
        endzeit: String = "",
        teilnehmer: Map<Long, Int>  // spielerId → Punkte
    ) {
        val eventId = eventDao.insert(
            SpielEvent(
                datum = datum,
                anzahlSpiele = anzahlSpiele,
                startzeit = startzeit,
                endzeit = endzeit
            )
        )
        teilnehmer.forEach { (spielerId, punkte) ->
            eventDao.insertTeilnehmer(
                SpielEventTeilnehmer(
                    eventId = eventId,
                    spielerId = spielerId,
                    punkte = punkte
                )
            )
        }
    }

    suspend fun eventLoeschen(event: SpielEvent) {
        eventDao.delete(event)
    }
    // Spiel-Typen
    fun getAlleSpielTypen(): Flow<List<SpielTyp>> = spielTypDao.getAlleSpielTypen()

    suspend fun spielTypHinzufuegen(name: String, gewinnmodus: String): Long {
        return spielTypDao.insert(SpielTyp(name = name, gewinnmodus = gewinnmodus))
    }

    suspend fun spielTypLoeschen(spielTyp: SpielTyp) {
        spielTypDao.delete(spielTyp)
    }

    suspend fun spielTypAktualisieren(spielTyp: SpielTyp) {
        spielTypDao.update(spielTyp)
    }

    suspend fun getSpielTypById(id: Long): SpielTyp? {
        return spielTypDao.getById(id)
    }

}

