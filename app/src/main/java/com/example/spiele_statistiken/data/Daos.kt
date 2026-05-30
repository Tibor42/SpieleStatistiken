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

    @Update
    suspend fun update(spieler: Spieler)
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

    @Query("DELETE FROM spiel_event_teilnehmer WHERE event_id = :eventId")
    suspend fun deleteTeilnehmerFuerEvent(eventId: Long)

    @Query("SELECT * FROM spiel_event_teilnehmer WHERE event_id = :eventId")
    fun getTeilnehmerFuerEvent(eventId: Long): Flow<List<SpielEventTeilnehmer>>

    @Query("SELECT * FROM spiel_event_teilnehmer")
    fun getAlleTeilnehmer(): Flow<List<SpielEventTeilnehmer>>

    @Query("SELECT spiel_typ_id FROM spiel_event ORDER BY id DESC LIMIT 1")
    suspend fun getLetztenSpielTypId():Long?

    @Query( """   
        SELECT t.*, se.spiel_typ_id
        FROM spiel_event_teilnehmer t
        INNER JOIN spiel_event se on t.event_id = se.id
    """)
    fun getAlleTeilnehmerMitTyp(): Flow<List<TeilnehmerMitTyp>>

}

@Dao
interface SpielTypDao {
    @Insert
    suspend fun insert(spielTyp: SpielTyp): Long

    @Update
    suspend fun update(spielTyp: SpielTyp)

    @Delete
    suspend fun delete(spielTyp: SpielTyp)

    @Query("SELECT * FROM spiel_typ ORDER BY name ASC")
    fun getAlleSpielTypen(): Flow<List<SpielTyp>>

    @Query("SELECT * FROM spiel_typ WHERE id = :id")
    suspend fun getById(id: Long): SpielTyp?
}