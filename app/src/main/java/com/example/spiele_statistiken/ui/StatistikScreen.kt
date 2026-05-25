package com.example.spiele_statistiken.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.spiele_statistiken.data.SpielEvent
import com.example.spiele_statistiken.data.Spieler
import com.example.spiele_statistiken.data.SpielEventTeilnehmer
import com.example.spiele_statistiken.viewmodel.SpielerStatistikViewModel

data class SpielerStatistik(
    val spieler: Spieler,
    val anzahlEvents: Int,
    val anzahlSiege: Int,
    val gesamtPunkte: Int,
    val durchschnittPunkte: Double,
    val teilnahmen: List<SpielEventTeilnehmer>
)

@Composable
fun StatistikScreen(
    viewModel: SpielerStatistikViewModel,
    innerPadding: PaddingValues
) {
    val alleSpieler by viewModel.alleSpieler.collectAsStateWithLifecycle(emptyList())
    val alleEvents by viewModel.alleEvents.collectAsStateWithLifecycle(emptyList())
    val alleTeilnehmer by viewModel.alleTeilnehmer.collectAsStateWithLifecycle(emptyList())

    val statistiken = remember(alleSpieler, alleEvents, alleTeilnehmer) {
        berechneStatistiken(alleSpieler, alleTeilnehmer)
    }

    if (alleSpieler.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text("Noch keine Daten vorhanden.")
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text("Gesamtstatistik", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            StatistikHeader()
        }
        items(statistiken) { statistik ->
            StatistikZeile(statistik = statistik, alleEvents = alleEvents)
        }
    }
}

fun berechneStatistiken(
    alleSpieler: List<Spieler>,
    alleTeilnehmer: List<SpielEventTeilnehmer>
): List<SpielerStatistik> {
    return alleSpieler.map { spieler ->
        val meineTeilnahmen = alleTeilnehmer.filter { it.spielerId == spieler.id }
        val anzahlEvents = meineTeilnahmen.size
        val gesamtPunkte = meineTeilnahmen.sumOf { it.punkte }

        val durchschnitt = if (anzahlEvents > 0)
            gesamtPunkte.toDouble() / anzahlEvents else 0.0

        // Siege: Events wo dieser Spieler die wenigsten Punkte hatte
        val siege = meineTeilnahmen.count { teilnahme ->
            val eventTeilnehmer = alleTeilnehmer.filter { it.eventId == teilnahme.eventId }
            eventTeilnehmer.minByOrNull { it.punkte }?.spielerId == spieler.id
        }

        SpielerStatistik(spieler, anzahlEvents, siege, gesamtPunkte, durchschnitt, meineTeilnahmen)
    }.filter { it.anzahlEvents > 0 }
        .sortedBy { it.gesamtPunkte }
}

@Composable
fun StatistikHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Spieler", fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
        Text("Events", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        Text("Siege", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        Text("Ø Pkt.", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
    }
    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
}

@Composable
fun StatistikZeile(statistik: SpielerStatistik, alleEvents: List<SpielEvent>) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "${statistik.spieler.vorname} ${statistik.spieler.nachname}".trim(),
                modifier = Modifier.weight(2f),
                fontWeight = if (statistik.anzahlSiege > 0) FontWeight.Bold else FontWeight.Normal
            )
            Text("${statistik.anzahlEvents}", modifier = Modifier.weight(1f))
            Text("${statistik.anzahlSiege}", modifier = Modifier.weight(1f))
            Text("%.0f".format(statistik.durchschnittPunkte), modifier = Modifier.weight(1f))
        }

        if (expanded) {
            SpielerVerlauf(statistik = statistik, alleEvents = alleEvents)
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))
    }
}

@Composable
fun SpielerVerlauf(statistik: SpielerStatistik, alleEvents: List<SpielEvent>) {
    Column(
        modifier = Modifier.fillMaxWidth()) {
        statistik.teilnahmen.forEach{
            teilnahme ->
                alleEvents.find { it.id == teilnahme.eventId}?.let {
                    event -> Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("${event.datum} - ${teilnahme.punkte} Pkt.") }
                }

        }
    }

}