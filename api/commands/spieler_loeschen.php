<?php
$spielerId = $body['id'] ?? null;

if (!$spielerId) {
    json_response(400, ['fehler' => 'id fehlt']);
}

try {
    $stmt = $db->prepare("DELETE FROM spstat_spieler WHERE id = ?");
    $stmt->execute([$spielerId]);
    json_response(200, ['nachricht' => 'Spieler gelöscht']);
} catch (PDOException $e) {
    json_response(409, ['fehler' => 'Spieler kann nicht gelöscht werden – noch Events vorhanden']);
}
?>