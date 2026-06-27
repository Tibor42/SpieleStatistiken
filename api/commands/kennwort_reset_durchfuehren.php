<?php

define('NUMBER_OF_TRIES', 5);

$name = trim($body['name'] ?? '');
$kennwort = trim($body['kennwort'] ??'');
$code = trim($body['code'] ??'');

$blnSendMail = false;

$errorMsg = 'Das passt nicht zusammen. Bitte überprüfe deine Eingaben und versuche es erneut.';
$resultMsg = 'Das Kennwort wurde erfolgreich zurückgesetzt.';

if (empty($name) || empty($kennwort) || empty($code)) {
    json_response(400, ['fehler' => 'Der Gruppenname, das Kennwort und der Code sind erforderlich']);
}

$kennwortHash = kennwort_hash($kennwort);

$stmt = $db->prepare("SELECT * from spstat_gruppen WHERE name = ?");
$stmt->execute([$name]);
$gruppe = $stmt->fetch();

if (empty($gruppe)) {
    json_response(404, ['fehler' => $errorMsg]);
}

$id = $gruppe['id'];
$email = $gruppe['email'];
$name = $gruppe['name'];

$stmt = $db->prepare("SELECT * FROM spstat_passwort_resets WHERE gruppe_id=?");
$stmt->execute([$id]);
$tries = $stmt->fetch();


if (!$tries || $tries['versuche'] >= NUMBER_OF_TRIES) {
    json_response(400, ['fehler' => $errorMsg]);
} else {
    if (time() > strtotime($tries['ablauf'])) {        
        json_response(400, ['fehler' => $errorMsg]);
    } else {
        if (  hash_equals($tries['code_hash'], hash('sha256', $code)) ) {
            // alles gut, wir können das Kennwort zurücksetzen
            $stmt = $db->prepare("UPDATE spstat_gruppen SET kennwort_hash=? WHERE id=?");
            $stmt->execute([$kennwortHash, $id]);

            // Löschen des Passwort-Reset-Eintrags
            $stmt = $db->prepare("DELETE FROM spstat_passwort_resets WHERE gruppe_id=?");
            $stmt->execute([$id]);
        } else {
            // versuche hochzählen
            $stmt = $db->prepare("UPDATE spstat_passwort_resets SET versuche= versuche + 1 WHERE gruppe_id=?");
            $stmt->execute([$id]);

            json_response(400, ['fehler' => $errorMsg]);
        }
    }
}
 

json_response(200, [
    'nachricht' => $resultMsg, 
]);
?>
