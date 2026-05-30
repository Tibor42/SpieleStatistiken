<?php
$spielTypId = $body['id'] ?? null;

if (!$spielTypId) {
    json_response(400, ['fehler' => 'id fehlt']);
}

try {
    $stmt = $db->prepare("DELETE FROM spstat_spiel_typ WHERE id = ?");
    $stmt->execute([$spielTypId]);
    json_response(200, ['nachricht' => 'Spiel-Typ gelöscht']);
} catch (PDOException $e) {
    json_response(409, ['fehler' => 'Spiel-Typ kann nicht gelöscht werden – noch Events vorhanden']);
}
?>