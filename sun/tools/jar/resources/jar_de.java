package sun.tools.jar.resources;

import java.util.ListResourceBundle;

public final class jar_de
  extends ListResourceBundle
{
  public jar_de() {}
  
  protected final Object[][] getContents()
  {
    return new Object[][] { { "error.bad.cflag", "Kennzeichen \"c\" erfordert Angabe von Manifest oder Eingabedateien." }, { "error.bad.eflag", "Kennzeichen \"e\" und Manifest mit dem Attribut \"Main-Class\" können nicht zusammen angegeben\nwerden." }, { "error.bad.option", "Eine der Optionen -{ctxu} muss angegeben werden." }, { "error.bad.uflag", "Kennzeichen \"u\" erfordert Angabe von Manifest, Kennzeichen \"e\" oder Eingabedateien." }, { "error.cant.open", "Öffnen nicht möglich: {0} " }, { "error.create.dir", "{0}: Verzeichnis konnte nicht erstellt werden" }, { "error.create.tempfile", "Es konnte keine temporäre Datei erstellt werden" }, { "error.illegal.option", "Ungültige Option: {0}" }, { "error.incorrect.length", "Falsche Länge bei der Verarbeitung: {0}" }, { "error.nosuch.fileordir", "{0}: Datei oder Verzeichnis nicht vorhanden" }, { "error.write.file", "Fehler beim Schreiben in vorhandener JAR-Datei" }, { "out.added.manifest", "Manifest wurde hinzugefügt" }, { "out.adding", "{0} wird hinzugefügt" }, { "out.create", "  erstellt: {0}" }, { "out.deflated", "({0} % verkleinert)" }, { "out.extracted", "extrahiert: {0}" }, { "out.ignore.entry", "Eintrag {0} wird ignoriert" }, { "out.inflated", " vergrößert: {0}" }, { "out.size", "(ein = {0}) (aus = {1})" }, { "out.stored", "(0 % gespeichert)" }, { "out.update.manifest", "Manifest wurde aktualisiert" }, { "usage", "Verwendung: jar {ctxui}[vfmn0PMe] [jar-file] [manifest-file] [entry-point] [-C dir] Dateien...\nOptionen:\n    -c  Neues Archiv erstellen\n    -t  Inhaltsverzeichnis für Archiv anzeigen\n    -x  Benannte (oder alle) Dateien aus dem Archiv extrahieren\n    -u  Vorhandenes Archiv aktualisieren\n    -v  Ausgabe im Verbose-Modus aus Standard-Ausgabe generieren\n    -f  Dateinamen für Archiv angeben\n    -m  Manifestinformationen aus angegebener Manifestdatei einschließen\n    -n  Pack200-Normalisierung nach Erstellung eines neuen Archivs ausführen\n    -e  Anwendungseinstiegspunkt für Standalone-Anwendung angeben \n        in einer ausführbaren JAR-Datei gebündelt\n    -0  Nur speichern; keine ZIP-Komprimierung verwenden\n    -P  Komponenten mit vorangestelltem \"/\" (absoluter Pfad) und \"..\" (übergeordnetes Verzeichnis) aus Dateinamen beibehalten\n    -M  Keine Manifest-Datei für die Einträge erstellen\n    -i  Indexinformationen für die angegebenen JAR-Dateien erstellen\n    -C  Zum angegebenen Verzeichnis wechseln und folgende Datei einschließen\nFalls eine Datei ein Verzeichnis ist, wird dieses rekursiv verarbeitet.\nDer Name der Manifestdatei, der Name der Archivdatei und der Name des Einstiegspunkts werden\nin derselben Reihenfolge wie die Kennzeichen \"m\", \"f\" und \"e\" angegeben.\n\nBeispiel 1: Archivieren Sie zwei Klassendateien in ein Archiv mit Namen \"classes.jar\": \n       jar cvf classes.jar Foo.class Bar.class \nBeispiel 2: Verwenden Sie die vorhandenen Manifestdatei \"mymanifest\", und archivieren Sie alle\n           Dateien im Verzeichnis foo/ directory in \"classes.jar\": \n       jar cvfm classes.jar mymanifest -C foo/ .\n" } };
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\tools\jar\resources\jar_de.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */