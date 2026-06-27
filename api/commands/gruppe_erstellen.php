<?php
$name = trim($body['name'] ?? '');
$kennwort = trim($body['kennwort'] ?? '');

if (empty($name) || empty($kennwort)) {
    json_response(400, ['fehler' => 'Name und Kennwort sind erforderlich']);
}

$kennwortHash =   kennwort_hash($kennwort);

$stmt = $db->prepare("INSERT INTO spstat_gruppen (name, kennwort_hash) VALUES (?, ?)");
$stmt->execute([$name, $kennwortHash]);
$id = $db->lastInsertId();

json_response(201, [
    'id' => $id,
    'name' => $name,
    'freigeschaltet' => false,
    'nachricht' => 'Gruppe erfolgreich erstellt'
]);
?>