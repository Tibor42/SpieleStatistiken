<?php
$gruppenId = $body['gruppen_id'] ?? null;

if (!$gruppenId) {
    json_response(400, ['fehler' => 'gruppen_id fehlt']);
}

$stmt = $db->prepare("SELECT * FROM spstat_spiel_typ WHERE gruppen_id = ? ORDER BY name ASC");
$stmt->execute([$gruppenId]);

json_response(200, $stmt->fetchAll());
?>