package com.sun.org.apache.xml.internal.serializer;

import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

abstract interface ExtendedLexicalHandler
  extends LexicalHandler
{
  public abstract void comment(String paramString)
    throws SAXException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\serializer\ExtendedLexicalHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */