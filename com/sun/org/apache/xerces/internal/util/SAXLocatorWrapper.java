package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import org.xml.sax.Locator;
import org.xml.sax.ext.Locator2;

public final class SAXLocatorWrapper
  implements XMLLocator
{
  private Locator fLocator = null;
  private Locator2 fLocator2 = null;
  
  public SAXLocatorWrapper() {}
  
  public void setLocator(Locator paramLocator)
  {
    fLocator = paramLocator;
    if (((paramLocator instanceof Locator2)) || (paramLocator == null)) {
      fLocator2 = ((Locator2)paramLocator);
    }
  }
  
  public Locator getLocator()
  {
    return fLocator;
  }
  
  public String getPublicId()
  {
    if (fLocator != null) {
      return fLocator.getPublicId();
    }
    return null;
  }
  
  public String getLiteralSystemId()
  {
    if (fLocator != null) {
      return fLocator.getSystemId();
    }
    return null;
  }
  
  public String getBaseSystemId()
  {
    return null;
  }
  
  public String getExpandedSystemId()
  {
    return getLiteralSystemId();
  }
  
  public int getLineNumber()
  {
    if (fLocator != null) {
      return fLocator.getLineNumber();
    }
    return -1;
  }
  
  public int getColumnNumber()
  {
    if (fLocator != null) {
      return fLocator.getColumnNumber();
    }
    return -1;
  }
  
  public int getCharacterOffset()
  {
    return -1;
  }
  
  public String getEncoding()
  {
    if (fLocator2 != null) {
      return fLocator2.getEncoding();
    }
    return null;
  }
  
  public String getXMLVersion()
  {
    if (fLocator2 != null) {
      return fLocator2.getXMLVersion();
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\util\SAXLocatorWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */