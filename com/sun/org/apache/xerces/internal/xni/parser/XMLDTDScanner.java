package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.utils.XMLLimitAnalyzer;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.IOException;

public abstract interface XMLDTDScanner
  extends XMLDTDSource, XMLDTDContentModelSource
{
  public abstract void setInputSource(XMLInputSource paramXMLInputSource)
    throws IOException;
  
  public abstract boolean scanDTDInternalSubset(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
    throws IOException, XNIException;
  
  public abstract boolean scanDTDExternalSubset(boolean paramBoolean)
    throws IOException, XNIException;
  
  public abstract boolean skipDTD(boolean paramBoolean)
    throws IOException;
  
  public abstract void setLimitAnalyzer(XMLLimitAnalyzer paramXMLLimitAnalyzer);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xni\parser\XMLDTDScanner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */