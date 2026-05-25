package com.example.spiele_statistiken.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.spiele_statistiken.data.AppDatabase
import com.example.spiele_statistiken.data.Repository
import com.example.spiele_statistiken.data.Spieler
import com.example.spiele_statistiken.data.SpielEvent
import com.example.spiele_statistiken.data.SpielEventTeilnehmer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SpielerStatistikViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: Repository = Repository(AppDatabase.getDatabase(application))

    val alleSpieler: Flow<List<Spieler>> = repository.getAlleSpieler()
    val alleEvents: Flow<List<SpielEvent>> = repository.getAlleEvents()
    val alleTeilnehmer: Flow<List<SpielEventTeilnehmer>> = repository.getAlleTeilnehmer()

    private val _ausgewaehlteSpieler = MutableStateFlow<Set<Long>>(emptySet())
    val ausgewaehlteSpieler: StateFlow<Set<Long>> = _ausgewaehlteSpieler

    fun spielerToggle(spielerId: Long) {
        val aktuell = _ausgewaehlteSpieler.value.toMutableSet()
        if (aktuell.contains(spielerId)) aktuell.remove(spielerId)
        else if (aktuell.size < 5) aktuell.add(spielerId)
        _ausgewaehlteSpieler.value = aktuell
    }

    fun spielerHinzufuegen(vorname: String, nachname: String = "") {
        viewModelScope.launch {
            repository.spielerHinzufuegen(vorname, nachname)
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
        teilnehmer: Map<Long, Int>
    ) {
        viewModelScope.launch {
            repository.eventHinzufuegen(datum, anzahlSpiele, startzeit, endzeit, teilnehmer)
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
}