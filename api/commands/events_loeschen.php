<?php
$eventId = $body['id'] ?? null;

if (!$eventId) {
    json_response(400, ['fehler' => 'id fehlt']);
}

try {
    $stmt = $db->prepare("DELETE FROM spstat_spiel_event WHERE id = ?");
    $stmt->execute([$eventId]);
    json_response(200, ['nachricht' => 'Event gelöscht']);
} catch (PDOException $e) {
    json_response(500, ['fehler' => 'Datenbankfehler: ' . $e->getMessage()]);
}
?>