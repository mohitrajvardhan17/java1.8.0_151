package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.xni.XNIException;

public abstract interface XMLErrorHandler
{
  public abstract void warning(String paramString1, String paramString2, XMLParseException paramXMLParseException)
    throws XNIException;
  
  public abstract void error(String paramString1, String paramString2, XMLParseException paramXMLParseException)
    throws XNIException;
  
  public abstract void fatalError(String paramString1, String paramString2, XMLParseException paramXMLParseException)
    throws XNIException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xni\parser\XMLErrorHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */