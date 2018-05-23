package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.IOException;

public abstract interface XMLDocumentScanner
  extends XMLDocumentSource
{
  public abstract void setInputSource(XMLInputSource paramXMLInputSource)
    throws IOException;
  
  public abstract boolean scanDocument(boolean paramBoolean)
    throws IOException, XNIException;
  
  public abstract int next()
    throws XNIException, IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xni\parser\XMLDocumentScanner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */