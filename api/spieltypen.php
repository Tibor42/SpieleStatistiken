<?php
require_once 'config.php';

$db = getDB();
$parts = explode('/', trim($request, '/'));
$spielTypId = $parts[1] ?? null;

match($method) {
    'GET' => alleSpielTypenAbrufen($db),
    'POST' => spielTypErstellen($db, $body),
    'PUT' => spielTypAktualisieren($db, $spielTypId, $body),
    'DELETE' => spielTypLoeschen($db, $spielTypId),
    default => json_response(405, ['fehler' => 'Methode nicht erlaubt'])
};

function alleSpielTypenAbrufen(PDO $db): void {
    $gruppenId = $_GET['gruppen_id'] ?? null;
    if (!$gruppenId) {
        json_response(400, ['fehler' => 'gruppen_id fehlt']);
        return;
    }
    $stmt = $db->prepare("SELECT * FROM spstat_spiel_typ WHERE gruppen_id = ? ORDER BY name ASC");
    $stmt->execute([$gruppenId]);
    json_response(200, $stmt->fetchAll());
}

function spielTypErstellen(PDO $db, array $body): void {
    $gruppenId = $body['gruppen_id'] ?? null;
    $name = trim($body['name'] ?? '');
    $gewinnmodus = $body['gewinnmodus'] ?? 'wenigste';
    $rundenRelevant = $body['runden_relevant'] ?? 1;

    if (!$gruppenId || empty($name)) {
        json_response(400, ['fehler' => 'gruppen_id und name sind erforderlich']);
        return;
    }

    if (!in_array($gewinnmodus, ['wenigste', 'meiste'])) {
        json_response(400, ['fehler' => 'gewinnmodus muss "wenigste" oder "meiste" sein']);
        return;
    }

    $stmt = $db->prepare("INSERT INTO spstat_spiel_typ (gruppen_id, name, gewinnmodus, runden_relevant) VALUES (?, ?, ?, ?)");
    $stmt->execute([$gruppenId, $name, $gewinnmodus, $rundenRelevant]);
    $id = $db->lastInsertId();

    json_response(201, [
        'id' => $id,
        'name' => $name,
        'gewinnmodus' => $gewinnmodus,
        'runden_relevant' => $rundenRelevant
    ]);
}

function spielTypAktualisieren(PDO $db, ?string $spielTypId, array $body): void {
    if (!$spielTypId) {
        json_response(400, ['fehler' => 'SpielTyp-ID fehlt']);
        return;
    }

    $name = trim($body['name'] ?? '');
    $gewinnmodus = $body['gewinnmodus'] ?? 'wenigste';
    $rundenRelevant = $body['runden_relevant'] ?? 1;

    if (empty($name)) {
        json_response(400, ['fehler' => 'name ist erforderlich']);
        return;
    }

    $stmt = $db->prepare("UPDATE spstat_spiel_typ SET name = ?, gewinnmodus = ?, runden_relevant = ? WHERE id = ?");
    $stmt->execute([$name, $gewinnmodus, $rundenRelevant, $spielTypId]);

    json_response(200, ['nachricht' => 'Spiel-Typ aktualisiert']);
}

function spielTypLoeschen(PDO $db, ?string $spielTypId): void {
    if (!$spielTypId) {
        json_response(400, ['fehler' => 'SpielTyp-ID fehlt']);
        return;
    }

    try {
        $stmt = $db->prepare("DELETE FROM spstat_spiel_typ WHERE id = ?");
        $stmt->execute([$spielTypId]);
        json_response(200, ['nachricht' => 'Spiel-Typ gelöscht']);
    } catch (PDOException $e) {
        json_response(409, ['fehler' => 'Spiel-Typ kann nicht gelöscht werden – noch Events vorhanden']);
    }
}
?>