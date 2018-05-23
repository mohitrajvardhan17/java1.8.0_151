package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import java.io.IOException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public abstract class XMLParser
{
  protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
  protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
  private static final String[] RECOGNIZED_PROPERTIES = { "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/error-handler" };
  protected XMLParserConfiguration fConfiguration;
  XMLSecurityManager securityManager;
  XMLSecurityPropertyManager securityPropertyManager;
  
  public boolean getFeature(String paramString)
    throws SAXNotSupportedException, SAXNotRecognizedException
  {
    return fConfiguration.getFeature(paramString);
  }
  
  protected XMLParser(XMLParserConfiguration paramXMLParserConfiguration)
  {
    fConfiguration = paramXMLParserConfiguration;
    fConfiguration.addRecognizedProperties(RECOGNIZED_PROPERTIES);
  }
  
  public void parse(XMLInputSource paramXMLInputSource)
    throws XNIException, IOException
  {
    if (securityManager == null)
    {
      securityManager = new XMLSecurityManager(true);
      fConfiguration.setProperty("http://apache.org/xml/properties/security-manager", securityManager);
    }
    if (securityPropertyManager == null)
    {
      securityPropertyManager = new XMLSecurityPropertyManager();
      fConfiguration.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", securityPropertyManager);
    }
    reset();
    fConfiguration.parse(paramXMLInputSource);
  }
  
  protected void reset()
    throws XNIException
  {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\parsers\XMLParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */