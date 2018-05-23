package com.sun.org.apache.xml.internal.serializer.utils;

import java.util.ListResourceBundle;

public class SerializerMessages_it
  extends ListResourceBundle
{
  public SerializerMessages_it() {}
  
  public Object[][] getContents()
  {
    Object[][] arrayOfObject = { { "BAD_MSGKEY", "La chiave di messaggio ''{0}'' non si trova nella classe messaggio ''{1}''" }, { "BAD_MSGFORMAT", "Formato di messaggio ''{0}'' in classe messaggio ''{1}'' non riuscito." }, { "ER_SERIALIZER_NOT_CONTENTHANDLER", "La classe serializzatore ''{0}'' non implementa org.xml.sax.ContentHandler." }, { "ER_RESOURCE_COULD_NOT_FIND", "Risorsa [ {0} ] non trovata.\n {1}" }, { "ER_RESOURCE_COULD_NOT_LOAD", "Impossibile caricare la risorsa [ {0} ]: {1} \n {2} \t {3}" }, { "ER_BUFFER_SIZE_LESSTHAN_ZERO", "Dimensione buffer <=0" }, { "ER_INVALID_UTF16_SURROGATE", "Rilevato surrogato UTF-16 non valido: {0}?" }, { "ER_OIERROR", "Errore di I/O" }, { "ER_ILLEGAL_ATTRIBUTE_POSITION", "Impossibile aggiungere l''attributo {0} dopo i nodi figlio o prima che sia prodotto un elemento. L''attributo verrà ignorato." }, { "ER_NAMESPACE_PREFIX", "Lo spazio di nomi per il prefisso ''{0}'' non è stato dichiarato." }, { "ER_STRAY_ATTRIBUTE", "Attributo ''{0}'' al di fuori dell''elemento." }, { "ER_STRAY_NAMESPACE", "Dichiarazione dello spazio di nomi ''{0}''=''{1}'' al di fuori dell''elemento." }, { "ER_COULD_NOT_LOAD_RESOURCE", "Impossibile caricare ''{0}'' (verificare CLASSPATH); verranno utilizzati i valori predefiniti" }, { "ER_ILLEGAL_CHARACTER", "Tentativo di eseguire l''output di un carattere di valore integrale {0} non rappresentato nella codifica di output {1} specificata." }, { "ER_COULD_NOT_LOAD_METHOD_PROPERTY", "Impossibile caricare il file delle proprietà ''{0}'' per il metodo di emissione ''{1}'' (verificare CLASSPATH)" }, { "ER_INVALID_PORT", "Numero di porta non valido" }, { "ER_PORT_WHEN_HOST_NULL", "La porta non può essere impostata se l'host è nullo" }, { "ER_HOST_ADDRESS_NOT_WELLFORMED", "Host non è un indirizzo corretto" }, { "ER_SCHEME_NOT_CONFORMANT", "Lo schema non è conforme." }, { "ER_SCHEME_FROM_NULL_STRING", "Impossibile impostare lo schema da una stringa nulla" }, { "ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE", "Il percorso contiene sequenza di escape non valida" }, { "ER_PATH_INVALID_CHAR", "Il percorso contiene un carattere non valido: {0}" }, { "ER_FRAG_INVALID_CHAR", "Il frammento contiene un carattere non valido" }, { "ER_FRAG_WHEN_PATH_NULL", "Il frammento non può essere impostato se il percorso è nullo" }, { "ER_FRAG_FOR_GENERIC_URI", "Il frammento può essere impostato solo per un URI generico" }, { "ER_NO_SCHEME_IN_URI", "Nessuno schema trovato nell'URI" }, { "ER_CANNOT_INIT_URI_EMPTY_PARMS", "Impossibile inizializzare l'URI con i parametri vuoti" }, { "ER_NO_FRAGMENT_STRING_IN_PATH", "Il frammento non può essere specificato sia nel percorso che nel frammento" }, { "ER_NO_QUERY_STRING_IN_PATH", "La stringa di query non può essere specificata nella stringa di percorso e query." }, { "ER_NO_PORT_IF_NO_HOST", "La porta non può essere specificata se l'host non è specificato" }, { "ER_NO_USERINFO_IF_NO_HOST", "Userinfo non può essere specificato se l'host non è specificato" }, { "ER_XML_VERSION_NOT_SUPPORTED", "Avvertenza: la versione del documento di output deve essere ''{0}''. Questa versione di XML non è supportata. La versione del documento di output sarà ''1.0''." }, { "ER_SCHEME_REQUIRED", "Lo schema è obbligatorio." }, { "ER_FACTORY_PROPERTY_MISSING", "L''oggetto Properties passato a SerializerFactory non dispone di una proprietà ''{0}''." }, { "ER_ENCODING_NOT_SUPPORTED", "Avvertenza: la codifica ''{0}'' non è supportata da Java Runtime." } };
    return arrayOfObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\serializer\utils\SerializerMessages_it.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */