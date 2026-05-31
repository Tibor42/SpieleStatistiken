<?php
$eventId = $body['id'] ?? null;
$gruppenId = $body['gruppen_id'] ?? null;

if (!$eventId) {
    json_response(400, ['fehler' => 'id fehlt']);
}

pruefeFreischaltung($db, $gruppenId);

try {
    $stmt = $db->prepare("DELETE FROM spstat_spiel_event WHERE id = ? AND gruppen_id = ?");
    $stmt->execute([$eventId, $gruppenId]);
    json_response(200, ['nachricht' => 'Event gelöscht']);
} catch (PDOException $e) {
    json_response(500, ['fehler' => 'Datenbankfehler: ' . $e->getMessage()]);
}
?>