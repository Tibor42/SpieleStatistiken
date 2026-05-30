<?php
require_once 'config.php';

$db = getDB();
$parts = explode('/', trim($request, '/'));
$gruppenId = $parts[1] ?? null;

match($method) {
    'POST' => gruppeErstellen($db, $body),
    'GET' => $gruppenId ? gruppeAbrufen($db, $gruppenId) : json_response(400, ['fehler' => 'Gruppen-ID fehlt']),
    default => json_response(405, ['fehler' => 'Methode nicht erlaubt'])
};

function gruppeErstellen(PDO $db, array $body): void {
    $name = trim($body['name'] ?? '');
    $kennwort = trim($body['kennwort'] ?? '');

    if (empty($name) || empty($kennwort)) {
        json_response(400, ['fehler' => 'Name und Kennwort sind erforderlich']);
        return;
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
}

function gruppeAbrufen(PDO $db, string $gruppenId): void {
    $stmt = $db->prepare("SELECT id, name FROM spstat_gruppen WHERE id = ?");
    $stmt->execute([$gruppenId]);
    $gruppe = $stmt->fetch();

    if (!$gruppe) {
        json_response(404, ['fehler' => 'Gruppe nicht gefunden']);
        return;
    }

    json_response(200, $gruppe);
}

function gruppenBeitreten(PDO $db, array $body): void {
    $name = trim($body['name'] ?? '');
    $kennwort = trim($body['kennwort'] ?? '');

    if (empty($name) || empty($kennwort)) {
        json_response(400, ['fehler' => 'Name und Kennwort sind erforderlich']);
        return;
    }

    $stmt = $db->prepare("SELECT * FROM spstat_gruppen WHERE name = ?");
    $stmt->execute([$name]);
    $gruppe = $stmt->fetch();

    if (!$gruppe || !password_verify($kennwort, $gruppe['kennwort_hash'])) {
        json_response(401, ['fehler' => 'Name oder Kennwort falsch']);
        return;
    }

    json_response(200, [
        'id' => $gruppe['id'],
        'name' => $gruppe['name'],
        'nachricht' => 'Erfolgreich beigetreten'
    ]);
}
?>