package com.sun.xml.internal.ws.util.xml;

import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory.RecycleAware;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class XMLStreamReaderFilter
  implements XMLStreamReaderFactory.RecycleAware, XMLStreamReader
{
  protected XMLStreamReader reader;
  
  public XMLStreamReaderFilter(XMLStreamReader paramXMLStreamReader)
  {
    reader = paramXMLStreamReader;
  }
  
  public void onRecycled()
  {
    XMLStreamReaderFactory.recycle(reader);
    reader = null;
  }
  
  public int getAttributeCount()
  {
    return reader.getAttributeCount();
  }
  
  public int getEventType()
  {
    return reader.getEventType();
  }
  
  public int getNamespaceCount()
  {
    return reader.getNamespaceCount();
  }
  
  public int getTextLength()
  {
    return reader.getTextLength();
  }
  
  public int getTextStart()
  {
    return reader.getTextStart();
  }
  
  public int next()
    throws XMLStreamException
  {
    return reader.next();
  }
  
  public int nextTag()
    throws XMLStreamException
  {
    return reader.nextTag();
  }
  
  public void close()
    throws XMLStreamException
  {
    reader.close();
  }
  
  public boolean hasName()
  {
    return reader.hasName();
  }
  
  public boolean hasNext()
    throws XMLStreamException
  {
    return reader.hasNext();
  }
  
  public boolean hasText()
  {
    return reader.hasText();
  }
  
  public boolean isCharacters()
  {
    return reader.isCharacters();
  }
  
  public boolean isEndElement()
  {
    return reader.isEndElement();
  }
  
  public boolean isStandalone()
  {
    return reader.isStandalone();
  }
  
  public boolean isStartElement()
  {
    return reader.isStartElement();
  }
  
  public boolean isWhiteSpace()
  {
    return reader.isWhiteSpace();
  }
  
  public boolean standaloneSet()
  {
    return reader.standaloneSet();
  }
  
  public char[] getTextCharacters()
  {
    return reader.getTextCharacters();
  }
  
  public boolean isAttributeSpecified(int paramInt)
  {
    return reader.isAttributeSpecified(paramInt);
  }
  
  public int getTextCharacters(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3)
    throws XMLStreamException
  {
    return reader.getTextCharacters(paramInt1, paramArrayOfChar, paramInt2, paramInt3);
  }
  
  public String getCharacterEncodingScheme()
  {
    return reader.getCharacterEncodingScheme();
  }
  
  public String getElementText()
    throws XMLStreamException
  {
    return reader.getElementText();
  }
  
  public String getEncoding()
  {
    return reader.getEncoding();
  }
  
  public String getLocalName()
  {
    return reader.getLocalName();
  }
  
  public String getNamespaceURI()
  {
    return reader.getNamespaceURI();
  }
  
  public String getPIData()
  {
    return reader.getPIData();
  }
  
  public String getPITarget()
  {
    return reader.getPITarget();
  }
  
  public String getPrefix()
  {
    return reader.getPrefix();
  }
  
  public String getText()
  {
    return reader.getText();
  }
  
  public String getVersion()
  {
    return reader.getVersion();
  }
  
  public String getAttributeLocalName(int paramInt)
  {
    return reader.getAttributeLocalName(paramInt);
  }
  
  public String getAttributeNamespace(int paramInt)
  {
    return reader.getAttributeNamespace(paramInt);
  }
  
  public String getAttributePrefix(int paramInt)
  {
    return reader.getAttributePrefix(paramInt);
  }
  
  public String getAttributeType(int paramInt)
  {
    return reader.getAttributeType(paramInt);
  }
  
  public String getAttributeValue(int paramInt)
  {
    return reader.getAttributeValue(paramInt);
  }
  
  public String getNamespacePrefix(int paramInt)
  {
    return reader.getNamespacePrefix(paramInt);
  }
  
  public String getNamespaceURI(int paramInt)
  {
    return reader.getNamespaceURI(paramInt);
  }
  
  public NamespaceContext getNamespaceContext()
  {
    return reader.getNamespaceContext();
  }
  
  public QName getName()
  {
    return reader.getName();
  }
  
  public QName getAttributeName(int paramInt)
  {
    return reader.getAttributeName(paramInt);
  }
  
  public Location getLocation()
  {
    return reader.getLocation();
  }
  
  public Object getProperty(String paramString)
    throws IllegalArgumentException
  {
    return reader.getProperty(paramString);
  }
  
  public void require(int paramInt, String paramString1, String paramString2)
    throws XMLStreamException
  {
    reader.require(paramInt, paramString1, paramString2);
  }
  
  public String getNamespaceURI(String paramString)
  {
    return reader.getNamespaceURI(paramString);
  }
  
  public String getAttributeValue(String paramString1, String paramString2)
  {
    return reader.getAttributeValue(paramString1, paramString2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\util\xml\XMLStreamReaderFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */