<?php
$gruppenId = $body['gruppen_id'] ?? null;
$vorname = trim($body['vorname'] ?? '');
$nachname = trim($body['nachname'] ?? '');

if (!$gruppenId || empty($vorname)) {
    json_response(400, ['fehler' => 'gruppen_id und vorname sind erforderlich']);
}

pruefeFreischaltung($db, $gruppenId);

$stmt = $db->prepare("INSERT INTO spstat_spieler (gruppen_id, vorname, nachname) VALUES (?, ?, ?)");
$stmt->execute([$gruppenId, $vorname, $nachname]);
$id = $db->lastInsertId();

json_response(201, [
    'id' => $id,
    'gruppen_id' => $gruppenId,
    'vorname' => $vorname,
    'nachname' => $nachname
]);
?>