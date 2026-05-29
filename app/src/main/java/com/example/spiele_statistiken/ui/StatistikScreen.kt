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
import com.example.spiele_statistiken.data.SpielTyp
import com.example.spiele_statistiken.data.TeilnehmerMitTyp
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

    val alleTeilnehmerMitTyp by viewModel.alleTeilnehmerMitTyp.collectAsStateWithLifecycle(initialValue = emptyList<TeilnehmerMitTyp>())
    val alleSpielTypen by viewModel.alleSpielTypen.collectAsStateWithLifecycle(initialValue = emptyList<SpielTyp>())

    val statistiken = remember(alleSpieler, alleEvents, alleTeilnehmerMitTyp, alleSpielTypen) {
        berechneStatistiken(alleSpieler, alleTeilnehmerMitTyp, alleSpielTypen)
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

        statistiken.forEach { (spielTyp, spielerListe) ->
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    spielTyp.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    if (spielTyp.gewinnmodus == "wenigste") "Wenigste Punkte gewinnt"
                    else "Meiste Punkte gewinnt",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                StatistikHeader()
            }
            items(spielerListe) { statistik ->
                StatistikZeile(statistik = statistik, alleEvents = alleEvents)
            }
        }
    }
}

fun berechneStatistiken(
    alleSpieler: List<Spieler>,
    alleTeilnehmer: List<TeilnehmerMitTyp>,
    alleSpielTypen: List<SpielTyp>
): Map<SpielTyp, List<SpielerStatistik>> {
    return alleSpielTypen.associateWith { spielTyp ->
        val teilnehmerDesTyps = alleTeilnehmer.filter { it.spielTypId == spielTyp.id }

        alleSpieler.map { spieler ->
            val meineTeilnahmen = teilnehmerDesTyps.filter { it.spielerId == spieler.id }
            val anzahlEvents = meineTeilnahmen.size
            val gesamtPunkte = meineTeilnahmen.sumOf { it.punkte }
            val durchschnitt = if (anzahlEvents > 0)
                gesamtPunkte.toDouble() / anzahlEvents else 0.0

            val siege = meineTeilnahmen.count { teilnahme ->
                val eventTeilnehmer = teilnehmerDesTyps.filter { it.eventId == teilnahme.eventId }
                if (spielTyp.gewinnmodus == "wenigste") {
                    eventTeilnehmer.minByOrNull { it.punkte }?.spielerId == spieler.id
                } else {
                    eventTeilnehmer.maxByOrNull { it.punkte }?.spielerId == spieler.id
                }
            }

            SpielerStatistik(spieler, anzahlEvents, siege, gesamtPunkte, durchschnitt, meineTeilnahmen.map {
                SpielEventTeilnehmer(it.eventId, it.spielerId, it.punkte)
            })
        }.filter { it.anzahlEvents > 0 }
            .sortedBy { if (spielTyp.gewinnmodus == "wenigste") it.gesamtPunkte else -it.gesamtPunkte }
    }.filter { it.value.isNotEmpty() }
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