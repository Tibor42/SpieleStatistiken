package com.example.spiele_statistiken.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.spiele_statistiken.data.AppPreferences
import com.example.spiele_statistiken.viewmodel.SpielerStatistikViewModel

@Composable
fun EinstellungenScreen(
    viewModel: SpielerStatistikViewModel,
    innerPadding: PaddingValues,
    onAboutClick: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { AppPreferences(context) }

    var gruppenName by remember { mutableStateOf("") }
    var kennwort by remember { mutableStateOf("") }
    var meldung by remember { mutableStateOf("") }
    var istFehler by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val aktuelleGruppe = prefs.gruppenName
    val istOnline = prefs.istOnline

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Einstellungen", style = MaterialTheme.typography.headlineSmall)

        // Aktueller Status
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Sync-Modus:", style = MaterialTheme.typography.titleSmall)
                Text(
                    if (istOnline) "Online – Gruppe: $aktuelleGruppe"
                    else "Lokal (kein Server)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (istOnline) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (istOnline) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            prefs.abmelden()
                            viewModel.setSyncModus("lokal")
                            meldung = "Abgemeldet – lokaler Modus aktiv"
                            istFehler = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Abmelden")
                    }
                }
            }
        }

        HorizontalDivider()

        Text("Gruppe erstellen oder beitreten:", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = gruppenName,
            onValueChange = { gruppenName = it },
            label = { Text("Gruppenname") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = kennwort,
            onValueChange = { kennwort = it },
            label = { Text("Kennwort") },
            modifier = Modifier.fillMaxWidth()
        )

        if (meldung.isNotEmpty()) {
            Text(
                text = meldung,
                color = if (istFehler) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.primary
            )
        }

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        if (gruppenName.isNotBlank() && kennwort.isNotBlank()) {
                            isLoading = true
                            viewModel.gruppeErstellen(gruppenName.trim(), kennwort.trim()) { ergebnis ->
                                isLoading = false
                                meldung = ergebnis.nachricht
                                istFehler = !ergebnis.erfolg
                                if (ergebnis.erfolg && ergebnis.gruppenId!=null) {
                                    prefs.syncModus = "online"
                                    prefs.gruppenName = gruppenName.trim()
                                    prefs.istFreigeschaltet = ergebnis.freigeschaltet
                                    prefs.gruppenId = ergebnis.gruppenId
                                }
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Erstellen")
                }
                Button(
                    onClick = {
                        if (gruppenName.isNotBlank() && kennwort.isNotBlank()) {
                            isLoading = true
                            viewModel.gruppeBeitreten(gruppenName.trim(), kennwort.trim()) { ergebnis ->
                                isLoading = false
                                meldung = ergebnis.nachricht
                                istFehler = !ergebnis.erfolg
                                if (ergebnis.erfolg && ergebnis.gruppenId!=null) {
                                    prefs.syncModus = "online"
                                    prefs.gruppenName = gruppenName.trim()
                                    prefs.istFreigeschaltet = ergebnis.freigeschaltet
                                    prefs.gruppenId = ergebnis.gruppenId
                                }
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Beitreten")
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        HorizontalDivider()

        TextButton(
            onClick = onAboutClick,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Ueber diese App")
        }

    }
}