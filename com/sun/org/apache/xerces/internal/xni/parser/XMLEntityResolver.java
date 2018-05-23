package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.IOException;

public abstract interface XMLEntityResolver
{
  public abstract XMLInputSource resolveEntity(XMLResourceIdentifier paramXMLResourceIdentifier)
    throws XNIException, IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xni\parser\XMLEntityResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */