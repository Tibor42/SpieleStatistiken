package com.example.spiele_statistiken.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import com.example.spiele_statistiken.data.SpielTyp
import com.example.spiele_statistiken.viewmodel.SpielerStatistikViewModel

@Composable
fun SpielTypScreen(
    viewModel: SpielerStatistikViewModel,
    innerPadding: PaddingValues
) {

    val alleSpielTypen by viewModel.alleSpielTypen.collectAsStateWithLifecycle(initialValue = emptyList<SpielTyp>())

    var neuerName by remember { mutableStateOf("") }
    var gewinnmodus by remember { mutableStateOf("wenigste") }

    var rundenRelevant by remember { mutableStateOf(true) }

    val spielTypFehler by viewModel.spielTypFehler.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text("Spiel-Typen verwalten", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = neuerName,
                onValueChange = { neuerName = it },
                label = { Text("Name des Spiels") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Gewinnmodus:", style = MaterialTheme.typography.titleSmall)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = gewinnmodus == "wenigste",
                    onClick = { gewinnmodus = "wenigste" }
                )
                Text("Wer die wenigsten Punkte hat gewinnt")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = gewinnmodus == "meiste",
                    onClick = { gewinnmodus = "meiste" }
                )
                Text("Wer die meisten Punkte hat gewinnt")
            }
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()) {

                Text("Runden relevant (Bps. Romme/Skat)")
                Switch(
                    checked = rundenRelevant,
                    onCheckedChange = { rundenRelevant = it }
                )

            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (neuerName.isNotBlank()) {
                        viewModel.spielTypHinzufuegen(neuerName.trim(), gewinnmodus, rundenRelevant)
                        neuerName = ""
                        gewinnmodus = "wenigste"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Spiel-Typ hinzufügen")
            }

            if (spielTypFehler.isNotEmpty()) {
                Text(
                    text = spielTypFehler,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            Text("Angelegte Spiel-Typen:", style = MaterialTheme.typography.titleMedium)
        }

        if (alleSpielTypen.isEmpty()) {
            item {
                Text(
                    "Noch keine Spiel-Typen angelegt.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(alleSpielTypen) { spielTyp ->
            SpielTypZeile(spielTyp = spielTyp, viewModel = viewModel)
        }
    }
}

@Composable
fun SpielTypZeile(spielTyp: SpielTyp, viewModel: SpielerStatistikViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(spielTyp.name, style = MaterialTheme.typography.bodyLarge)
            Text(
                if (spielTyp.gewinnmodus == "wenigste")
                    "Wenigste Punkte gewinnt"
                else "Meiste Punkte gewinnt",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                if (spielTyp.rundenRelevant) {
                    "Runden sind relevant"
                } else "Runden spielen keine Rolle",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(onClick = { viewModel.spielTypLoeschen(spielTyp) }) {
            Icon(Icons.Filled.Delete, contentDescription = "Löschen")
        }
    }
    HorizontalDivider()
}