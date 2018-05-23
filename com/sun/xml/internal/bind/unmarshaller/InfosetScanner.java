package com.sun.xml.internal.bind.unmarshaller;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LocatorEx;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public abstract interface InfosetScanner<XmlNode>
{
  public abstract void scan(XmlNode paramXmlNode)
    throws SAXException;
  
  public abstract void setContentHandler(ContentHandler paramContentHandler);
  
  public abstract ContentHandler getContentHandler();
  
  public abstract XmlNode getCurrentElement();
  
  public abstract LocatorEx getLocator();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\unmarshaller\InfosetScanner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */