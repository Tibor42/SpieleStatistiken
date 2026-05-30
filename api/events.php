<?php
require_once 'config.php';

$db = getDB();
$parts = explode('/', trim($request, '/'));
$eventId = $parts[1] ?? null;

match($method) {
    'GET' => alleEventsAbrufen($db),
    'POST' => eventErstellen($db, $body),
    'DELETE' => eventLoeschen($db, $eventId),
    default => json_response(405, ['fehler' => 'Methode nicht erlaubt'])
};

function alleEventsAbrufen(PDO $db): void {
    $gruppenId = $_GET['gruppen_id'] ?? null;
    if (!$gruppenId) {
        json_response(400, ['fehler' => 'gruppen_id fehlt']);
        return;
    }

    $stmt = $db->prepare("
        SELECT e.*,
               t.spieler_id,
               t.punkte,
               s.vorname,
               s.nachname
        FROM spstat_spiel_event e
        LEFT JOIN spstat_spiel_event_teilnehmer t ON e.id = t.event_id
        LEFT JOIN spstat_spieler s ON t.spieler_id = s.id
        WHERE e.gruppen_id = ?
        ORDER BY e.datum DESC, e.id DESC
    ");
    $stmt->execute([$gruppenId]);
    $rows = $stmt->fetchAll();

    // Events gruppieren
    $events = [];
    foreach ($rows as $row) {
        $eventId = $row['id'];
        if (!isset($events[$eventId])) {
            $events[$eventId] = [
                'id' => $row['id'],
                'gruppen_id' => $row['gruppen_id'],
                'spiel_typ_id' => $row['spiel_typ_id'],
                'datum' => $row['datum'],
                'startzeit' => $row['startzeit'],
                'endzeit' => $row['endzeit'],
                'anzahl_spiele' => $row['anzahl_spiele'],
                'teilnehmer' => []
            ];
        }
        if ($row['spieler_id']) {
            $events[$eventId]['teilnehmer'][] = [
                'spieler_id' => $row['spieler_id'],
                'vorname' => $row['vorname'],
                'nachname' => $row['nachname'],
                'punkte' => $row['punkte']
            ];
        }
    }

    json_response(200, array_values($events));
}

function eventErstellen(PDO $db, array $body): void {
    $gruppenId = $body['gruppen_id'] ?? null;
    $datum = trim($body['datum'] ?? '');
    $anzahlSpiele = $body['anzahl_spiele'] ?? null;
    $spielTypId = $body['spiel_typ_id'] ?? null;
    $startzeit = trim($body['startzeit'] ?? '');
    $endzeit = trim($body['endzeit'] ?? '');
    $teilnehmer = $body['teilnehmer'] ?? [];

    if (!$gruppenId || empty($datum) || !$anzahlSpiele) {
        json_response(400, ['fehler' => 'gruppen_id, datum und anzahl_spiele sind erforderlich']);
        return;
    }

    if (count($teilnehmer) < 2) {
        json_response(400, ['fehler' => 'Mindestens 2 Teilnehmer erforderlich']);
        return;
    }

    try {
        $db->beginTransaction();

        $stmt = $db->prepare("
            INSERT INTO spstat_spiel_event
            (gruppen_id, spiel_typ_id, datum, startzeit, endzeit, anzahl_spiele)
            VALUES (?, ?, ?, ?, ?, ?)
        ");
        $stmt->execute([$gruppenId, $spielTypId, $datum, $startzeit, $endzeit, $anzahlSpiele]);
        $eventId = $db->lastInsertId();

        $stmtT = $db->prepare("
            INSERT INTO spstat_spiel_event_teilnehmer (event_id, spieler_id, punkte)
            VALUES (?, ?, ?)
        ");
        foreach ($teilnehmer as $t) {
            $stmtT->execute([$eventId, $t['spieler_id'], $t['punkte']]);
        }

        $db->commit();
        json_response(201, ['id' => $eventId, 'nachricht' => 'Event erfolgreich erstellt']);

    } catch (PDOException $e) {
        $db->rollBack();
        json_response(500, ['fehler' => 'Datenbankfehler: ' . $e->getMessage()]);
    }
}

function eventLoeschen(PDO $db, ?string $eventId): void {
    if (!$eventId) {
        json_response(400, ['fehler' => 'Event-ID fehlt']);
        return;
    }

    $stmt = $db->prepare("DELETE FROM spstat_spiel_event WHERE id = ?");
    $stmt->execute([$eventId]);
    json_response(200, ['nachricht' => 'Event gelöscht']);
}
?>