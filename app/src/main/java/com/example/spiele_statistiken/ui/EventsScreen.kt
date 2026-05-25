package com.example.spiele_statistiken.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.spiele_statistiken.data.Spieler
import com.example.spiele_statistiken.data.SpielEvent
import com.example.spiele_statistiken.data.SpielEventTeilnehmer
import com.example.spiele_statistiken.viewmodel.SpielerStatistikViewModel

@Composable
fun EventsScreen(
    viewModel: SpielerStatistikViewModel,
    innerPadding: PaddingValues
) {
    val alleEvents by viewModel.alleEvents.collectAsStateWithLifecycle(emptyList())
    val alleSpieler by viewModel.alleSpieler.collectAsStateWithLifecycle(emptyList())

    if (alleEvents.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text("Noch keine Events gespeichert.")
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(alleEvents) { event ->
            EventCard(
                event = event,
                alleSpieler = alleSpieler,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun EventCard(
    event: SpielEvent,
    alleSpieler: List<Spieler>,
    viewModel: SpielerStatistikViewModel
) {
    val teilnehmer by viewModel.getTeilnehmerFuerEvent(event.id)
        .collectAsStateWithLifecycle(emptyList())

    val winner = teilnehmer.minByOrNull { it.punkte }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = event.datum,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (event.startzeit.isNotEmpty()) {
                        Text(
                            text = if (event.endzeit.isNotEmpty())
                                "${event.startzeit} – ${event.endzeit} Uhr"
                            else "${event.startzeit} Uhr",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    val runden = if (teilnehmer.isNotEmpty())
                        event.anzahlSpiele / teilnehmer.size else 0
                    Text(
                        text = "${event.anzahlSpiele} Spiele / $runden Runden",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                IconButton(onClick = { viewModel.eventLoeschen(event) }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Löschen")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            teilnehmer.sortedBy { it.punkte }.forEach { t ->
                val spieler = alleSpieler.find { it.id == t.spielerId }
                val name = spieler?.let { "${it.vorname} ${it.nachname}".trim() } ?: "Unbekannt"
                val istGewinner = t.spielerId == winner?.spielerId
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (istGewinner) "🏆 $name" else name,
                        style = if (istGewinner)
                            MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.primary
                            )
                        else MaterialTheme.typography.bodyMedium
                    )
                    Text("${t.punkte} Pkt.")
                }
            }
        }
    }
}

