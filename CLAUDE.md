# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

# Projektkontext: Spiele-Statistiken

## Overview

Android-App zur Erfassung von Karten- und Brettspiel-Ergebnissen im Familien-
und Freundeskreis. Ausgangspunkt war die Rommé-Variante "Räuber Rommé",
inzwischen sind beliebige Spieltypen anlegbar.

Das Projekt ist Teil eines Portfolios, das im Play Store veröffentlicht werden
soll. Die Veröffentlichung ist ausdrücklich kein Monetarisierungsziel, sondern
dient dazu, ein öffentliches Entwicklerprofil aufzubauen und Projekte
tatsächlich zu 100 % fertigzustellen statt bei 90 % liegenzulassen. Bei
Architektur- und Feature-Entscheidungen daher immer mitdenken:
Store-Richtlinien, Altersfreigabe, Marken-/Urheberrecht, nötige Disclaimer.

The repo contains two independent codebases:
- `app/` — the Android app (Kotlin, Jetpack Compose, Room).
- `api/` — a standalone PHP RPC-style(command dispatch, not REST) backend for optional online/multi-device sync, deployed
  separately (not built by Gradle).


## Sokrates-Modell (Standardmodus der Zusammenarbeit)

Dieses Projekt dient dem Lernen, nicht dem schnellen Abliefern. Arbeite daher
standardmäßig sokratisch:

- **Stelle Fragen, statt Lösungen vorwegzunehmen.** Führe mich über gezielte
  Rückfragen zur Lösung, statt sie zu präsentieren.
- **Zeige Code nur auf ausdrückliche Aufforderung.** Formulierungen wie "zeig
  mir die Lösung", "schreib den Code" oder "mach es" sind das Signal. Ein
  beschriebenes Problem allein ist noch keine Aufforderung.
- **Ändere keine Dateien ungefragt.** Erst besprechen, dann implementieren.
- **Analyse und Diagnose sind ausdrücklich erwünscht.** Fehlerursachen,
  Zusammenhänge und Konsequenzen einer Entscheidung darfst du direkt benennen
  — der zurückhaltende Teil betrifft die fertige Lösung, nicht das Verständnis.
- **Sag es, wenn ein Ansatz in eine Sackgasse führt.** Das Sokrates-Modell ist
  kein Grund, mich in einen Fehler laufen zu lassen. Widerspruch und
  Gegenvorschläge sind Teil der Methode.
- **Wenn ich um die Lösung bitte, dann vollständig.** Kein absichtliches
  Auslassen von Teilen zu didaktischen Zwecken.
- **Keine Subagents.** Starte keine parallelen Agenten, um Teilaufgaben
  auszulagern. Arbeite die Aufgabe in der laufenden Sitzung ab, damit ich jeden
  Schritt mitlesen kann. Wenn du einen Subagent für sinnvoll hältst, frag
  vorher.

## Technischer Stack

- **Hosting:** thurnay.de, Zugriff über phpMyAdmin
- **API-Pfad:** `thurnay.de/apps/spstat/api/`
- **Tabellenpräfix:** `spstat_`
- **Umgebung:** Arch Linux mit KDE Plasma, Android Studio

## Architekturprinzipien

- **Local-first:** Room ist die primäre Datenhaltung, der Server-Sync ist
  optional. Die App muss ohne Netzverbindung vollständig nutzbar bleiben.
- **RPC statt REST:** Bewusste Entscheidung auf Basis langjähriger
  PHP-Erfahrung. Command-Pattern auf Serverseite. Nicht zu REST umbauen
  vorschlagen.
- **Freischaltungs-Pattern:** Gruppen lassen sich frei anlegen, der
  Online-Sync wird jedoch manuell in der Datenbank freigeschaltet.

## Codekonventionen

- **Deutsche Bezeichner** in Kotlin (Spieler, SpielEvent, Teilnehmer,
  Gewinnmodus, ...). Keine englischen Variablennamen einführen.
- **Explizite Typangaben** bei `var`/`val`, auch wo die Inferenz reichen würde.
- **ViewModels** initialisieren im `init`-Block.
- **Room:** Spaltennamen in snake_case über `@ColumnInfo`.
- **PHP:** schließende `?>`-Tags werden gesetzt.
- Keine Semikolons am Zeilenende in Kotlin.

## Stand der Umsetzung

**Schritt 1 — fertig.** Lokale Room-Datenbank: Entities `Spieler`,
`SpielEvent`, `SpielEventTeilnehmer`, zugehörige DAOs, Repository, ViewModel,
Compose-UI mit Navigation, Statistik- und Spielerdetailansicht.

**Schritt 2 — fertig.** SpielTyp-Verwaltung mit konfigurierbarem Gewinnmodus
(`"wenigste"` / `"meiste"`) und Flag `rundenRelevant`. Datenbankmigrationen,
nach Spieltyp gruppierte Statistiken, Vorschlag des zuletzt gespielten Typs.
Schemanänderungen bisher über destruktive Migration.

**Schritt 3 — fertig.** PHP-RPC-API inklusive Android-Anbindung
(`RemoteRepository`, `ApiService`, `ApiModels`). Spieler-Gruppen mit Name und
Passwort, Mehrgeräte-Sync über die Webdatenbank, Freischaltungssystem.

**Schritt 4 — teilweise umgesetzt.** Passwort-Reset per E-Mail und
Security-Hardening. PHPMailer und E-Mail-Feld sind vorhanden, Rate Limiting
fehlt.

**Schritt 5 — internationalisierung.** Texte mehrsprachig anbieten (Englisch und Deutsch)

**Schritt 6: echte Room-Migrationen schreiben**

