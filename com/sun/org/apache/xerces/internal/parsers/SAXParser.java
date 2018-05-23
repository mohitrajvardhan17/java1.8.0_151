package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager.State;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager.State;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class SAXParser
  extends AbstractSAXParser
{
  protected static final String NOTIFY_BUILTIN_REFS = "http://apache.org/xml/features/scanner/notify-builtin-refs";
  protected static final String REPORT_WHITESPACE = "http://java.sun.com/xml/schema/features/report-ignored-element-content-whitespace";
  private static final String[] RECOGNIZED_FEATURES = { "http://apache.org/xml/features/scanner/notify-builtin-refs", "http://java.sun.com/xml/schema/features/report-ignored-element-content-whitespace" };
  protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  protected static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
  private static final String[] RECOGNIZED_PROPERTIES = { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/grammar-pool" };
  
  public SAXParser(XMLParserConfiguration paramXMLParserConfiguration)
  {
    super(paramXMLParserConfiguration);
  }
  
  public SAXParser()
  {
    this(null, null);
  }
  
  public SAXParser(SymbolTable paramSymbolTable)
  {
    this(paramSymbolTable, null);
  }
  
  public SAXParser(SymbolTable paramSymbolTable, XMLGrammarPool paramXMLGrammarPool)
  {
    super(new XIncludeAwareParserConfiguration());
    fConfiguration.addRecognizedFeatures(RECOGNIZED_FEATURES);
    fConfiguration.setFeature("http://apache.org/xml/features/scanner/notify-builtin-refs", true);
    fConfiguration.addRecognizedProperties(RECOGNIZED_PROPERTIES);
    if (paramSymbolTable != null) {
      fConfiguration.setProperty("http://apache.org/xml/properties/internal/symbol-table", paramSymbolTable);
    }
    if (paramXMLGrammarPool != null) {
      fConfiguration.setProperty("http://apache.org/xml/properties/internal/grammar-pool", paramXMLGrammarPool);
    }
  }
  
  public void setProperty(String paramString, Object paramObject)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    if (paramString.equals("http://apache.org/xml/properties/security-manager"))
    {
      securityManager = XMLSecurityManager.convert(paramObject, securityManager);
      super.setProperty("http://apache.org/xml/properties/security-manager", securityManager);
      return;
    }
    if (paramString.equals("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager"))
    {
      if (paramObject == null) {
        securityPropertyManager = new XMLSecurityPropertyManager();
      } else {
        securityPropertyManager = ((XMLSecurityPropertyManager)paramObject);
      }
      super.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", securityPropertyManager);
      return;
    }
    if (securityManager == null)
    {
      securityManager = new XMLSecurityManager(true);
      super.setProperty("http://apache.org/xml/properties/security-manager", securityManager);
    }
    if (securityPropertyManager == null)
    {
      securityPropertyManager = new XMLSecurityPropertyManager();
      super.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", securityPropertyManager);
    }
    int i = securityPropertyManager.getIndex(paramString);
    if (i > -1) {
      securityPropertyManager.setValue(i, XMLSecurityPropertyManager.State.APIPROPERTY, (String)paramObject);
    } else if (!securityManager.setLimit(paramString, XMLSecurityManager.State.APIPROPERTY, paramObject)) {
      super.setProperty(paramString, paramObject);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\parsers\SAXParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */