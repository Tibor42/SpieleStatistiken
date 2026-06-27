<?php
require_once 'config.php';

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

$body = json_decode(file_get_contents('php://input'), true) ?? [];
$cmd = $body['cmd'] ?? '';

if (empty($cmd)) {
    json_response(400, ['fehler' => 'Kein Commando angegeben']);
    exit();
}

// Erlaubte Commands
$commands = [
    'gruppe_erstellen',
    'gruppe_beitreten',
    'spieler_abrufen',
    'spieler_erstellen',
    'spieler_aktualisieren',
    'spieler_loeschen',
    'spieltypen_abrufen',
    'spieltyp_erstellen',
    'spieltyp_aktualisieren',
    'spieltyp_loeschen',
    'events_abrufen',
    'events_erstellen',
    'events_loeschen',
    'gruppen_email_setzen',
];

if (!in_array($cmd, $commands)) {
    json_response(400, ['fehler' => 'Unbekanntes Commando: ' . $cmd]);
    exit();
}

$db = getDB();
$cmdFile = 'commands/' . $cmd . '.php';

if (!file_exists($cmdFile)) {
    json_response(500, ['fehler' => 'Commando nicht implementiert']);
    exit();
}

try {
    require $cmdFile;
} catch (Throwable $e) {
    error_log('Exception: ' . $e->getMessage(),0);
    json_response(500, ['fehler' => 'Ausnahmefehler, wenn er wiederholt auftritt bitte Ruhe bewahren und tief ausatmen.'  ]);
}

function json_response(int $code, array $data): void {
    http_response_code($code);
    echo json_encode($data);
    exit();
}

function random_chars($charNbr)
{
    $chars = array('0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','A','B','C','D','E','F','G', 'H', 'J','K','L', 'M','O', 'P', 'R', 'U', 'S', 'T','W', 'Z');
    $randomchar = '';
    $maxNum = count($chars) -1;
    for($i=0;$i<$charNbr;++$i)
    {
        $randomchar .= $chars[random_int(0,$maxNum)];
    }
    return $randomchar;
}

?>
