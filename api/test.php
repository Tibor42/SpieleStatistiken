<?php
require_once 'config.php';

try {
    $db = getDB();
    $result = $db->query("SELECT COUNT(*) as anzahl FROM spstat_gruppen");
    $row = $result->fetch();
    echo "Verbindung erfolgreich! Gruppen in DB: " . $row['anzahl'];
} catch (Exception $e) {
    echo "Fehler: " . $e->getMessage();
}
?>