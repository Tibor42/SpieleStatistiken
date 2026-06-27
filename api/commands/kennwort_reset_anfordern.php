<?php

require_once __DIR__ . '/../mail_helper.php';

define('NUMBER_OF_TRIES', 5);
define('ABLAUF_ZEIT', 60*15);

$name = trim($body['name'] ?? '');
$blnSendMail = false;


if (empty($name)) {
    json_response(400, ['fehler' => 'Der Gruppenname ist erforderlich']);
}

// $kennwortHash = password_hash($kennwort, PASSWORD_BCRYPT);

$resultMsg = "Falls die Gruppe existiert und eine Email-Adresse hinterlegt ist, wurde ein Reset-Code verschickt.";

$stmt = $db->prepare("SELECT * from spstat_gruppen WHERE name = ?");
$stmt->execute([$name]);
$gruppe = $stmt->fetch();

if (!$gruppe || !$gruppe['email']) {
    json_response(200, [
        'nachricht' => $resultMsg 
    ]);
}

$id = $gruppe['id'];
$email = $gruppe['email'];

$stmt = $db->prepare("SELECT * FROM spstat_passwort_resets WHERE gruppe_id=?");
$stmt->execute([$id]);
$tries = $stmt->fetch();

$ablauf = date("Y-m-d H:i:s", time()+ABLAUF_ZEIT);
$code =  random_chars(10);
$code_hash = hash('sha256', $code);

if (!$tries) {
    $insert = $db->prepare("INSERT into spstat_passwort_resets (gruppe_id, code_hash, ablauf) VALUES (?,?,?)");
    $insert->execute([$id, $code_hash, $ablauf]);
    $blnSendMail = true; 
} else {
    if (time() > strtotime($tries['ablauf'])) {
        $update = $db->prepare("UPDATE spstat_passwort_resets SET code_hash=?, ablauf=?, versuche=0 WHERE gruppe_id=?"); 
        $update->execute([$code_hash, $ablauf, $id]);
        $blnSendMail = true;
    }
}
 
if ($blnSendMail) {
    $mailText = "Hallo,\n\nes wurde ein Kennwort-Reset für die Gruppe '$name' angefordert. Hier ist dein Reset-Code:\n\n$code\n\nDieser Code ist 15 Minuten gültig.\n\nWenn du diesen Reset nicht angefordert hast, kannst du diese Nachricht ignorieren.";
    

    mail($email, "Kennwort-Reset für Gruppe '$name'", $mailText);
}
json_response(200, [
    'nachricht' => $resultMsg
]);
?>
