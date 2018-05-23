package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import javax.xml.stream.Location;

public final class StAXLocationWrapper
  implements XMLLocator
{
  private Location fLocation = null;
  
  public StAXLocationWrapper() {}
  
  public void setLocation(Location paramLocation)
  {
    fLocation = paramLocation;
  }
  
  public Location getLocation()
  {
    return fLocation;
  }
  
  public String getPublicId()
  {
    if (fLocation != null) {
      return fLocation.getPublicId();
    }
    return null;
  }
  
  public String getLiteralSystemId()
  {
    if (fLocation != null) {
      return fLocation.getSystemId();
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
    if (fLocation != null) {
      return fLocation.getLineNumber();
    }
    return -1;
  }
  
  public int getColumnNumber()
  {
    if (fLocation != null) {
      return fLocation.getColumnNumber();
    }
    return -1;
  }
  
  public int getCharacterOffset()
  {
    if (fLocation != null) {
      return fLocation.getCharacterOffset();
    }
    return -1;
  }
  
  public String getEncoding()
  {
    return null;
  }
  
  public String getXMLVersion()
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\util\StAXLocationWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */