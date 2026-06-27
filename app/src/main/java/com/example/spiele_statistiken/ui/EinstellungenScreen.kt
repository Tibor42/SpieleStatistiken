package com.example.spiele_statistiken.ui

import androidx.compose.foundation.layout.*
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

    // Bereich B (lokal): Gruppe erstellen/beitreten
    var gruppenName by remember { mutableStateOf("") }
    var kennwort by remember { mutableStateOf("") }
    var meldung by remember { mutableStateOf("") }
    var istFehler by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var zeigeResetDialog by remember { mutableStateOf(false) }

    // Bereich A (online): Kontakt-E-Mail fuer den Reset
    var emailEingabe by remember { mutableStateOf("") }
    var emailKennwort by remember { mutableStateOf("") }
    var emailMeldung by remember { mutableStateOf("") }
    var emailIstFehler by remember { mutableStateOf(false) }
    var emailLoading by remember { mutableStateOf(false) }

    // Online-Status als eigener State -> sofortiges Ein-/Ausblenden.
    var istOnline by remember { mutableStateOf(prefs.istOnline) }
    val aktuelleGruppe = prefs.gruppenName

    if (zeigeResetDialog) {
        PasswortResetDialog(
            viewModel = viewModel,
            initialerGruppenName = gruppenName,
            onDismiss = { zeigeResetDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Einstellungen", style = MaterialTheme.typography.headlineSmall)

        // ---------- Bereich A: aktueller Sync-Status (immer sichtbar) ----------
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
                            istOnline = false
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

        // ---------- Bereich A (Forts.): Kontakt-E-Mail fuer Passwort-Reset ----------
        // Nur online sinnvoll: man muss das aktuelle Kennwort kennen, um die
        // E-Mail zu hinterlegen, an die spaeter der Reset-Code geht.
        if (istOnline) {
            HorizontalDivider()

            Text("Kontakt-E-Mail für Passwort-Reset", style = MaterialTheme.typography.titleMedium)
            Text(
                "Damit das Gruppen-Kennwort später per E-Mail zurückgesetzt werden " +
                        "kann, hier eine E-Mail hinterlegen. Zur Bestätigung das aktuelle " +
                        "Kennwort eingeben.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = emailEingabe,
                onValueChange = { emailEingabe = it },
                label = { Text("E-Mail") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = emailKennwort,
                onValueChange = { emailKennwort = it },
                label = { Text("Aktuelles Kennwort") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (emailMeldung.isNotEmpty()) {
                Text(
                    text = emailMeldung,
                    color = if (emailIstFehler) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary
                )
            }

            if (emailLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        if (emailEingabe.isNotBlank() && emailKennwort.isNotBlank()) {
                            emailLoading = true
                            emailMeldung = ""
                            viewModel.gruppenEmailSetzen(
                                aktuelleGruppe, emailKennwort, emailEingabe.trim()
                            ) { ergebnis ->
                                emailLoading = false
                                emailMeldung = ergebnis.nachricht
                                emailIstFehler = !ergebnis.erfolg
                                if (ergebnis.erfolg) {
                                    emailKennwort = ""   // Kennwort nicht im UI haengen lassen
                                }
                            }
                        }
                    }
                ) {
                    Text("E-Mail speichern")
                }
            }
        }

        // ---------- Bereich B: Gruppe erstellen/beitreten (NUR lokal) ----------
        if (!istOnline) {
            HorizontalDivider()

            Text(
                "Gruppe erstellen oder beitreten:",
                style = MaterialTheme.typography.titleMedium
            )

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
                                    if (ergebnis.erfolg && ergebnis.gruppenId != null) {
                                        prefs.syncModus = "online"
                                        prefs.gruppenName = gruppenName.trim()
                                        prefs.istFreigeschaltet = ergebnis.freigeschaltet
                                        prefs.gruppenId = ergebnis.gruppenId
                                        istOnline = true
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
                                    if (ergebnis.erfolg && ergebnis.gruppenId != null) {
                                        prefs.syncModus = "online"
                                        prefs.gruppenName = gruppenName.trim()
                                        prefs.istFreigeschaltet = ergebnis.freigeschaltet
                                        prefs.gruppenId = ergebnis.gruppenId
                                        istOnline = true
                                    }
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Beitreten")
                    }
                }

                // Reset-Einstieg: nur relevant, wenn man beitreten will.
                TextButton(onClick = { zeigeResetDialog = true }) {
                    Text("Kennwort vergessen?")
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

// =====================================================================
// Reset-Dialog: zweistufig.
//  Schritt 1: Gruppenname -> Code anfordern (Antwort bewusst neutral).
//  Schritt 2: Code + neues Kennwort -> aendern.
// =====================================================================
@Composable
private fun PasswortResetDialog(
    viewModel: SpielerStatistikViewModel,
    initialerGruppenName: String,
    onDismiss: () -> Unit
) {
    var schritt by remember { mutableStateOf(1) }
    var gruppenName by remember { mutableStateOf(initialerGruppenName) }
    var code by remember { mutableStateOf("") }
    var neuesKennwort by remember { mutableStateOf("") }
    var meldung by remember { mutableStateOf("") }
    var istFehler by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = { Text(if (schritt == 1) "Kennwort vergessen" else "Code eingeben") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (schritt == 1) {
                    Text(
                        "Gruppennamen eingeben. Ist eine Kontakt-E-Mail hinterlegt, " +
                                "wird ein 6-stelliger Code dorthin gesendet.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    OutlinedTextField(
                        value = gruppenName,
                        onValueChange = { gruppenName = it },
                        label = { Text("Gruppenname") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        "Code aus der E-Mail eingeben und neues Kennwort setzen. " +
                                "Der Code ist 15 Minuten gueltig.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it },
                        label = { Text("Code (6-stellig)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = neuesKennwort,
                        onValueChange = { neuesKennwort = it },
                        label = { Text("Neues Kennwort (min. 8 Zeichen)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (meldung.isNotEmpty()) {
                    Text(
                        meldung,
                        color = if (istFehler) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }
        },
        confirmButton = {
            if (schritt == 1) {
                TextButton(
                    enabled = !isLoading && gruppenName.isNotBlank(),
                    onClick = {
                        isLoading = true
                        meldung = ""
                        viewModel.kennwortResetAnfordern(gruppenName.trim()) { ergebnis ->
                            isLoading = false
                            // Immer zu Schritt 2 - die Antwort ist absichtlich neutral,
                            // damit nicht erkennbar ist, ob die Gruppe existiert.
                            meldung = ergebnis.nachricht
                            istFehler = false
                            schritt = 2
                        }
                    }
                ) { Text("Code anfordern") }
            } else {
                TextButton(
                    enabled = !isLoading && code.isNotBlank() && neuesKennwort.length >= 8,
                    onClick = {
                        isLoading = true
                        meldung = ""
                        viewModel.kennwortResetDurchfuehren(
                            gruppenName.trim(), code.trim(), neuesKennwort
                        ) { ergebnis ->
                            isLoading = false
                            meldung = ergebnis.nachricht
                            istFehler = !ergebnis.erfolg
                            if (ergebnis.erfolg) {
                                onDismiss()
                            }
                        }
                    }
                ) { Text("Kennwort aendern") }
            }
        },
        dismissButton = {
            TextButton(enabled = !isLoading, onClick = onDismiss) { Text("Abbrechen") }
        }
    )
}