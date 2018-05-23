package com.sun.istack.internal;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

public class FragmentContentHandler
  extends XMLFilterImpl
{
  public FragmentContentHandler() {}
  
  public FragmentContentHandler(XMLReader paramXMLReader)
  {
    super(paramXMLReader);
  }
  
  public FragmentContentHandler(ContentHandler paramContentHandler)
  {
    setContentHandler(paramContentHandler);
  }
  
  public void startDocument()
    throws SAXException
  {}
  
  public void endDocument()
    throws SAXException
  {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\istack\internal\FragmentContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */