<?php
$spielerId = $body['id'] ?? null;
$vorname = trim($body['vorname'] ?? '');
$nachname = trim($body['nachname'] ?? '');

if (!$spielerId || empty($vorname)) {
    json_response(400, ['fehler' => 'id und vorname sind erforderlich']);
}

$stmt = $db->prepare("UPDATE spstat_spieler SET vorname = ?, nachname = ? WHERE id = ?");
$stmt->execute([$vorname, $nachname, $spielerId]);

json_response(200, ['nachricht' => 'Spieler aktualisiert']);
?>