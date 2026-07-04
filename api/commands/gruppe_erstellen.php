<?php
$name = trim($body['name'] ?? '');
$kennwort = trim($body['kennwort'] ?? '');
$email = trim($body['email'] ?? '');

if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    $email = null; 
}

if (empty($name) || empty($kennwort)) {
    json_response(400, ['fehler' => 'Name und Kennwort sind erforderlich']);
}

$kennwortHash = kennwort_hash($kennwort);

$stmt = $db->prepare("INSERT INTO spstat_gruppen (name, kennwort_hash, email) VALUES (?, ?, ?)");
$stmt->execute([$name, $kennwortHash, $email]);
$id = $db->lastInsertId();

json_response(201, [
    'id' => $id,
    'name' => $name,
    'freigeschaltet' => false,
    'email' => $email,
    'nachricht' => 'Gruppe erfolgreich erstellt'
]);
?>