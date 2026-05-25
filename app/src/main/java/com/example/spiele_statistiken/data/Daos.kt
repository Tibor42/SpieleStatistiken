package com.example.spiele_statistiken.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SpielerDao {
    @Query("SELECT * FROM spieler ORDER BY vorname ASC")
    fun getAlleSpieler(): Flow<List<Spieler>>

    @Insert
    suspend fun insert(spieler: Spieler)

    @Delete
    suspend fun delete(spieler: Spieler)
}

@Dao
interface SpielEventDao {
    @Query("SELECT * FROM spiel_event ORDER BY datum DESC")
    fun getAlleEvents(): Flow<List<SpielEvent>>

    @Insert
    suspend fun insert(event: SpielEvent): Long

    @Delete
    suspend fun delete(event: SpielEvent)

    @Insert
    suspend fun insertTeilnehmer(teilnehmer: SpielEventTeilnehmer)

    @Query("DELETE FROM spiel_event_teilnehmer WHERE eventId = :eventId")
    suspend fun deleteTeilnehmerFuerEvent(eventId: Long)

    @Query("SELECT * FROM spiel_event_teilnehmer WHERE eventId = :eventId")
    fun getTeilnehmerFuerEvent(eventId: Long): Flow<List<SpielEventTeilnehmer>>

    @Query("SELECT * FROM spiel_event_teilnehmer")
    fun getAlleTeilnehmer(): Flow<List<SpielEventTeilnehmer>>

}