package jdk.internal.org.xml.sax.helpers;

import java.io.IOException;
import jdk.internal.org.xml.sax.Attributes;
import jdk.internal.org.xml.sax.ContentHandler;
import jdk.internal.org.xml.sax.DTDHandler;
import jdk.internal.org.xml.sax.EntityResolver;
import jdk.internal.org.xml.sax.ErrorHandler;
import jdk.internal.org.xml.sax.InputSource;
import jdk.internal.org.xml.sax.Locator;
import jdk.internal.org.xml.sax.SAXException;
import jdk.internal.org.xml.sax.SAXParseException;

public class DefaultHandler
  implements EntityResolver, DTDHandler, ContentHandler, ErrorHandler
{
  public DefaultHandler() {}
  
  public InputSource resolveEntity(String paramString1, String paramString2)
    throws IOException, SAXException
  {
    return null;
  }
  
  public void notationDecl(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {}
  
  public void unparsedEntityDecl(String paramString1, String paramString2, String paramString3, String paramString4)
    throws SAXException
  {}
  
  public void setDocumentLocator(Locator paramLocator) {}
  
  public void startDocument()
    throws SAXException
  {}
  
  public void endDocument()
    throws SAXException
  {}
  
  public void startPrefixMapping(String paramString1, String paramString2)
    throws SAXException
  {}
  
  public void endPrefixMapping(String paramString)
    throws SAXException
  {}
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {}
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {}
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {}
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {}
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {}
  
  public void skippedEntity(String paramString)
    throws SAXException
  {}
  
  public void warning(SAXParseException paramSAXParseException)
    throws SAXException
  {}
  
  public void error(SAXParseException paramSAXParseException)
    throws SAXException
  {}
  
  public void fatalError(SAXParseException paramSAXParseException)
    throws SAXException
  {
    throw paramSAXParseException;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\xml\sax\helpers\DefaultHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */