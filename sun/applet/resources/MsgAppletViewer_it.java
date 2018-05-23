package sun.applet.resources;

import java.util.ListResourceBundle;

public class MsgAppletViewer_it
  extends ListResourceBundle
{
  public MsgAppletViewer_it() {}
  
  public Object[][] getContents()
  {
    Object[][] arrayOfObject = { { "textframe.button.dismiss", "Chiudi" }, { "appletviewer.tool.title", "Visualizzatore applet: {0}" }, { "appletviewer.menu.applet", "Applet" }, { "appletviewer.menuitem.restart", "Riavvia" }, { "appletviewer.menuitem.reload", "Ricarica" }, { "appletviewer.menuitem.stop", "Arresta" }, { "appletviewer.menuitem.save", "Salva..." }, { "appletviewer.menuitem.start", "Avvia" }, { "appletviewer.menuitem.clone", "Copia..." }, { "appletviewer.menuitem.tag", "Tag..." }, { "appletviewer.menuitem.info", "Informazioni..." }, { "appletviewer.menuitem.edit", "Modifica" }, { "appletviewer.menuitem.encoding", "Codifica caratteri" }, { "appletviewer.menuitem.print", "Stampa..." }, { "appletviewer.menuitem.props", "Proprietà..." }, { "appletviewer.menuitem.close", "Chiudi" }, { "appletviewer.menuitem.quit", "Esci" }, { "appletviewer.label.hello", "Benvenuti..." }, { "appletviewer.status.start", "avvio applet in corso..." }, { "appletviewer.appletsave.filedialogtitle", "Serializza applet in file" }, { "appletviewer.appletsave.err1", "serializzazione di {0} in {1}" }, { "appletviewer.appletsave.err2", "in appletSave: {0}" }, { "appletviewer.applettag", "Tag visualizzata" }, { "appletviewer.applettag.textframe", "Applet tag HTML" }, { "appletviewer.appletinfo.applet", "-- nessuna informazione sull'applet --" }, { "appletviewer.appletinfo.param", "-- nessuna informazione sul parametro --" }, { "appletviewer.appletinfo.textframe", "Informazioni applet" }, { "appletviewer.appletprint.fail", "Stampa non riuscita." }, { "appletviewer.appletprint.finish", "Stampa completata." }, { "appletviewer.appletprint.cancel", "Stampa annullata." }, { "appletviewer.appletencoding", "Codifica caratteri: {0}" }, { "appletviewer.parse.warning.requiresname", "Avvertenza: la tag <param name=... value=...> richiede un attributo name." }, { "appletviewer.parse.warning.paramoutside", "Avvertenza: la tag <param> non rientra in <applet>... </applet>." }, { "appletviewer.parse.warning.applet.requirescode", "Avvertenza: la tag <applet> richiede un attributo code." }, { "appletviewer.parse.warning.applet.requiresheight", "Avvertenza: la tag <applet> richiede un attributo height." }, { "appletviewer.parse.warning.applet.requireswidth", "Avvertenza: la tag <applet> richiede un attributo width." }, { "appletviewer.parse.warning.object.requirescode", "Avvertenza: la tag <object> richiede un attributo code." }, { "appletviewer.parse.warning.object.requiresheight", "Avvertenza: la tag <object> richiede un attributo height." }, { "appletviewer.parse.warning.object.requireswidth", "Avvertenza: la tag <object> richiede un attributo width." }, { "appletviewer.parse.warning.embed.requirescode", "Avvertenza: la tag <embed> richiede un attributo code." }, { "appletviewer.parse.warning.embed.requiresheight", "Avvertenza: la tag <embed> richiede un attributo height." }, { "appletviewer.parse.warning.embed.requireswidth", "Avvertenza: la tag <embed> richiede un attributo width." }, { "appletviewer.parse.warning.appnotLongersupported", "Avvertenza: la tag <app> non è più supportata. Utilizzare <applet>:" }, { "appletviewer.usage", "Uso: appletviewer <opzioni> url(s)\n\ndove <opzioni> includono:\n  -debug                  Avvia il visualizzatore applet nel debugger Java\n  -encoding <codifica>    Specifica la codifica dei caratteri utilizzata dai file HTML\n  -J<flag runtime>        Passa l'argomento all'interpreter Java\n\nL'opzione -J non è standard ed è soggetta a modifica senza preavviso." }, { "appletviewer.main.err.unsupportedopt", "Opzione non supportata: {0}" }, { "appletviewer.main.err.unrecognizedarg", "Argomento non riconosciuto: {0}" }, { "appletviewer.main.err.dupoption", "Uso duplicato dell''opzione: {0}" }, { "appletviewer.main.err.inputfile", "Nessun file di input specificato." }, { "appletviewer.main.err.badurl", "URL non valido: {0} ( {1} )" }, { "appletviewer.main.err.io", "Eccezione I/O durante la lettura di {0}" }, { "appletviewer.main.err.readablefile", "Assicurarsi che {0} sia un file e che sia leggibile." }, { "appletviewer.main.err.correcturl", "{0} è l''URL corretto?" }, { "appletviewer.main.prop.store", "Proprietà specifiche dell'utente per AppletViewer" }, { "appletviewer.main.err.prop.cantread", "Impossibile leggere il file delle proprietà utente: {0}" }, { "appletviewer.main.err.prop.cantsave", "Impossibile salvare il file delle proprietà utente: {0}" }, { "appletviewer.main.warn.nosecmgr", "Avvertenza: la sicurezza verrà disabilitata." }, { "appletviewer.main.debug.cantfinddebug", "Impossibile trovare il debugger." }, { "appletviewer.main.debug.cantfindmain", "Impossibile trovare il metodo principale nel debugger." }, { "appletviewer.main.debug.exceptionindebug", "Eccezione nel debugger." }, { "appletviewer.main.debug.cantaccess", "Impossibile accedere al debugger." }, { "appletviewer.main.nosecmgr", "Avvertenza: SecurityManager non installato." }, { "appletviewer.main.warning", "Avvertenza: nessuna applet avviata. Assicurarsi che l'input contenga una tag <applet>." }, { "appletviewer.main.warn.prop.overwrite", "Avvertenza: la proprietà di sistema verrà sovrascritta temporaneamente su richiesta dell''utente. Chiave {0}, valore precedente {1}, nuovo valore {2}." }, { "appletviewer.main.warn.cantreadprops", "Avvertenza: impossibile leggere il file delle proprietà AppletViewer {0}. Verranno utilizzate le impostazioni predefinite." }, { "appletioexception.loadclass.throw.interrupted", "caricamento della classe interrotto: {0}" }, { "appletioexception.loadclass.throw.notloaded", "classe non caricata: {0}" }, { "appletclassloader.loadcode.verbose", "Apertura del flusso per {0} per recuperare {1}" }, { "appletclassloader.filenotfound", "File non trovato durante la ricerca di {0}" }, { "appletclassloader.fileformat", "Eccezione di formato file durante il caricamento di {0}" }, { "appletclassloader.fileioexception", "Eccezione I/O durante il caricamento di {0}" }, { "appletclassloader.fileexception", "Eccezione {0} durante il caricamento di {1}" }, { "appletclassloader.filedeath", "{0} terminato durante il caricamento di {1}" }, { "appletclassloader.fileerror", "Errore {0} durante il caricamento di {1}" }, { "appletclassloader.findclass.verbose.openstream", "Apertura del flusso per {0} per recuperare {1}" }, { "appletclassloader.getresource.verbose.forname", "AppletClassLoader.getResource per il nome: {0}" }, { "appletclassloader.getresource.verbose.found", "È stata trovata la risorsa {0} come risorsa di sistema" }, { "appletclassloader.getresourceasstream.verbose", "È stata trovata la risorsa {0} come risorsa di sistema" }, { "appletpanel.runloader.err", "Parametro di oggetto o di codice." }, { "appletpanel.runloader.exception", "eccezione durante la deserializzazione di {0}" }, { "appletpanel.destroyed", "Applet rimossa." }, { "appletpanel.loaded", "Applet caricata." }, { "appletpanel.started", "Applet avviata." }, { "appletpanel.inited", "Applet inizializzata." }, { "appletpanel.stopped", "Applet arrestata." }, { "appletpanel.disposed", "Applet eliminata." }, { "appletpanel.nocode", "Nella tag APPLET manca il parametro CODE." }, { "appletpanel.notfound", "caricamento: classe {0} non trovata." }, { "appletpanel.nocreate", "caricamento: impossibile creare un''istanza di {0}." }, { "appletpanel.noconstruct", "caricamento: {0} non è pubblico o non ha un costruttore pubblico." }, { "appletpanel.death", "terminato" }, { "appletpanel.exception", "eccezione: {0}" }, { "appletpanel.exception2", "eccezione: {0}: {1}." }, { "appletpanel.error", "errore: {0}." }, { "appletpanel.error2", "errore: {0}: {1}." }, { "appletpanel.notloaded", "Inizializzazione: applet non caricata." }, { "appletpanel.notinited", "Avvio: applet non inizializzata." }, { "appletpanel.notstarted", "Arresto: applet non avviata." }, { "appletpanel.notstopped", "Rimozione: applet non arrestata." }, { "appletpanel.notdestroyed", "Eliminazione: applet non rimossa." }, { "appletpanel.notdisposed", "Caricamento: applet non eliminata." }, { "appletpanel.bail", "Interrotto: chiusura." }, { "appletpanel.filenotfound", "File non trovato durante la ricerca di {0}" }, { "appletpanel.fileformat", "Eccezione di formato file durante il caricamento di {0}" }, { "appletpanel.fileioexception", "Eccezione I/O durante il caricamento di {0}" }, { "appletpanel.fileexception", "Eccezione {0} durante il caricamento di {1}" }, { "appletpanel.filedeath", "{0} terminato durante il caricamento di {1}" }, { "appletpanel.fileerror", "Errore {0} durante il caricamento di {1}" }, { "appletpanel.badattribute.exception", "Analisi HTML: valore errato per l'attributo width/height" }, { "appletillegalargumentexception.objectinputstream", "AppletObjectInputStream richiede un loader non nullo" }, { "appletprops.title", "Proprietà AppletViewer" }, { "appletprops.label.http.server", "Server proxy http:" }, { "appletprops.label.http.proxy", "Porta proxy http:" }, { "appletprops.label.network", "Accesso alla rete:" }, { "appletprops.choice.network.item.none", "Nessuno" }, { "appletprops.choice.network.item.applethost", "Host applet" }, { "appletprops.choice.network.item.unrestricted", "Non limitato" }, { "appletprops.label.class", "Accesso alla classe:" }, { "appletprops.choice.class.item.restricted", "Limitato" }, { "appletprops.choice.class.item.unrestricted", "Non limitato" }, { "appletprops.label.unsignedapplet", "Consenti applet senza firma:" }, { "appletprops.choice.unsignedapplet.no", "No" }, { "appletprops.choice.unsignedapplet.yes", "Sì" }, { "appletprops.button.apply", "Applica" }, { "appletprops.button.cancel", "Annulla" }, { "appletprops.button.reset", "Reimposta" }, { "appletprops.apply.exception", "Salvataggio delle proprietà non riuscito: {0}" }, { "appletprops.title.invalidproxy", "Voce non valida" }, { "appletprops.label.invalidproxy", "La porta del proxy deve essere un valore intero positivo." }, { "appletprops.button.ok", "OK" }, { "appletprops.prop.store", "Proprietà specifiche dell'utente per AppletViewer" }, { "appletsecurityexception.checkcreateclassloader", "Eccezione di sicurezza: classloader" }, { "appletsecurityexception.checkaccess.thread", "Eccezione di sicurezza: thread" }, { "appletsecurityexception.checkaccess.threadgroup", "Eccezione di sicurezza: threadgroup: {0}" }, { "appletsecurityexception.checkexit", "Eccezione di sicurezza: exit: {0}" }, { "appletsecurityexception.checkexec", "Eccezione di sicurezza: exec: {0}" }, { "appletsecurityexception.checklink", "Eccezione di sicurezza: link: {0}" }, { "appletsecurityexception.checkpropsaccess", "Eccezione di sicurezza: properties" }, { "appletsecurityexception.checkpropsaccess.key", "Eccezione di sicurezza: properties access {0}" }, { "appletsecurityexception.checkread.exception1", "Eccezione di sicurezza: {0}, {1}" }, { "appletsecurityexception.checkread.exception2", "Eccezione di sicurezza: file.read: {0}" }, { "appletsecurityexception.checkread", "Eccezione di sicurezza: file.read: {0} == {1}" }, { "appletsecurityexception.checkwrite.exception", "Eccezione di sicurezza: {0}, {1}" }, { "appletsecurityexception.checkwrite", "Eccezione di sicurezza: file.write: {0} == {1}" }, { "appletsecurityexception.checkread.fd", "Eccezione di sicurezza: fd.read" }, { "appletsecurityexception.checkwrite.fd", "Eccezione di sicurezza: fd.write" }, { "appletsecurityexception.checklisten", "Eccezione di sicurezza: socket.listen: {0}" }, { "appletsecurityexception.checkaccept", "Eccezione di sicurezza: socket.accept: {0}:{1}" }, { "appletsecurityexception.checkconnect.networknone", "Eccezione di sicurezza: socket.connect: {0}->{1}" }, { "appletsecurityexception.checkconnect.networkhost1", "Eccezione di sicurezza: impossibile connettersi a {0} con origine da {1}." }, { "appletsecurityexception.checkconnect.networkhost2", "Eccezione di sicurezza: impossibile risolvere l''IP per l''host {0} o per {1}. " }, { "appletsecurityexception.checkconnect.networkhost3", "Eccezione di sicurezza: impossibile non risolvere l''IP per l''host {0}. Vedere la proprietà trustProxy." }, { "appletsecurityexception.checkconnect", "Eccezione di sicurezza: connect: {0}->{1}" }, { "appletsecurityexception.checkpackageaccess", "Eccezione di sicurezza: impossibile accedere al package {0}" }, { "appletsecurityexception.checkpackagedefinition", "Eccezione di sicurezza: impossibile definire il package {0}" }, { "appletsecurityexception.cannotsetfactory", "Eccezione di sicurezza: impossibile impostare il factory" }, { "appletsecurityexception.checkmemberaccess", "Eccezione di sicurezza: controllare l'accesso dei membri" }, { "appletsecurityexception.checkgetprintjob", "Eccezione di sicurezza: getPrintJob" }, { "appletsecurityexception.checksystemclipboardaccess", "Eccezione di sicurezza: getSystemClipboard" }, { "appletsecurityexception.checkawteventqueueaccess", "Eccezione di sicurezza: getEventQueue" }, { "appletsecurityexception.checksecurityaccess", "Eccezione di sicurezza: operazione di sicurezza {0}" }, { "appletsecurityexception.getsecuritycontext.unknown", "tipo di loader della classe sconosciuto. Impossibile verificare la presenza di getContext." }, { "appletsecurityexception.checkread.unknown", "tipo di loader della classe sconosciuto. Impossibile verificare la presenza della lettura di controllo {0}." }, { "appletsecurityexception.checkconnect.unknown", "tipo di loader della classe sconosciuto. Impossibile verificare la presenza della connessione di controllo." } };
    return arrayOfObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\applet\resources\MsgAppletViewer_it.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */