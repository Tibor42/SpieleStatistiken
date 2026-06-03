package com.example.spiele_statistiken.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.spiele_statistiken.data.AppDatabase
import com.example.spiele_statistiken.data.Repository
import com.example.spiele_statistiken.data.Spieler
import com.example.spiele_statistiken.data.SpielEvent
import com.example.spiele_statistiken.data.SpielEventTeilnehmer
import com.example.spiele_statistiken.data.SpielTyp
import com.example.spiele_statistiken.data.TeilnehmerMitTyp
import com.example.spiele_statistiken.network.GruppenErgebnis
import com.example.spiele_statistiken.network.RemoteRepository
import com.example.spiele_statistiken.network.TeilnehmerRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch



class SpielerStatistikViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: Repository = Repository(AppDatabase.getDatabase(application))

    val alleSpieler: Flow<List<Spieler>> = repository.getAlleSpieler()
    val alleEvents: Flow<List<SpielEvent>> = repository.getAlleEvents()
    val alleTeilnehmer: Flow<List<SpielEventTeilnehmer>> = repository.getAlleTeilnehmer()

    val alleTeilnehmerMitTyp: Flow<List<TeilnehmerMitTyp>> = repository.getAlleTeilnehmerMitTyp()

    val alleSpielTypen: Flow<List<SpielTyp>> = repository.getAlleSpielTypen()

    private val _ausgewaehlterSpielTyp = MutableStateFlow<SpielTyp?>(null)
    private val _spielTypFehler = MutableStateFlow<String>("")
    val spielTypFehler: StateFlow<String> = _spielTypFehler

    val ausgewaehlterSpielTyp: StateFlow<SpielTyp?> = _ausgewaehlterSpielTyp
    private val _ausgewaehlteSpieler = MutableStateFlow<Set<Long>>(emptySet())
    val ausgewaehlteSpieler: StateFlow<Set<Long>> = _ausgewaehlteSpieler

    private val remoteRepository = RemoteRepository()

    private val _syncModus = MutableStateFlow("lokal")
    val syncModus: StateFlow<String> = _syncModus

    fun spielerToggle(spielerId: Long) {
        val aktuell = _ausgewaehlteSpieler.value.toMutableSet()
        if (aktuell.contains(spielerId)) aktuell.remove(spielerId)
        else if (aktuell.size < 5) aktuell.add(spielerId)
        _ausgewaehlteSpieler.value = aktuell
    }

    init {
        viewModelScope.launch {
            val letzterTypId = repository.getLetztenSpielTypId()
            if (letzterTypId != null) {
                _ausgewaehlterSpielTyp.value = repository.getSpielTypById(letzterTypId)
            }
        }
    }

    fun setSyncModus(modus: String) {
        _syncModus.value = modus
    }


    fun spielerHinzufuegen(vorname: String, nachname: String = "") {
        viewModelScope.launch {
            repository.spielerHinzufuegen(vorname, nachname)
        }
    }

    fun spielerAktualisieren(spieler: Spieler) {
        viewModelScope.launch {
            repository.spielerAktualisieren(spieler)
        }
    }

    fun spielerLoeschen(spieler: Spieler) {
        viewModelScope.launch {
            repository.spielerLoeschen(spieler)
        }
    }

    fun eventHinzufuegen(
        datum: String,
        anzahlSpiele: Int,
        startzeit: String = "",
        endzeit: String = "",
        teilnehmer: Map<Long, Int>,
        spielTypId: Long
    ) {
        viewModelScope.launch {
            repository.eventHinzufuegen(datum, anzahlSpiele, startzeit, endzeit, teilnehmer, spielTypId)
            _ausgewaehlteSpieler.value = emptySet()
        }
    }

    fun eventLoeschen(event: SpielEvent) {
        viewModelScope.launch {
            repository.eventLoeschen(event)
        }
    }

    fun getTeilnehmerFuerEvent(eventId: Long): Flow<List<SpielEventTeilnehmer>> =
        repository.getTeilnehmerFuerEvent(eventId)

    fun spielTypAuswaehlen(spielTyp: SpielTyp) {
        _ausgewaehlterSpielTyp.value = spielTyp
    }

    fun spielTypHinzufuegen(name: String, gewinnmodus: String, rundenRelevant: Boolean) {
        viewModelScope.launch {
            val id = repository.spielTypHinzufuegen(name, gewinnmodus, rundenRelevant)
            val neuerTyp = repository.getSpielTypById(id)
            _ausgewaehlterSpielTyp.value = neuerTyp
        }
    }

    fun spielTypLoeschen(spielTyp: SpielTyp) {
        viewModelScope.launch {
            val erfolg = repository.spielTypLoeschen(spielTyp)
            if (erfolg) {
                if (_ausgewaehlterSpielTyp.value?.id == spielTyp.id) {
                    _ausgewaehlterSpielTyp.value = null
                }
                _spielTypFehler.value = ""
            } else {
                _spielTypFehler.value = "\"${spielTyp.name}\" kann nicht geloescht werden - es gibt noch Events mit diesem Typ!"
            }

        }
    }

    fun gruppeErstellen(name: String, kennwort: String, callback: (GruppenErgebnis) -> Unit) {
        viewModelScope.launch {
            try {
                val response = remoteRepository.gruppeErstellen(name, kennwort)
                _syncModus.value = "online"
                callback(GruppenErgebnis(
                    true,
                    response.nachricht ?: "Gruppe '${response.name}' erfolgreich erstellt!",
                    response.id,
                    freigeschaltet = response.freigeschaltet
                ))
            } catch (e: Exception) {
                callback(GruppenErgebnis(false, "Fehler: ${e.message}"))
            }
        }
    }
    fun gruppeBeitreten(name: String, kennwort: String, callback: (GruppenErgebnis) -> Unit) {
        viewModelScope.launch {
            try {
                val response = remoteRepository.gruppeBeitreten(name, kennwort)
                _syncModus.value = "online"
                callback(GruppenErgebnis(
                    true,
                    response.nachricht ?:"Erfolgreich der Gruppe '${response.name}' beigetreten!",
                    response.id,
                    freigeschaltet = response.freigeschaltet
                ))
            } catch (e: Exception) {
                callback(GruppenErgebnis(false, "Fehler: ${e.message}"))
            }
        }
    }


}

