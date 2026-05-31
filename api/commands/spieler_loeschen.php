<?php
$spielerId = $body['id'] ?? null;
$gruppenId = $body['gruppen_id'] ?? null;

if (!$spielerId) {
    json_response(400, ['fehler' => 'id fehlt']);
}

pruefeFreischaltung($db, $gruppenId);

try {
    $stmt = $db->prepare("DELETE FROM spstat_spieler WHERE id = ? AND gruppen_id = ?");
    $stmt->execute([$spielerId, $gruppenId]);
    json_response(200, ['nachricht' => 'Spieler gelöscht']);
} catch (PDOException $e) {
    json_response(409, ['fehler' => 'Spieler kann nicht gelöscht werden – noch Events vorhanden']);
}
?>