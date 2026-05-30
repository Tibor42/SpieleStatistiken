<?php
require_once 'config.php';

$db = getDB();
$parts = explode('/', trim($request, '/'));
$spielerId = $parts[1] ?? null;

match($method) {
    'GET' => alleSppielerAbrufen($db, $request),
    'POST' => spielerErstellen($db, $body),
    'PUT' => spielerAktualisieren($db, $spielerId, $body),
    'DELETE' => spielerLoeschen($db, $spielerId),
    default => json_response(405, ['fehler' => 'Methode nicht erlaubt'])
};

function alleSppielerAbrufen(PDO $db, string $request): void {
    $gruppenId = $_GET['gruppen_id'] ?? null;
    if (!$gruppenId) {
        json_response(400, ['fehler' => 'gruppen_id fehlt']);
        return;
    }
    $stmt = $db->prepare("SELECT * FROM spstat_spieler WHERE gruppen_id = ? ORDER BY vorname ASC");
    $stmt->execute([$gruppenId]);
    json_response(200, $stmt->fetchAll());
}

function spielerErstellen(PDO $db, array $body): void {
    $gruppenId = $body['gruppen_id'] ?? null;
    $vorname = trim($body['vorname'] ?? '');
    $nachname = trim($body['nachname'] ?? '');

    if (!$gruppenId || empty($vorname)) {
        json_response(400, ['fehler' => 'gruppen_id und vorname sind erforderlich']);
        return;
    }

    $stmt = $db->prepare("INSERT INTO spstat_spieler (gruppen_id, vorname, nachname) VALUES (?, ?, ?)");
    $stmt->execute([$gruppenId, $vorname, $nachname]);
    $id = $db->lastInsertId();

    json_response(201, ['id' => $id, 'vorname' => $vorname, 'nachname' => $nachname]);
}

function spielerAktualisieren(PDO $db, ?string $spielerId, array $body): void {
    if (!$spielerId) {
        json_response(400, ['fehler' => 'Spieler-ID fehlt']);
        return;
    }

    $vorname = trim($body['vorname'] ?? '');
    $nachname = trim($body['nachname'] ?? '');

    if (empty($vorname)) {
        json_response(400, ['fehler' => 'vorname ist erforderlich']);
        return;
    }

    $stmt = $db->prepare("UPDATE spstat_spieler SET vorname = ?, nachname = ? WHERE id = ?");
    $stmt->execute([$vorname, $nachname, $spielerId]);

    json_response(200, ['nachricht' => 'Spieler aktualisiert']);
}

function spielerLoeschen(PDO $db, ?string $spielerId): void {
    if (!$spielerId) {
        json_response(400, ['fehler' => 'Spieler-ID fehlt']);
        return;
    }

    try {
        $stmt = $db->prepare("DELETE FROM spstat_spieler WHERE id = ?");
        $stmt->execute([$spielerId]);
        json_response(200, ['nachricht' => 'Spieler gelöscht']);
    } catch (PDOException $e) {
        json_response(409, ['fehler' => 'Spieler kann nicht gelöscht werden – noch Events vorhanden']);
    }
}
?>