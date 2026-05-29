package com.example.spiele_statistiken.data


import androidx.room.ColumnInfo
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

@Entity(
    tableName = "spiel_event",
    foreignKeys = [
        ForeignKey(
            entity = SpielTyp::class,
            parentColumns = ["id"],
            childColumns = ["spiel_typ_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class SpielEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val datum: String,
    val startzeit: String = "",
    val endzeit: String = "",
    val anzahlSpiele: Int,
    @ColumnInfo(name = "spiel_typ_id")
    val spielTypId: Long? = null

)

@Entity(
    tableName = "spiel_event_teilnehmer",
    primaryKeys = ["event_id", "spieler_id"],
    foreignKeys = [
        ForeignKey(
            entity = SpielEvent::class,
            parentColumns = ["id"],
            childColumns = ["event_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Spieler::class,
            parentColumns = ["id"],
            childColumns = ["spieler_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SpielEventTeilnehmer(
    @ColumnInfo(name = "event_id")
    val eventId: Long,
    @ColumnInfo(name = "spieler_id")
    val spielerId: Long,
    val punkte: Int
)

@Entity(tableName = "spiel_typ")
data class SpielTyp(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val gewinnmodus: String = "wenigste", // "wenigste" oder "meiste"
    val rundenRelevant: Boolean = true
)


data class TeilnehmerMitTyp(
    @ColumnInfo(name = "event_id")
    val eventId: Long,
    @ColumnInfo(name = "spieler_id")
    val spielerId: Long,

    val punkte: Int,
    @ColumnInfo(name = "spiel_typ_id")
    val spielTypId: Long?
)