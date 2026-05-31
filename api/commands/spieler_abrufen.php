<?php
$gruppenId = $body['gruppen_id'] ?? null;

if (!$gruppenId) {
    json_response(400, ['fehler' => 'gruppen_id fehlt']);
}

pruefeFreischaltung($db, $gruppenId);

$stmt = $db->prepare("SELECT * FROM spstat_spieler WHERE gruppen_id = ? ORDER BY vorname ASC");
$stmt->execute([$gruppenId]);

json_response(200, $stmt->fetchAll());
?>