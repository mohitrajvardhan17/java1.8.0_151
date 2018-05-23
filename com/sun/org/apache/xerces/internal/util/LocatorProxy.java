package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import org.xml.sax.ext.Locator2;

public class LocatorProxy
  implements Locator2
{
  private final XMLLocator fLocator;
  
  public LocatorProxy(XMLLocator paramXMLLocator)
  {
    fLocator = paramXMLLocator;
  }
  
  public String getPublicId()
  {
    return fLocator.getPublicId();
  }
  
  public String getSystemId()
  {
    return fLocator.getExpandedSystemId();
  }
  
  public int getLineNumber()
  {
    return fLocator.getLineNumber();
  }
  
  public int getColumnNumber()
  {
    return fLocator.getColumnNumber();
  }
  
  public String getXMLVersion()
  {
    return fLocator.getXMLVersion();
  }
  
  public String getEncoding()
  {
    return fLocator.getEncoding();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\util\LocatorProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */