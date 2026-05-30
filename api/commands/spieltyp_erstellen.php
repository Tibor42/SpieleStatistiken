<?php
$gruppenId = $body['gruppen_id'] ?? null;
$name = trim($body['name'] ?? '');
$gewinnmodus = $body['gewinnmodus'] ?? 'wenigste';
$rundenRelevant = $body['runden_relevant'] ?? 1;

if (!$gruppenId || empty($name)) {
    json_response(400, ['fehler' => 'gruppen_id und name sind erforderlich']);
}

if (!in_array($gewinnmodus, ['wenigste', 'meiste'])) {
    json_response(400, ['fehler' => 'gewinnmodus muss "wenigste" oder "meiste" sein']);
}

$stmt = $db->prepare("INSERT INTO spstat_spiel_typ (gruppen_id, name, gewinnmodus, runden_relevant) VALUES (?, ?, ?, ?)");
$stmt->execute([$gruppenId, $name, $gewinnmodus, $rundenRelevant]);
$id = $db->lastInsertId();

json_response(201, [
    'id' => $id,
    'gruppen_id' => $gruppenId,
    'name' => $name,
    'gewinnmodus' => $gewinnmodus,
    'runden_relevant' => $rundenRelevant
]);
?>