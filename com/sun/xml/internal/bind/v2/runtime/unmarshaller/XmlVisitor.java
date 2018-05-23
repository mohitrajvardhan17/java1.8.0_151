package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import javax.xml.namespace.NamespaceContext;
import org.xml.sax.SAXException;

public abstract interface XmlVisitor
{
  public abstract void startDocument(LocatorEx paramLocatorEx, NamespaceContext paramNamespaceContext)
    throws SAXException;
  
  public abstract void endDocument()
    throws SAXException;
  
  public abstract void startElement(TagName paramTagName)
    throws SAXException;
  
  public abstract void endElement(TagName paramTagName)
    throws SAXException;
  
  public abstract void startPrefixMapping(String paramString1, String paramString2)
    throws SAXException;
  
  public abstract void endPrefixMapping(String paramString)
    throws SAXException;
  
  public abstract void text(CharSequence paramCharSequence)
    throws SAXException;
  
  public abstract UnmarshallingContext getContext();
  
  public abstract TextPredictor getPredictor();
  
  public static abstract interface TextPredictor
  {
    public abstract boolean expectText();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\unmarshaller\XmlVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */