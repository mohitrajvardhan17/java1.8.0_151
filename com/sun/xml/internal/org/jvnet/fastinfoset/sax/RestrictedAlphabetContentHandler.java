package com.sun.xml.internal.org.jvnet.fastinfoset.sax;

import org.xml.sax.SAXException;

public abstract interface RestrictedAlphabetContentHandler
{
  public abstract void numericCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException;
  
  public abstract void dateTimeCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException;
  
  public abstract void alphabetCharacters(String paramString, char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\fastinfoset\sax\RestrictedAlphabetContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */