package com.sun.xml.internal.stream.buffer.sax;

import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class DefaultWithLexicalHandler
  extends DefaultHandler
  implements LexicalHandler
{
  public DefaultWithLexicalHandler() {}
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {}
  
  public void startDTD(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {}
  
  public void endDTD()
    throws SAXException
  {}
  
  public void startEntity(String paramString)
    throws SAXException
  {}
  
  public void endEntity(String paramString)
    throws SAXException
  {}
  
  public void startCDATA()
    throws SAXException
  {}
  
  public void endCDATA()
    throws SAXException
  {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\buffer\sax\DefaultWithLexicalHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */