<?php

define('MAX_LOGIN_VERSUCHE', 5);

$error_msg = "Fehler beim Login. Bitte überprüfen Sie Name und Kennwort.";

$name = trim($body['name'] ?? '');
$kennwort = trim($body['kennwort'] ?? '');

$ip = $_SERVER['REMOTE_ADDR'] ?? '';

if (empty($name) || empty($kennwort)) {
    json_response(400, ['fehler' => 'Name und Kennwort sind erforderlich']);
}

$stmt = $db->prepare("SELECT count(*) from spstat_login_errors WHERE ip_adresse = ? AND erstellt_am > NOW() - INTERVAL 1 MINUTE");
$stmt->execute([$ip]);
$login_errors = $stmt->fetchColumn();

if ($login_errors >= MAX_LOGIN_VERSUCHE) {
    insert_login_error($db, $ip);
    json_response(429, ['fehler' => $error_msg]);
}

$stmt = $db->prepare("SELECT * FROM spstat_gruppen WHERE name = ?");
$stmt->execute([$name]);
$gruppe = $stmt->fetch();

if (!$gruppe || !kennwort_verify($kennwort, $gruppe['kennwort_hash'])) {
    insert_login_error($db, $ip);
    json_response(401, ['fehler' => $error_msg]);
}

delete_login_errors($db, $ip);

json_response(200, [
    'id' => $gruppe['id'],
    'name' => $gruppe['name'],
    'freigeschaltet' => (bool) $gruppe['freigeschaltet'],
    'email' => $gruppe['email'] ?? null,
]);

function insert_login_error($db, $ip) {
    $stmt = $db->prepare("INSERT INTO spstat_login_errors (ip_adresse) VALUES (?)");
    $stmt->execute([$ip]);
}

function delete_login_errors($db, $ip) {
    $stmt = $db->prepare("DELETE FROM spstat_login_errors WHERE ip_adresse = ?");
    $stmt->execute([$ip]);
}
?>
