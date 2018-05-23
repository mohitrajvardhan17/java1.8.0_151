package com.sun.org.apache.xerces.internal.impl;

import java.io.PrintStream;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class XMLStreamFilterImpl
  implements XMLStreamReader
{
  private StreamFilter fStreamFilter = null;
  private XMLStreamReader fStreamReader = null;
  private int fCurrentEvent;
  private boolean fEventAccepted = false;
  private boolean fStreamAdvancedByHasNext = false;
  
  public XMLStreamFilterImpl(XMLStreamReader paramXMLStreamReader, StreamFilter paramStreamFilter)
  {
    fStreamReader = paramXMLStreamReader;
    fStreamFilter = paramStreamFilter;
    try
    {
      if (fStreamFilter.accept(fStreamReader)) {
        fEventAccepted = true;
      } else {
        findNextEvent();
      }
    }
    catch (XMLStreamException localXMLStreamException)
    {
      System.err.println("Error while creating a stream Filter" + localXMLStreamException);
    }
  }
  
  protected void setStreamFilter(StreamFilter paramStreamFilter)
  {
    fStreamFilter = paramStreamFilter;
  }
  
  public int next()
    throws XMLStreamException
  {
    if ((fStreamAdvancedByHasNext) && (fEventAccepted))
    {
      fStreamAdvancedByHasNext = false;
      return fCurrentEvent;
    }
    int i = findNextEvent();
    if (i != -1) {
      return i;
    }
    throw new IllegalStateException("The stream reader has reached the end of the document, or there are no more  items to return");
  }
  
  public int nextTag()
    throws XMLStreamException
  {
    if ((fStreamAdvancedByHasNext) && (fEventAccepted) && ((fCurrentEvent == 1) || (fCurrentEvent == 1)))
    {
      fStreamAdvancedByHasNext = false;
      return fCurrentEvent;
    }
    int i = findNextTag();
    if (i != -1) {
      return i;
    }
    throw new IllegalStateException("The stream reader has reached the end of the document, or there are no more  items to return");
  }
  
  public boolean hasNext()
    throws XMLStreamException
  {
    if (fStreamReader.hasNext())
    {
      if (!fEventAccepted)
      {
        if ((fCurrentEvent = findNextEvent()) == -1) {
          return false;
        }
        fStreamAdvancedByHasNext = true;
      }
      return true;
    }
    return false;
  }
  
  private int findNextEvent()
    throws XMLStreamException
  {
    fStreamAdvancedByHasNext = false;
    while (fStreamReader.hasNext())
    {
      fCurrentEvent = fStreamReader.next();
      if (fStreamFilter.accept(fStreamReader))
      {
        fEventAccepted = true;
        return fCurrentEvent;
      }
    }
    if (fCurrentEvent == 8) {
      return fCurrentEvent;
    }
    return -1;
  }
  
  private int findNextTag()
    throws XMLStreamException
  {
    fStreamAdvancedByHasNext = false;
    while (fStreamReader.hasNext())
    {
      fCurrentEvent = fStreamReader.nextTag();
      if (fStreamFilter.accept(fStreamReader))
      {
        fEventAccepted = true;
        return fCurrentEvent;
      }
    }
    if (fCurrentEvent == 8) {
      return fCurrentEvent;
    }
    return -1;
  }
  
  public void close()
    throws XMLStreamException
  {
    fStreamReader.close();
  }
  
  public int getAttributeCount()
  {
    return fStreamReader.getAttributeCount();
  }
  
  public QName getAttributeName(int paramInt)
  {
    return fStreamReader.getAttributeName(paramInt);
  }
  
  public String getAttributeNamespace(int paramInt)
  {
    return fStreamReader.getAttributeNamespace(paramInt);
  }
  
  public String getAttributePrefix(int paramInt)
  {
    return fStreamReader.getAttributePrefix(paramInt);
  }
  
  public String getAttributeType(int paramInt)
  {
    return fStreamReader.getAttributeType(paramInt);
  }
  
  public String getAttributeValue(int paramInt)
  {
    return fStreamReader.getAttributeValue(paramInt);
  }
  
  public String getAttributeValue(String paramString1, String paramString2)
  {
    return fStreamReader.getAttributeValue(paramString1, paramString2);
  }
  
  public String getCharacterEncodingScheme()
  {
    return fStreamReader.getCharacterEncodingScheme();
  }
  
  public String getElementText()
    throws XMLStreamException
  {
    return fStreamReader.getElementText();
  }
  
  public String getEncoding()
  {
    return fStreamReader.getEncoding();
  }
  
  public int getEventType()
  {
    return fStreamReader.getEventType();
  }
  
  public String getLocalName()
  {
    return fStreamReader.getLocalName();
  }
  
  public Location getLocation()
  {
    return fStreamReader.getLocation();
  }
  
  public QName getName()
  {
    return fStreamReader.getName();
  }
  
  public NamespaceContext getNamespaceContext()
  {
    return fStreamReader.getNamespaceContext();
  }
  
  public int getNamespaceCount()
  {
    return fStreamReader.getNamespaceCount();
  }
  
  public String getNamespacePrefix(int paramInt)
  {
    return fStreamReader.getNamespacePrefix(paramInt);
  }
  
  public String getNamespaceURI()
  {
    return fStreamReader.getNamespaceURI();
  }
  
  public String getNamespaceURI(int paramInt)
  {
    return fStreamReader.getNamespaceURI(paramInt);
  }
  
  public String getNamespaceURI(String paramString)
  {
    return fStreamReader.getNamespaceURI(paramString);
  }
  
  public String getPIData()
  {
    return fStreamReader.getPIData();
  }
  
  public String getPITarget()
  {
    return fStreamReader.getPITarget();
  }
  
  public String getPrefix()
  {
    return fStreamReader.getPrefix();
  }
  
  public Object getProperty(String paramString)
    throws IllegalArgumentException
  {
    return fStreamReader.getProperty(paramString);
  }
  
  public String getText()
  {
    return fStreamReader.getText();
  }
  
  public char[] getTextCharacters()
  {
    return fStreamReader.getTextCharacters();
  }
  
  public int getTextCharacters(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3)
    throws XMLStreamException
  {
    return fStreamReader.getTextCharacters(paramInt1, paramArrayOfChar, paramInt2, paramInt3);
  }
  
  public int getTextLength()
  {
    return fStreamReader.getTextLength();
  }
  
  public int getTextStart()
  {
    return fStreamReader.getTextStart();
  }
  
  public String getVersion()
  {
    return fStreamReader.getVersion();
  }
  
  public boolean hasName()
  {
    return fStreamReader.hasName();
  }
  
  public boolean hasText()
  {
    return fStreamReader.hasText();
  }
  
  public boolean isAttributeSpecified(int paramInt)
  {
    return fStreamReader.isAttributeSpecified(paramInt);
  }
  
  public boolean isCharacters()
  {
    return fStreamReader.isCharacters();
  }
  
  public boolean isEndElement()
  {
    return fStreamReader.isEndElement();
  }
  
  public boolean isStandalone()
  {
    return fStreamReader.isStandalone();
  }
  
  public boolean isStartElement()
  {
    return fStreamReader.isStartElement();
  }
  
  public boolean isWhiteSpace()
  {
    return fStreamReader.isWhiteSpace();
  }
  
  public void require(int paramInt, String paramString1, String paramString2)
    throws XMLStreamException
  {
    fStreamReader.require(paramInt, paramString1, paramString2);
  }
  
  public boolean standaloneSet()
  {
    return fStreamReader.standaloneSet();
  }
  
  public String getAttributeLocalName(int paramInt)
  {
    return fStreamReader.getAttributeLocalName(paramInt);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\XMLStreamFilterImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */