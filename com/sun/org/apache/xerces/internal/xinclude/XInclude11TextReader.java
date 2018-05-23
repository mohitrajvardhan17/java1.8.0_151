package com.sun.org.apache.xerces.internal.xinclude;

import com.sun.org.apache.xerces.internal.util.XML11Char;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.IOException;

public class XInclude11TextReader
  extends XIncludeTextReader
{
  public XInclude11TextReader(XMLInputSource paramXMLInputSource, XIncludeHandler paramXIncludeHandler, int paramInt)
    throws IOException
  {
    super(paramXMLInputSource, paramXIncludeHandler, paramInt);
  }
  
  protected boolean isValid(int paramInt)
  {
    return XML11Char.isXML11Valid(paramInt);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xinclude\XInclude11TextReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */