package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.dtd.DTDGrammar;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDLoader;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaLoader;
import com.sun.org.apache.xerces.internal.impl.xs.XSMessageFormatter;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLGrammarPoolImpl;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.IOException;

public class XMLGrammarCachingConfiguration
  extends XIncludeAwareParserConfiguration
{
  public static final int BIG_PRIME = 2039;
  protected static final SynchronizedSymbolTable fStaticSymbolTable = new SynchronizedSymbolTable(2039);
  protected static final XMLGrammarPoolImpl fStaticGrammarPool = new XMLGrammarPoolImpl();
  protected static final String SCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
  protected XMLSchemaLoader fSchemaLoader = new XMLSchemaLoader(fSymbolTable);
  protected XMLDTDLoader fDTDLoader;
  
  public XMLGrammarCachingConfiguration()
  {
    this(fStaticSymbolTable, fStaticGrammarPool, null);
  }
  
  public XMLGrammarCachingConfiguration(SymbolTable paramSymbolTable)
  {
    this(paramSymbolTable, fStaticGrammarPool, null);
  }
  
  public XMLGrammarCachingConfiguration(SymbolTable paramSymbolTable, XMLGrammarPool paramXMLGrammarPool)
  {
    this(paramSymbolTable, paramXMLGrammarPool, null);
  }
  
  public XMLGrammarCachingConfiguration(SymbolTable paramSymbolTable, XMLGrammarPool paramXMLGrammarPool, XMLComponentManager paramXMLComponentManager)
  {
    super(paramSymbolTable, paramXMLGrammarPool, paramXMLComponentManager);
    fSchemaLoader.setProperty("http://apache.org/xml/properties/internal/grammar-pool", fGrammarPool);
    fDTDLoader = new XMLDTDLoader(fSymbolTable, fGrammarPool);
  }
  
  public void lockGrammarPool()
  {
    fGrammarPool.lockPool();
  }
  
  public void clearGrammarPool()
  {
    fGrammarPool.clear();
  }
  
  public void unlockGrammarPool()
  {
    fGrammarPool.unlockPool();
  }
  
  public Grammar parseGrammar(String paramString1, String paramString2)
    throws XNIException, IOException
  {
    XMLInputSource localXMLInputSource = new XMLInputSource(null, paramString2, null);
    return parseGrammar(paramString1, localXMLInputSource);
  }
  
  public Grammar parseGrammar(String paramString, XMLInputSource paramXMLInputSource)
    throws XNIException, IOException
  {
    if (paramString.equals("http://www.w3.org/2001/XMLSchema")) {
      return parseXMLSchema(paramXMLInputSource);
    }
    if (paramString.equals("http://www.w3.org/TR/REC-xml")) {
      return parseDTD(paramXMLInputSource);
    }
    return null;
  }
  
  SchemaGrammar parseXMLSchema(XMLInputSource paramXMLInputSource)
    throws IOException
  {
    XMLEntityResolver localXMLEntityResolver = getEntityResolver();
    if (localXMLEntityResolver != null) {
      fSchemaLoader.setEntityResolver(localXMLEntityResolver);
    }
    if (fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null) {
      fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", new XSMessageFormatter());
    }
    fSchemaLoader.setProperty("http://apache.org/xml/properties/internal/error-reporter", fErrorReporter);
    String str1 = "http://apache.org/xml/properties/";
    String str2 = str1 + "schema/external-schemaLocation";
    fSchemaLoader.setProperty(str2, getProperty(str2));
    str2 = str1 + "schema/external-noNamespaceSchemaLocation";
    fSchemaLoader.setProperty(str2, getProperty(str2));
    str2 = "http://java.sun.com/xml/jaxp/properties/schemaSource";
    fSchemaLoader.setProperty(str2, getProperty(str2));
    fSchemaLoader.setFeature("http://apache.org/xml/features/validation/schema-full-checking", getFeature("http://apache.org/xml/features/validation/schema-full-checking"));
    SchemaGrammar localSchemaGrammar = (SchemaGrammar)fSchemaLoader.loadGrammar(paramXMLInputSource);
    if (localSchemaGrammar != null) {
      fGrammarPool.cacheGrammars("http://www.w3.org/2001/XMLSchema", new Grammar[] { localSchemaGrammar });
    }
    return localSchemaGrammar;
  }
  
  DTDGrammar parseDTD(XMLInputSource paramXMLInputSource)
    throws IOException
  {
    XMLEntityResolver localXMLEntityResolver = getEntityResolver();
    if (localXMLEntityResolver != null) {
      fDTDLoader.setEntityResolver(localXMLEntityResolver);
    }
    fDTDLoader.setProperty("http://apache.org/xml/properties/internal/error-reporter", fErrorReporter);
    DTDGrammar localDTDGrammar = (DTDGrammar)fDTDLoader.loadGrammar(paramXMLInputSource);
    if (localDTDGrammar != null) {
      fGrammarPool.cacheGrammars("http://www.w3.org/TR/REC-xml", new Grammar[] { localDTDGrammar });
    }
    return localDTDGrammar;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\parsers\XMLGrammarCachingConfiguration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */