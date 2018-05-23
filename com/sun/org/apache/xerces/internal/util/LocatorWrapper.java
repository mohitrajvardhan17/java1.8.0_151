package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import org.xml.sax.Locator;

public class LocatorWrapper
  implements XMLLocator
{
  private final Locator locator;
  
  public LocatorWrapper(Locator paramLocator)
  {
    locator = paramLocator;
  }
  
  public int getColumnNumber()
  {
    return locator.getColumnNumber();
  }
  
  public int getLineNumber()
  {
    return locator.getLineNumber();
  }
  
  public String getBaseSystemId()
  {
    return null;
  }
  
  public String getExpandedSystemId()
  {
    return locator.getSystemId();
  }
  
  public String getLiteralSystemId()
  {
    return locator.getSystemId();
  }
  
  public String getPublicId()
  {
    return locator.getPublicId();
  }
  
  public String getEncoding()
  {
    return null;
  }
  
  public int getCharacterOffset()
  {
    return -1;
  }
  
  public String getXMLVersion()
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\util\LocatorWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */