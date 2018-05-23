package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;

public abstract interface XMLDTDSource
{
  public abstract void setDTDHandler(XMLDTDHandler paramXMLDTDHandler);
  
  public abstract XMLDTDHandler getDTDHandler();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xni\parser\XMLDTDSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */