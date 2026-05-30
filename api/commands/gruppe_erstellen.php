<?php
$name = trim($body['name'] ?? '');
$kennwort = trim($body['kennwort'] ?? '');

if (empty($name) || empty($kennwort)) {
    json_response(400, ['fehler' => 'Name und Kennwort sind erforderlich']);
}

$kennwortHash = password_hash($kennwort, PASSWORD_BCRYPT);

$stmt = $db->prepare("INSERT INTO spstat_gruppen (name, kennwort_hash) VALUES (?, ?)");
$stmt->execute([$name, $kennwortHash]);
$id = $db->lastInsertId();

json_response(201, [
    'id' => $id,
    'name' => $name,
    'nachricht' => 'Gruppe erfolgreich erstellt'
]);
?>