<?php
$gruppenId = $body['gruppen_id'] ?? null;

if (!$gruppenId) {
    json_response(400, ['fehler' => 'gruppen_id fehlt']);
}

pruefeFreischaltung($db, $gruppenId);

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
?>