package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarLoader;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class XMLGrammarPreparser
{
  private static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
  protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
  protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
  protected static final String GRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
  private static final Map<String, String> KNOWN_LOADERS;
  private static final String[] RECOGNIZED_PROPERTIES = { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/error-handler", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/grammar-pool" };
  protected SymbolTable fSymbolTable;
  protected XMLErrorReporter fErrorReporter;
  protected XMLEntityResolver fEntityResolver;
  protected XMLGrammarPool fGrammarPool;
  protected Locale fLocale;
  private Map<String, XMLGrammarLoader> fLoaders;
  
  public XMLGrammarPreparser()
  {
    this(new SymbolTable());
  }
  
  public XMLGrammarPreparser(SymbolTable paramSymbolTable)
  {
    fSymbolTable = paramSymbolTable;
    fLoaders = new HashMap();
    fErrorReporter = new XMLErrorReporter();
    setLocale(Locale.getDefault());
    fEntityResolver = new XMLEntityManager();
  }
  
  public boolean registerPreparser(String paramString, XMLGrammarLoader paramXMLGrammarLoader)
  {
    if (paramXMLGrammarLoader == null)
    {
      if (KNOWN_LOADERS.containsKey(paramString))
      {
        String str = (String)KNOWN_LOADERS.get(paramString);
        try
        {
          XMLGrammarLoader localXMLGrammarLoader = (XMLGrammarLoader)ObjectFactory.newInstance(str, true);
          fLoaders.put(paramString, localXMLGrammarLoader);
        }
        catch (Exception localException)
        {
          return false;
        }
        return true;
      }
      return false;
    }
    fLoaders.put(paramString, paramXMLGrammarLoader);
    return true;
  }
  
  public Grammar preparseGrammar(String paramString, XMLInputSource paramXMLInputSource)
    throws XNIException, IOException
  {
    if (fLoaders.containsKey(paramString))
    {
      XMLGrammarLoader localXMLGrammarLoader = (XMLGrammarLoader)fLoaders.get(paramString);
      localXMLGrammarLoader.setProperty("http://apache.org/xml/properties/internal/symbol-table", fSymbolTable);
      localXMLGrammarLoader.setProperty("http://apache.org/xml/properties/internal/entity-resolver", fEntityResolver);
      localXMLGrammarLoader.setProperty("http://apache.org/xml/properties/internal/error-reporter", fErrorReporter);
      if (fGrammarPool != null) {
        try
        {
          localXMLGrammarLoader.setProperty("http://apache.org/xml/properties/internal/grammar-pool", fGrammarPool);
        }
        catch (Exception localException) {}
      }
      return localXMLGrammarLoader.loadGrammar(paramXMLInputSource);
    }
    return null;
  }
  
  public void setLocale(Locale paramLocale)
  {
    fLocale = paramLocale;
    fErrorReporter.setLocale(paramLocale);
  }
  
  public Locale getLocale()
  {
    return fLocale;
  }
  
  public void setErrorHandler(XMLErrorHandler paramXMLErrorHandler)
  {
    fErrorReporter.setProperty("http://apache.org/xml/properties/internal/error-handler", paramXMLErrorHandler);
  }
  
  public XMLErrorHandler getErrorHandler()
  {
    return fErrorReporter.getErrorHandler();
  }
  
  public void setEntityResolver(XMLEntityResolver paramXMLEntityResolver)
  {
    fEntityResolver = paramXMLEntityResolver;
  }
  
  public XMLEntityResolver getEntityResolver()
  {
    return fEntityResolver;
  }
  
  public void setGrammarPool(XMLGrammarPool paramXMLGrammarPool)
  {
    fGrammarPool = paramXMLGrammarPool;
  }
  
  public XMLGrammarPool getGrammarPool()
  {
    return fGrammarPool;
  }
  
  public XMLGrammarLoader getLoader(String paramString)
  {
    return (XMLGrammarLoader)fLoaders.get(paramString);
  }
  
  public void setFeature(String paramString, boolean paramBoolean)
  {
    Iterator localIterator = fLoaders.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      try
      {
        XMLGrammarLoader localXMLGrammarLoader = (XMLGrammarLoader)localEntry.getValue();
        localXMLGrammarLoader.setFeature(paramString, paramBoolean);
      }
      catch (Exception localException) {}
    }
    if (paramString.equals("http://apache.org/xml/features/continue-after-fatal-error")) {
      fErrorReporter.setFeature("http://apache.org/xml/features/continue-after-fatal-error", paramBoolean);
    }
  }
  
  public void setProperty(String paramString, Object paramObject)
  {
    Iterator localIterator = fLoaders.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      try
      {
        XMLGrammarLoader localXMLGrammarLoader = (XMLGrammarLoader)localEntry.getValue();
        localXMLGrammarLoader.setProperty(paramString, paramObject);
      }
      catch (Exception localException) {}
    }
  }
  
  public boolean getFeature(String paramString1, String paramString2)
  {
    XMLGrammarLoader localXMLGrammarLoader = (XMLGrammarLoader)fLoaders.get(paramString1);
    return localXMLGrammarLoader.getFeature(paramString2);
  }
  
  public Object getProperty(String paramString1, String paramString2)
  {
    XMLGrammarLoader localXMLGrammarLoader = (XMLGrammarLoader)fLoaders.get(paramString1);
    return localXMLGrammarLoader.getProperty(paramString2);
  }
  
  static
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("http://www.w3.org/2001/XMLSchema", "com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaLoader");
    localHashMap.put("http://www.w3.org/TR/REC-xml", "com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDLoader");
    KNOWN_LOADERS = Collections.unmodifiableMap(localHashMap);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\parsers\XMLGrammarPreparser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */