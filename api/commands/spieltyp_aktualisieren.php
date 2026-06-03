<?php
$spielTypId = $body['id'] ?? null;
$name = trim($body['name'] ?? '');
$gewinnmodus = $body['gewinnmodus'] ?? 'wenigste';
$rundenRelevant = $body['runden_relevant'] ?? 1;
$gruppenId = $body['gruppen_id'] ?? null;

pruefeFreischaltung($db, $gruppenId);

if (!$spielTypId || empty($name)) {
    json_response(400, ['fehler' => 'id und name sind erforderlich']);
}

if (!in_array($gewinnmodus, ['wenigste', 'meiste'])) {
    json_response(400, ['fehler' => 'gewinnmodus muss "wenigste" oder "meiste" sein']);
}

$stmt = $db->prepare("UPDATE spstat_spiel_typ SET name = ?, gewinnmodus = ?, runden_relevant = ? WHERE id = ? and gruppen_id = ?");
$stmt->execute([$name, $gewinnmodus, $rundenRelevant, $spielTypId, $gruppenId]);

json_response(200, ['nachricht' => 'Spiel-Typ aktualisiert']);
?>