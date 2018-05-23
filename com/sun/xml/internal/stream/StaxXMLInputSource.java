package com.sun.xml.internal.stream;

import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;

public class StaxXMLInputSource
{
  XMLStreamReader fStreamReader;
  XMLEventReader fEventReader;
  XMLInputSource fInputSource;
  boolean fHasResolver = false;
  
  public StaxXMLInputSource(XMLStreamReader paramXMLStreamReader)
  {
    fStreamReader = paramXMLStreamReader;
  }
  
  public StaxXMLInputSource(XMLEventReader paramXMLEventReader)
  {
    fEventReader = paramXMLEventReader;
  }
  
  public StaxXMLInputSource(XMLInputSource paramXMLInputSource)
  {
    fInputSource = paramXMLInputSource;
  }
  
  public StaxXMLInputSource(XMLInputSource paramXMLInputSource, boolean paramBoolean)
  {
    fInputSource = paramXMLInputSource;
    fHasResolver = paramBoolean;
  }
  
  public XMLStreamReader getXMLStreamReader()
  {
    return fStreamReader;
  }
  
  public XMLEventReader getXMLEventReader()
  {
    return fEventReader;
  }
  
  public XMLInputSource getXMLInputSource()
  {
    return fInputSource;
  }
  
  public boolean hasXMLStreamOrXMLEventReader()
  {
    return (fStreamReader != null) || (fEventReader != null);
  }
  
  public boolean hasResolver()
  {
    return fHasResolver;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\StaxXMLInputSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */