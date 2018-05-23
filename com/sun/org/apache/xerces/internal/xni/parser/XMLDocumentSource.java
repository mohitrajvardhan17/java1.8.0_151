package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;

public abstract interface XMLDocumentSource
{
  public abstract void setDocumentHandler(XMLDocumentHandler paramXMLDocumentHandler);
  
  public abstract XMLDocumentHandler getDocumentHandler();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xni\parser\XMLDocumentSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */