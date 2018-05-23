package org.xml.sax.ext;

import org.xml.sax.SAXException;

public abstract interface LexicalHandler
{
  public abstract void startDTD(String paramString1, String paramString2, String paramString3)
    throws SAXException;
  
  public abstract void endDTD()
    throws SAXException;
  
  public abstract void startEntity(String paramString)
    throws SAXException;
  
  public abstract void endEntity(String paramString)
    throws SAXException;
  
  public abstract void startCDATA()
    throws SAXException;
  
  public abstract void endCDATA()
    throws SAXException;
  
  public abstract void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\xml\sax\ext\LexicalHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */