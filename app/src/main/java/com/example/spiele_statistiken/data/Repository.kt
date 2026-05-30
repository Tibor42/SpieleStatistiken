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
        teilnehmer: Map<Long, Int>,  // spielerId → Punkte
        spielTypId: Long
    ) {
        val eventId = eventDao.insert(
            SpielEvent(
                datum = datum,
                anzahlSpiele = anzahlSpiele,
                startzeit = startzeit,
                endzeit = endzeit,
                spielTypId = spielTypId
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

    fun getAlleTeilnehmerMitTyp(): Flow<List<TeilnehmerMitTyp>> = eventDao.getAlleTeilnehmerMitTyp()

    suspend fun spielerAktualisieren(spieler: Spieler) {
        spielerDao.update(spieler)
    }

    suspend fun spielTypHinzufuegen(name: String, gewinnmodus: String, rundenRelevant: Boolean): Long {
        return spielTypDao.insert(SpielTyp(name = name, gewinnmodus = gewinnmodus, rundenRelevant = rundenRelevant))
    }

    suspend fun spielTypLoeschen(spielTyp: SpielTyp): Boolean {
        return try {
            spielTypDao.delete(spielTyp)
            true
        } catch (e: android.database.sqlite.SQLiteConstraintException) {
            false
        }
    }

    suspend fun spielTypAktualisieren(spielTyp: SpielTyp) {
        spielTypDao.update(spielTyp)
    }

    suspend fun getSpielTypById(id: Long): SpielTyp? {
        return spielTypDao.getById(id)
    }
    suspend fun getLetztenSpielTypId(): Long? {
        return eventDao.getLetztenSpielTypId()
    }
}

