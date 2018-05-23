package com.sun.xml.internal.org.jvnet.fastinfoset.sax;

import org.xml.sax.SAXException;

public abstract interface EncodingAlgorithmContentHandler
{
  public abstract void octets(String paramString, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
    throws SAXException;
  
  public abstract void object(String paramString, int paramInt, Object paramObject)
    throws SAXException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\fastinfoset\sax\EncodingAlgorithmContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */