package com.example.spiele_statistiken.data


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "spieler")
data class Spieler(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vorname: String,
    val nachname: String = ""
)

@Entity(tableName = "spiel_event")
data class SpielEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val datum: String,
    val startzeit: String = "",
    val endzeit: String = "",
    val anzahlSpiele: Int
)

@Entity(
    tableName = "spiel_event_teilnehmer",
    primaryKeys = ["eventId", "spielerId"],
    foreignKeys = [
        ForeignKey(
            entity = SpielEvent::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Spieler::class,
            parentColumns = ["id"],
            childColumns = ["spielerId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SpielEventTeilnehmer(
    val eventId: Long,
    val spielerId: Long,
    val punkte: Int
)