<?php
$gruppenId = $body['gruppen_id'] ?? null;
$datum = trim($body['datum'] ?? '');
$anzahlSpiele = $body['anzahl_spiele'] ?? null;
$spielTypId = $body['spiel_typ_id'] ?? null;
$startzeit = trim($body['startzeit'] ?? '');
$endzeit = trim($body['endzeit'] ?? '');
$teilnehmer = $body['teilnehmer'] ?? [];

if (!$gruppenId || empty($datum) || !$anzahlSpiele) {
    json_response(400, ['fehler' => 'gruppen_id, datum und anzahl_spiele sind erforderlich']);
}

if (count($teilnehmer) < 2) {
    json_response(400, ['fehler' => 'Mindestens 2 Teilnehmer erforderlich']);
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
?>