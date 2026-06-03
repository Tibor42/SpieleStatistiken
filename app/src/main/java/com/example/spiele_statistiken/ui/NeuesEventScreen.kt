package com.example.spiele_statistiken.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.spiele_statistiken.data.SpielTyp
import com.example.spiele_statistiken.viewmodel.SpielerStatistikViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun NeuesEventScreen(
    viewModel: SpielerStatistikViewModel,
    innerPadding: PaddingValues,
    onSpielerClick: (Long) -> Unit
) {
    val alleSpieler by viewModel.spielerListe.collectAsStateWithLifecycle()
    val ausgewaehlt by viewModel.ausgewaehlteSpieler.collectAsStateWithLifecycle()
    val alleSpielTypen by viewModel.spielTypListe.collectAsStateWithLifecycle()
    val ausgewaehlterSpielTyp by viewModel.ausgewaehlterSpielTyp.collectAsStateWithLifecycle()

    var datum by remember { mutableStateOf(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))) }
    var startzeit by remember { mutableStateOf("") }
    var endzeit by remember { mutableStateOf("") }
    var anzahlSpiele by remember { mutableStateOf("") }
    var punkte by remember { mutableStateOf<Map<Long, String>>(emptyMap()) }
    var fehler by remember { mutableStateOf("") }
    var erfolg by remember { mutableStateOf(false) }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .windowInsetsPadding(WindowInsets.ime)
        ,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Neues Spiel-Event", style = MaterialTheme.typography.headlineSmall)

        if (alleSpielTypen.isEmpty()) {
            Text( "Bitte zuerst einen Spiel-Typ anlegen.", color = MaterialTheme.colorScheme.error)
        } else {
            Text("Spiel-Typ:", style = MaterialTheme.typography.titleMedium)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                alleSpielTypen.forEach { spielTyp ->  FilterChip(
                    selected = ausgewaehlterSpielTyp?.id == spielTyp.id,
                    onClick = { viewModel.spielTypAuswaehlen(spielTyp) },
                    label = { Text(spielTyp.name)}
                ) }
            }
        }

        OutlinedTextField(
            value = datum,
            onValueChange = { datum = it },
            label = { Text("Datum (TT.MM.JJJJ)") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { startzeit = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) },
                modifier = Modifier.weight(1f)
            ) {
                Text(if (startzeit.isEmpty()) "Spiel-Beginn jetzt" else "Beginn: $startzeit Uhr")
            }

            Button(
                onClick = { endzeit = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) },
                modifier = Modifier.weight(1f),
                enabled = startzeit.isNotEmpty()
            ) {
                Text(if (endzeit.isEmpty()) "Spiel-Ende jetzt" else "Ende: $endzeit Uhr")
            }

        }

        OutlinedTextField(
            value = anzahlSpiele,
            onValueChange = { anzahlSpiele = it },
            label = { Text("Anzahl Spiele") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Text("Teilnehmer auswählen (3–5):", style = MaterialTheme.typography.titleMedium)

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            alleSpieler.forEach { spieler ->
                val istAusgewaehlt = ausgewaehlt.contains(spieler.id)
                FilterChip(
                    selected = istAusgewaehlt,
                    onClick = { viewModel.spielerToggle(spieler.id) },
                    label = { Text("${spieler.vorname} ${spieler.nachname}".trim()) }
                )
            }
        }

        if (ausgewaehlt.size >= 3) {
            Text("Punkte eingeben:", style = MaterialTheme.typography.titleMedium)
            alleSpieler.filter { ausgewaehlt.contains(it.id) }.forEach { spieler ->
                OutlinedTextField(
                    value = punkte[spieler.id] ?: "",
                    onValueChange = { punkte = punkte + (spieler.id to it) },
                    label = { Text("${spieler.vorname} ${spieler.nachname}".trim()) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (fehler.isNotEmpty()) {
            Text(fehler, color = MaterialTheme.colorScheme.error)
        }

        if (erfolg) {
            Text("Event gespeichert!", color = MaterialTheme.colorScheme.primary)
        }

        Button(
            onClick = {
                fehler = ""
                erfolg = false
                val spiele = anzahlSpiele.toIntOrNull()
                if (datum.isBlank()) { fehler = "Bitte Datum angeben"; return@Button }
                val datumParsed = try {
                    LocalDate.parse(datum, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                } catch (e: Exception) {
                    fehler = "Datum ungültig (Format: TT.MM.JJJJ)"
                    return@Button
                }
                if (datumParsed.isAfter(LocalDate.now())) {
                    fehler = "Datum darf nicht in der Zukunft liegen"
                    return@Button
                }
                if (spiele == null || spiele < 1) { fehler = "Bitte gültige Spielanzahl angeben"; return@Button }

                if (ausgewaehlt.size < 3) { fehler = "Mindestens 3 Spieler auswählen"; return@Button }
                val teilnehmerMap = ausgewaehlt.associateWith { id ->
                    punkte[id]?.trim()?.toIntOrNull() ?: -1
                }
                if (teilnehmerMap.values.any { it < 0 }) { fehler = "Bitte alle Punkte eintragen"; return@Button }
                if (ausgewaehlterSpielTyp == null) {
                    fehler = "Bitte einen Spiel-Typ auswaehlen"
                    return@Button
                }
                viewModel.eventHinzufuegen(datum, spiele, startzeit, endzeit, teilnehmerMap, ausgewaehlterSpielTyp!!.id)
                anzahlSpiele = ""
                startzeit = ""
                endzeit = ""
                punkte = emptyMap()
                erfolg = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Event speichern")
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        Text("Spieler verwalten", style = MaterialTheme.typography.titleMedium)

        var neuerVorname by remember { mutableStateOf("") }
        var neuerNachname by remember { mutableStateOf("") }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = neuerVorname,
                onValueChange = { neuerVorname = it },
                label = { Text("Vorname") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = neuerNachname,
                onValueChange = { neuerNachname = it },
                label = { Text("Nachname") },
                modifier = Modifier.weight(1f)
            )
        }

        Button(
            onClick = {
                if (neuerVorname.isNotBlank()) {
                    viewModel.spielerHinzufuegen(neuerVorname.trim(), neuerNachname.trim())
                    neuerVorname = ""
                    neuerNachname = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Spieler hinzufügen")
        }

        alleSpieler.forEach { spieler ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSpielerClick(spieler.id) },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${spieler.vorname} ${spieler.nachname}".trim())
                Icon(
                    Icons.Filled.ArrowForward,
                    contentDescription = "Details",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}