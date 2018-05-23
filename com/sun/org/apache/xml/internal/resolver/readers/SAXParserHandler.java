package com.sun.org.apache.xml.internal.resolver.readers;

import java.io.IOException;
import java.io.PrintStream;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SAXParserHandler
  extends DefaultHandler
{
  private EntityResolver er = null;
  private ContentHandler ch = null;
  
  public SAXParserHandler() {}
  
  public void setEntityResolver(EntityResolver paramEntityResolver)
  {
    er = paramEntityResolver;
  }
  
  public void setContentHandler(ContentHandler paramContentHandler)
  {
    ch = paramContentHandler;
  }
  
  public InputSource resolveEntity(String paramString1, String paramString2)
    throws SAXException
  {
    if (er != null) {
      try
      {
        return er.resolveEntity(paramString1, paramString2);
      }
      catch (IOException localIOException)
      {
        System.out.println("resolveEntity threw IOException!");
        return null;
      }
    }
    return null;
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (ch != null) {
      ch.characters(paramArrayOfChar, paramInt1, paramInt2);
    }
  }
  
  public void endDocument()
    throws SAXException
  {
    if (ch != null) {
      ch.endDocument();
    }
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    if (ch != null) {
      ch.endElement(paramString1, paramString2, paramString3);
    }
  }
  
  public void endPrefixMapping(String paramString)
    throws SAXException
  {
    if (ch != null) {
      ch.endPrefixMapping(paramString);
    }
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (ch != null) {
      ch.ignorableWhitespace(paramArrayOfChar, paramInt1, paramInt2);
    }
  }
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {
    if (ch != null) {
      ch.processingInstruction(paramString1, paramString2);
    }
  }
  
  public void setDocumentLocator(Locator paramLocator)
  {
    if (ch != null) {
      ch.setDocumentLocator(paramLocator);
    }
  }
  
  public void skippedEntity(String paramString)
    throws SAXException
  {
    if (ch != null) {
      ch.skippedEntity(paramString);
    }
  }
  
  public void startDocument()
    throws SAXException
  {
    if (ch != null) {
      ch.startDocument();
    }
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    if (ch != null) {
      ch.startElement(paramString1, paramString2, paramString3, paramAttributes);
    }
  }
  
  public void startPrefixMapping(String paramString1, String paramString2)
    throws SAXException
  {
    if (ch != null) {
      ch.startPrefixMapping(paramString1, paramString2);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\resolver\readers\SAXParserHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */