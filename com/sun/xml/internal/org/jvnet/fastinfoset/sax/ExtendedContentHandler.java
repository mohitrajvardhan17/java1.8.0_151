package com.sun.xml.internal.org.jvnet.fastinfoset.sax;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public abstract interface ExtendedContentHandler
  extends ContentHandler
{
  public abstract void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean)
    throws SAXException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\fastinfoset\sax\ExtendedContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */