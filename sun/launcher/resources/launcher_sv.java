package sun.launcher.resources;

import java.util.ListResourceBundle;

public final class launcher_sv
  extends ListResourceBundle
{
  public launcher_sv() {}
  
  protected final Object[][] getContents()
  {
    return new Object[][] { { "java.launcher.X.macosx.usage", "\nFöljande alternativ är specifika för Mac OS X:\n    -XstartOnFirstThread\n                      kör huvudmetoden() på den första (AppKit) tråden\n    -Xdock:name=<application name>\"\n                      åsidosatt standardapplikationsnamn visas i docka\n    -Xdock:icon=<path to icon file>\n                      åsidosatt standardikon visas i docka\n\n" }, { "java.launcher.X.usage", "    -Xmixed           exekvering i blandat läge (standard)\n    -Xint             endast exekvering i tolkat läge\n    -Xbootclasspath:<kataloger och zip-/jar-filer avgränsas med {0}>\n                      ange sökväg för programladdningsklasser och -resurser\n    -Xbootclasspath/a:<kataloger och zip-/jar-filer avgränsas med {0}>\n                      lägg till i slutet av programladdningsklassens sökväg\n    -Xbootclasspath/p:<kataloger och zip-/jar-filer avgränsas med {0}>\n                      lägg till i början av programladdningsklassens sökväg\n    -Xdiag            visa ytterligare diagnostiska meddelanden\n    -Xnoclassgc       avaktivera klassens skräpinsamling\n    -Xincgc           aktivera inkrementell skräpinsamling\n    -Xloggc:<fil>    logga GC-status till en fil med tidsstämplar\n    -Xbatch           avaktivera bakgrundskompilering\n    -Xms<storlek>        ange ursprunglig storlek för Java-heap\n    -Xmx<storlek>        ange maximal storlek för Java-heap\n    -Xss<storlek>        ange storlek för java-trådsstack\n    -Xprof            utdata för processorprofilering\n    -Xfuture          aktivera strängaste kontroller, förväntad framtida standard\n    -Xrs              minska OS-signalanvändning av Java/VM (se dokumentation)\n    -Xcheck:jni       utför ytterligare kontroller för JNI-funktioner\n    -Xshare:off       använd inte delade klassdata\n    -Xshare:auto      använd delade klassdata om det går (standard)\n    -Xshare:on        kräv att delade klassdata används, annars slutför inte.\n    -XshowSettings    visa alla inställningar och fortsätt\n    -XshowSettings:all\n                      visa alla inställningar och fortsätt\n    -XshowSettings:vm visa alla vm-relaterade inställningar och fortsätt\n    -XshowSettings:properties\n                      visa alla egenskapsinställningar och fortsätt\n    -XshowSettings:locale\n                      visa alla språkrelaterade inställningar och fortsätt\n\n-X-alternativen är inte standard och kan ändras utan föregående meddelande.\n" }, { "java.launcher.cls.error1", "Fel: Kan inte hitta eller kan inte ladda huvudklassen {0}" }, { "java.launcher.cls.error2", "Fel: Huvudmetoden är inte {0} i klassen {1}, definiera huvudmetoden som:\n   public static void main(String[] args)" }, { "java.launcher.cls.error3", "Fel: Huvudmetoden måste returnera ett värde av typen void i klassen {0}, \ndefiniera huvudmetoden som:\n   public static void main(String[] args)" }, { "java.launcher.cls.error4", "Fel: Huvudmetoden finns inte i klassen {0}, definiera huvudmetoden som:\n   public static void main(String[] args)\neller så måste en JavaFX-applikationsklass utöka {1}" }, { "java.launcher.cls.error5", "Fel: JavaFX-exekveringskomponenter saknas, och de krävs för att kunna köra den här applikationen" }, { "java.launcher.ergo.message1", "                  Standard-VM är {0}" }, { "java.launcher.ergo.message2", "                  eftersom du kör en serverklassmaskin.\n" }, { "java.launcher.init.error", "initieringsfel" }, { "java.launcher.jar.error1", "Fel: Ett oväntat fel inträffade när filen {0} skulle öppnas" }, { "java.launcher.jar.error2", "manifest finns inte i {0}" }, { "java.launcher.jar.error3", "inget huvudmanifestattribut i {0}" }, { "java.launcher.javafx.error1", "Fel: JavaFX launchApplication-metoden har fel signatur, den \nmåste ha deklarerats som statisk och returnera ett värde av typen void" }, { "java.launcher.opt.datamodel", "    -d{0}\t  använd en {0}-bitsdatamodell om det finns\n" }, { "java.launcher.opt.footer", "    -cp <class search path of directories and zip/jar files>\n    -classpath <class search path of directories and zip/jar files>\n                  En lista över kataloger, JAR-arkiv och och ZIP-arkiv\n                  för sökning efter klassfiler avgränsad med {0}.\n    -D<name>=<value>\n                  ange en systemegenskap\n    -verbose:[class|gc|jni]\n                  aktivera utförliga utdata\n    -version      skriv ut produktversion och avsluta\n    -version:<value>\n                  Varning: den här funktionen är inaktuell och kommer\n                  att tas bort i en framtida utgåva.\n                  kräv den angivna versionen för att köra\n    -showversion  skriv ut produktversion och fortsätt\n    -jre-restrict-search | -no-jre-restrict-search\n                  Varning: den här funktionen är inaktuell och kommer\n                  att tas bort i en framtida utgåva.\n                  inkludera/exkludera användarprivata JRE:er i versionssökningen\n    -? -help      skriv ut det här hjälpmeddelandet\n    -X            skriv ut hjälp för icke-standardalternativ\n    -ea[:<packagename>...|:<classname>]\n    -enableassertions[:<packagename>...|:<classname>]\n                  aktivera verifieringar med den angivna detaljgraden\n    -da[:<packagename>...|:<classname>]\n    -disableassertions[:<packagename>...|:<classname>]\n                  avaktivera verifieringar med den angivna detaljgraden\n    -esa | -enablesystemassertions\n                  aktivera systemverifieringar\n    -dsa | -disablesystemassertions\n                  avaktivera systemverifieringar\n    -agentlib:<libname>[=<options>]\n                  ladda det ursprungliga agentbiblioteket <libname>, t.ex. -agentlib:hprof\n                  se även -agentlib:jdwp=help och -agentlib:hprof=help\n    -agentpath:<pathname>[=<options>]\n                  ladda det ursprungliga agentbiblioteket med det fullständiga sökvägsnamnet\n    -javaagent:<jarpath>[=<options>]\n                  ladda agenten för programmeringsspråket Java, se java.lang.instrument\n    -splash:<imagepath>\n                  visa välkomstskärmen med den angivna bilden\nMer information finns på http://www.oracle.com/technetwork/java/javase/documentation/index.html." }, { "java.launcher.opt.header", "Syntax: {0} [-alternativ] class [argument...]\n           (för att köra en klass)\n   eller  {0} [-alternativ] -jar jarfile [argument...]\n           (för att köra en jar-fil)\ndär alternativen omfattar:\n" }, { "java.launcher.opt.hotspot", "    {0}\t  är en synonym för \"{1}\" VM  [inaktuell]\n" }, { "java.launcher.opt.vmselect", "    {0}\t  för att välja \"{1}\" VM\n" } };
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\launcher\resources\launcher_sv.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */