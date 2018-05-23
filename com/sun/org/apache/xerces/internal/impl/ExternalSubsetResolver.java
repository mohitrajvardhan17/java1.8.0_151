package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLDTDDescription;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.IOException;

public abstract interface ExternalSubsetResolver
  extends XMLEntityResolver
{
  public abstract XMLInputSource getExternalSubset(XMLDTDDescription paramXMLDTDDescription)
    throws XNIException, IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\ExternalSubsetResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */