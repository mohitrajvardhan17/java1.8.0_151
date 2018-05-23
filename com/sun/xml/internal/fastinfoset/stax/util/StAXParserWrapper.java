package com.sun.xml.internal.fastinfoset.stax.util;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class StAXParserWrapper
  implements XMLStreamReader
{
  private XMLStreamReader _reader;
  
  public StAXParserWrapper() {}
  
  public StAXParserWrapper(XMLStreamReader paramXMLStreamReader)
  {
    _reader = paramXMLStreamReader;
  }
  
  public void setReader(XMLStreamReader paramXMLStreamReader)
  {
    _reader = paramXMLStreamReader;
  }
  
  public XMLStreamReader getReader()
  {
    return _reader;
  }
  
  public int next()
    throws XMLStreamException
  {
    return _reader.next();
  }
  
  public int nextTag()
    throws XMLStreamException
  {
    return _reader.nextTag();
  }
  
  public String getElementText()
    throws XMLStreamException
  {
    return _reader.getElementText();
  }
  
  public void require(int paramInt, String paramString1, String paramString2)
    throws XMLStreamException
  {
    _reader.require(paramInt, paramString1, paramString2);
  }
  
  public boolean hasNext()
    throws XMLStreamException
  {
    return _reader.hasNext();
  }
  
  public void close()
    throws XMLStreamException
  {
    _reader.close();
  }
  
  public String getNamespaceURI(String paramString)
  {
    return _reader.getNamespaceURI(paramString);
  }
  
  public NamespaceContext getNamespaceContext()
  {
    return _reader.getNamespaceContext();
  }
  
  public boolean isStartElement()
  {
    return _reader.isStartElement();
  }
  
  public boolean isEndElement()
  {
    return _reader.isEndElement();
  }
  
  public boolean isCharacters()
  {
    return _reader.isCharacters();
  }
  
  public boolean isWhiteSpace()
  {
    return _reader.isWhiteSpace();
  }
  
  public QName getAttributeName(int paramInt)
  {
    return _reader.getAttributeName(paramInt);
  }
  
  public int getTextCharacters(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3)
    throws XMLStreamException
  {
    return _reader.getTextCharacters(paramInt1, paramArrayOfChar, paramInt2, paramInt3);
  }
  
  public String getAttributeValue(String paramString1, String paramString2)
  {
    return _reader.getAttributeValue(paramString1, paramString2);
  }
  
  public int getAttributeCount()
  {
    return _reader.getAttributeCount();
  }
  
  public String getAttributePrefix(int paramInt)
  {
    return _reader.getAttributePrefix(paramInt);
  }
  
  public String getAttributeNamespace(int paramInt)
  {
    return _reader.getAttributeNamespace(paramInt);
  }
  
  public String getAttributeLocalName(int paramInt)
  {
    return _reader.getAttributeLocalName(paramInt);
  }
  
  public String getAttributeType(int paramInt)
  {
    return _reader.getAttributeType(paramInt);
  }
  
  public String getAttributeValue(int paramInt)
  {
    return _reader.getAttributeValue(paramInt);
  }
  
  public boolean isAttributeSpecified(int paramInt)
  {
    return _reader.isAttributeSpecified(paramInt);
  }
  
  public int getNamespaceCount()
  {
    return _reader.getNamespaceCount();
  }
  
  public String getNamespacePrefix(int paramInt)
  {
    return _reader.getNamespacePrefix(paramInt);
  }
  
  public String getNamespaceURI(int paramInt)
  {
    return _reader.getNamespaceURI(paramInt);
  }
  
  public int getEventType()
  {
    return _reader.getEventType();
  }
  
  public String getText()
  {
    return _reader.getText();
  }
  
  public char[] getTextCharacters()
  {
    return _reader.getTextCharacters();
  }
  
  public int getTextStart()
  {
    return _reader.getTextStart();
  }
  
  public int getTextLength()
  {
    return _reader.getTextLength();
  }
  
  public String getEncoding()
  {
    return _reader.getEncoding();
  }
  
  public boolean hasText()
  {
    return _reader.hasText();
  }
  
  public Location getLocation()
  {
    return _reader.getLocation();
  }
  
  public QName getName()
  {
    return _reader.getName();
  }
  
  public String getLocalName()
  {
    return _reader.getLocalName();
  }
  
  public boolean hasName()
  {
    return _reader.hasName();
  }
  
  public String getNamespaceURI()
  {
    return _reader.getNamespaceURI();
  }
  
  public String getPrefix()
  {
    return _reader.getPrefix();
  }
  
  public String getVersion()
  {
    return _reader.getVersion();
  }
  
  public boolean isStandalone()
  {
    return _reader.isStandalone();
  }
  
  public boolean standaloneSet()
  {
    return _reader.standaloneSet();
  }
  
  public String getCharacterEncodingScheme()
  {
    return _reader.getCharacterEncodingScheme();
  }
  
  public String getPITarget()
  {
    return _reader.getPITarget();
  }
  
  public String getPIData()
  {
    return _reader.getPIData();
  }
  
  public Object getProperty(String paramString)
  {
    return _reader.getProperty(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\stax\util\StAXParserWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */