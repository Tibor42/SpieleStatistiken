<?php
$name = trim($body['name'] ?? '');
$kennwort = trim($body['kennwort'] ?? '');
$email = trim($body['email'] ?? '');

if (empty($name) || empty($kennwort) || empty($email)) {
    json_response(400, ['fehler' => 'Name, Email und Kennwort sind erforderlich']);
}

$stmt = $db->prepare("SELECT * FROM spstat_gruppen WHERE name = ?");
$stmt->execute([$name]);
$gruppe = $stmt->fetch();

if (!$gruppe || !kennwort_verify($kennwort, $gruppe['kennwort_hash'])) {
    json_response(401, ['fehler' => 'Name oder Kennwort falsch']);
}

if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    json_response(400, ['fehler' => 'Email ist keine korrekte Email-Adresse']);
}

$stmt = $db->prepare("UPDATE spstat_gruppen SET email = ? where id =?");
$stmt->execute([$email,$gruppe['id']] );

json_response(200, [
    'id' => $gruppe['id'],
    'name' => $gruppe['name'],
    'nachricht' => $email. ' wurde eingetragen' ,
]);
?>
