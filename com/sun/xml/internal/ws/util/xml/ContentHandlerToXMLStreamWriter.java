package com.sun.xml.internal.ws.util.xml;

import java.util.Stack;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ContentHandlerToXMLStreamWriter
  extends DefaultHandler
{
  private final XMLStreamWriter staxWriter;
  private final Stack prefixBindings;
  
  public ContentHandlerToXMLStreamWriter(XMLStreamWriter paramXMLStreamWriter)
  {
    staxWriter = paramXMLStreamWriter;
    prefixBindings = new Stack();
  }
  
  public void endDocument()
    throws SAXException
  {
    try
    {
      staxWriter.writeEndDocument();
      staxWriter.flush();
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new SAXException(localXMLStreamException);
    }
  }
  
  public void startDocument()
    throws SAXException
  {
    try
    {
      staxWriter.writeStartDocument();
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new SAXException(localXMLStreamException);
    }
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    try
    {
      staxWriter.writeCharacters(paramArrayOfChar, paramInt1, paramInt2);
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new SAXException(localXMLStreamException);
    }
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    characters(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void endPrefixMapping(String paramString)
    throws SAXException
  {}
  
  public void skippedEntity(String paramString)
    throws SAXException
  {
    try
    {
      staxWriter.writeEntityRef(paramString);
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new SAXException(localXMLStreamException);
    }
  }
  
  public void setDocumentLocator(Locator paramLocator) {}
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {
    try
    {
      staxWriter.writeProcessingInstruction(paramString1, paramString2);
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new SAXException(localXMLStreamException);
    }
  }
  
  public void startPrefixMapping(String paramString1, String paramString2)
    throws SAXException
  {
    if (paramString1 == null) {
      paramString1 = "";
    }
    if (paramString1.equals("xml")) {
      return;
    }
    prefixBindings.add(paramString1);
    prefixBindings.add(paramString2);
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    try
    {
      staxWriter.writeEndElement();
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new SAXException(localXMLStreamException);
    }
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    try
    {
      staxWriter.writeStartElement(getPrefix(paramString3), paramString2, paramString1);
      while (prefixBindings.size() != 0)
      {
        String str1 = (String)prefixBindings.pop();
        String str2 = (String)prefixBindings.pop();
        if (str2.length() == 0) {
          staxWriter.setDefaultNamespace(str1);
        } else {
          staxWriter.setPrefix(str2, str1);
        }
        staxWriter.writeNamespace(str2, str1);
      }
      writeAttributes(paramAttributes);
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new SAXException(localXMLStreamException);
    }
  }
  
  private void writeAttributes(Attributes paramAttributes)
    throws XMLStreamException
  {
    for (int i = 0; i < paramAttributes.getLength(); i++)
    {
      String str = getPrefix(paramAttributes.getQName(i));
      if (!str.equals("xmlns")) {
        staxWriter.writeAttribute(str, paramAttributes.getURI(i), paramAttributes.getLocalName(i), paramAttributes.getValue(i));
      }
    }
  }
  
  private String getPrefix(String paramString)
  {
    int i = paramString.indexOf(':');
    if (i == -1) {
      return "";
    }
    return paramString.substring(0, i);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\util\xml\ContentHandlerToXMLStreamWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */