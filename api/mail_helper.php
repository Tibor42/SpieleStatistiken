<?php
// =====================================================================
// mail_helper.php - E-Mail-Versand fuer den Passwort-Reset
//
// WICHTIG auf Shared Hosting: die PHP-Funktion mail() landet fast immer
// im Spam oder wird gar nicht zugestellt. Daher Versand ueber SMTP mit
// PHPMailer und einem echten Postfach deines Hosters (z.B. noreply@thurnay.de).
//
// PHPMailer beschaffen:
//   - Wenn Composer verfuegbar:  composer require phpmailer/phpmailer
//   - Sonst manuell: die 3 Dateien PHPMailer.php, SMTP.php, Exception.php
//     aus dem src/-Ordner des Projekts in ein Unterverzeichnis PHPMailer/ legen.
// =====================================================================

require_once __DIR__ . '/PHPMailer/PHPMailer.php';
require_once __DIR__ . '/PHPMailer/SMTP.php';
require_once __DIR__ . '/PHPMailer/Exception.php';

require_once __DIR__ . '/config.php'; // Für EMAIL_PASSWORD
//  require_once 'config.php';
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

/**
 * Versendet den Reset-Code an die Kontakt-E-Mail der Gruppe.
 * Gibt true bei Erfolg, false bei Fehler zurueck (der Aufrufer
 * antwortet trotzdem neutral, um keine Infos preiszugeben).
 *
 * Die SMTP-Zugangsdaten gehoeren idealerweise NICHT hierher, sondern
 * in eine separate config-Datei ausserhalb des Web-Roots.
 */
function sende_reset_mail(string $an_email, string $gruppen_name, string $code): bool
{
    $mail = new PHPMailer(true);
    try {
        $mail->isSMTP();
        $mail->Host       = 'sslout.df.eu';     // -> SMTP-Host deines Hosters
        $mail->SMTPAuth   = true;
        $mail->Username   = 'tibor@thurnay.de';  // -> Postfach
        $mail->Password   = EMAIL_PASSWORD;       // -> aus Config laden, nicht hart codieren
        $mail->SMTPSecure = PHPMailer::ENCRYPTION_SMTPS; // 465; alternativ STARTTLS auf 587
        $mail->Port       = 465;
        $mail->CharSet    = 'UTF-8';

        $mail->setFrom('tibor@thurnay.de', 'Spiele-Statistiken');
        $mail->addAddress($an_email);

        $mail->Subject = 'Passwort zuruecksetzen';
        $mail->Body =
        "Hallo,\n\n" .
        "für die Gruppe \"{$gruppen_name}\" wurde das Zurücksetzen des " .
        "Passworts angefordert.\n\n" .
        "Dein Code lautet: {$code}\n\n" .
        "Der Code ist 15 Minuten gültig. Falls du das nicht angefordert hast, " .
        "kannst du diese E-Mail einfach ignorieren - es passiert dann nichts.\n";

        $mail->send();
        return true;
    } catch (Exception $e) {
        // Nur serverseitig protokollieren, nie an den Client zurückgeben.
        error_log('Reset-Mailversand fehlgeschlagen: ' . $mail->ErrorInfo);
        return false;
    }
}
?>
