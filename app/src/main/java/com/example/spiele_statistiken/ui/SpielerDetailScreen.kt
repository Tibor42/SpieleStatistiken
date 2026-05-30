package com.example.spiele_statistiken.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpielerDetailScreen(
    spielerId: Long,
    viewModel: SpielerStatistikViewModel,
    innerPadding: PaddingValues,
    onBack: () -> Unit
) {
    val alleSpieler by viewModel.alleSpieler.collectAsStateWithLifecycle(emptyList())
    val alleEvents by viewModel.alleEvents.collectAsStateWithLifecycle(emptyList())
    val alleTeilnehmer by viewModel.alleTeilnehmer.collectAsStateWithLifecycle(emptyList())

    val spieler = alleSpieler.find { it.id == spielerId }
    val meineTeilnahmen = alleTeilnehmer.filter { it.spielerId == spielerId }
    val kannGeloeschtWerden = meineTeilnahmen.isEmpty()

    var bearbeitenModus by remember { mutableStateOf(false) }
    var neuerVorname by remember { mutableStateOf(spieler?.vorname ?: "") }
    var neuerNachname by remember { mutableStateOf(spieler?.nachname ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(spieler?.let { "${it.vorname} ${it.nachname}".trim() } ?: "Spieler") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                }
            )
        }
    ) { scaffoldPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                if (bearbeitenModus) {
                    Text("Name bearbeiten", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = neuerVorname,
                        onValueChange = { neuerVorname = it },
                        label = { Text("Vorname") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = neuerNachname,
                        onValueChange = { neuerNachname = it },
                        label = { Text("Nachname") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                spieler?.let {
                                    viewModel.spielerAktualisieren(
                                        it.copy(
                                            vorname = neuerVorname.trim(),
                                            nachname = neuerNachname.trim()
                                        )
                                    )
                                }
                                bearbeitenModus = false
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Speichern")
                        }
                        OutlinedButton(
                            onClick = { bearbeitenModus = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Abbrechen")
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { bearbeitenModus = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Name bearbeiten")
                        }
                        if (kannGeloeschtWerden) {
                            Button(
                                onClick = {
                                    spieler?.let { viewModel.spielerLoeschen(it) }
                                    onBack()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Löschen")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Text("Spiel-Verlauf", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))

                if (meineTeilnahmen.isEmpty()) {
                    Text("Noch keine Spiele.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            items(meineTeilnahmen) { teilnahme ->
                val event = alleEvents.find { it.id == teilnahme.eventId }
                event?.let {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(it.datum)
                        Text("${teilnahme.punkte} Pkt.")
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))
                }
            }
        }
    }
}