**Schritt 7 — später.** OCR-/KI-Auswertung handschriftlicher Spielzettel per
Foto. Mehrstufig und aufwendig, bewusst ans Ende geschoben.

## Bekannte Fallstricke

- `SQLiteConstraintException` bei Foreign-Key-Verletzungen gezielt fangen,
  nicht über ein generisches `Exception`.
- `fallbackToDestructiveMigration(true)` ist bewusst nur für die
  Entwicklungsphase gesetzt und muss vor der Veröffentlichung raus.
- `maxByOrNull` / `minByOrNull` statt `maxBy` / `minBy` verwenden — Letztere
  werfen bei leerer Liste eine `NoSuchElementException`.
- Der Gewinnmodus muss überall dort berücksichtigt werden, wo Gewinner
  ermittelt oder Teilnehmer sortiert werden — nicht nur in der Statistik,
  sondern auch in der Events-Übersicht.
- Punktegleichstand ist bei Rommé technisch ausgeschlossen und bei Kniffel
  praktisch nie aufgetreten. Bei Canasta mit Paaren wäre er relevant, ist aber
  bisher nicht modelliert.

## Arbeitsweise

- Das Projekt wird in nummerierten Schritten entwickelt. Neue Arbeit ordnet
  sich in diese Schrittfolge ein.
- Vor größeren Umbauten committen.
- Änderungen bevorzugt schrittweise und nachvollziehbar statt in einem großen
  Wurf.

## Commands

All Gradle commands use the wrapper from the repo root.

```bash
./gradlew assembleDebug                 # build debug APK
./gradlew installDebug                  # build + install on connected device/emulator
./gradlew test                          # JVM unit tests (app/src/test)
./gradlew test --tests "com.example.spiele_statistiken.ExampleUnitTest"   # single test class
./gradlew connectedAndroidTest          # instrumented tests (needs device/emulator)
./gradlew lint                          # Android lint; report in app/build/reports/lint-results-debug.html
```

Only stub example tests exist so far.

## Toolchain

`compileSdk`/`targetSdk` 36, `minSdk` 26, Java 11. Konkrete Versionen von
Gradle, AGP, Kotlin und KSP stehen in `gradle/libs.versions.toml` — dort
nachsehen statt hier. Seit KSP 2.3 ist die KSP-Version nicht mehr an die
Kotlin-Version gekoppelt.
## App architecture

Single-Activity Compose app. `MainActivity` → `MainScreen` holds the `NavHost` and a
`BottomNavBar`; routes are plain strings (`neues_event`, `events`, `statistik`,
`spiel_typen`, `spieler_detail/{spielerId}`, `einstellungen`, `about`). Start destination
is `neues_event`.

**One shared ViewModel.** `SpielerStatistikViewModel` (an `AndroidViewModel`) is created
once in `MainScreen` via `viewModel()` and passed to every screen. There is no DI framework
and no per-screen ViewModel.

**Data layers:**
- `data/` — Room. `AppDatabase` (singleton, `version = 6`, `fallbackToDestructiveMigration`,
  schemas exported to `app/schemas/`). Entities: `Spieler`, `SpielEvent`,
  `SpielEventTeilnehmer` (join table with composite PK), `SpielTyp`. DAOs in `Daos.kt`,
  wrapped by `Repository`. **Bumping schema requires incrementing the `@Database` version**;
  destructive migration means local data is wiped on schema change.
- `network/` — Retrofit client (`RetrofitClient`, base URL `https://thurnay.de/apps/spstat/api/`)
  talking to the PHP backend. All calls `POST` to `.` with an `ApiRequest` body whose `cmd`
  field selects the operation; `ApiService` declares one method per response shape.
  `RemoteRepository` builds the requests.
- `AppPreferences` — `SharedPreferences` wrapper for sync state: `gruppenId`, `gruppenName`,
  `email`, `syncModus` (`"lokal"` / `"online"`), `istFreigeschaltet`.

**Local-first sync model.** Room is the source of truth. When
`istOnline && istFreigeschaltet`, `datenLaden()` overlays remote data into the
`_spielerListe` / `_spielTypListe` / `_eventListe` StateFlows (falling back to local on
error); otherwise those flows mirror the Room flows. Screens read the StateFlows, not the
raw repository flows, so both modes look the same to the UI. A "Gruppe" (group, name +
password) is the sync account; online mode must be manually unlocked server-side
(`freigeschaltet`).

**Statistics** are computed client-side in `StatistikScreen.kt` (`berechneStatistiken`),
grouped per `SpielTyp`. `SpielTyp.gewinnmodus` (`"wenigste"` / `"meiste"`) decides whether
low or high score wins.

## PHP backend (`api/`)

Single entry point `api/index.php`: reads JSON body, validates `cmd` against an allow-list,
then `require`s `api/commands/<cmd>.php`. Shared helpers in `api/config.php` (`getDB()` PDO,
`pruefeFreischaltung()`) and `api/tools.inc.php` (password hashing, random tokens). MySQL
tables are prefixed `spstat_`. Email (password reset) via bundled PHPMailer.

Deployed by SFTP-on-save (VS Code SFTP extension config in `api/.vscode/sftp.json`) to
shared hosting. `.htaccess` routes all non-file requests to `index.php`.

**Secrets:** `api/config.php` and `api/.vscode/sftp.json` are git-ignored and hold live DB,
email, and FTP credentials. Never commit them or copy their contents elsewhere.

## Localization

Localization was recently started and is incomplete. `res/values/strings.xml` is the
default (English), `res/values-de/strings.xml` is German. Most UI strings are still
hardcoded German literals in the Compose code; migrate them to `strings.xml` when touching a
screen.
