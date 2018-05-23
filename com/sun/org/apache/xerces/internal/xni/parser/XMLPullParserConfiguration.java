package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.IOException;

public abstract interface XMLPullParserConfiguration
  extends XMLParserConfiguration
{
  public abstract void setInputSource(XMLInputSource paramXMLInputSource)
    throws XMLConfigurationException, IOException;
  
  public abstract boolean parse(boolean paramBoolean)
    throws XNIException, IOException;
  
  public abstract void cleanup();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xni\parser\XMLPullParserConfiguration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */