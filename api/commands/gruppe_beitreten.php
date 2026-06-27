<?php
$name = trim($body['name'] ?? '');
$kennwort = trim($body['kennwort'] ?? '');

if (empty($name) || empty($kennwort)) {
    json_response(400, ['fehler' => 'Name und Kennwort sind erforderlich']);
}

$stmt = $db->prepare("SELECT * FROM spstat_gruppen WHERE name = ?");
$stmt->execute([$name]);
$gruppe = $stmt->fetch();

if (!$gruppe || !password_verify($kennwort, $gruppe['kennwort_hash'])) {
    json_response(401, ['fehler' => 'Name oder Kennwort falsch']);
}

json_response(200, [
    'id' => $gruppe['id'],
    'name' => $gruppe['name'],
    'freigeschaltet' => (bool)$gruppe['freigeschaltet'],
]);
?>
