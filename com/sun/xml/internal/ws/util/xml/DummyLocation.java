package com.sun.xml.internal.ws.util.xml;

import javax.xml.stream.Location;

public final class DummyLocation
  implements Location
{
  public static final Location INSTANCE = new DummyLocation();
  
  private DummyLocation() {}
  
  public int getCharacterOffset()
  {
    return -1;
  }
  
  public int getColumnNumber()
  {
    return -1;
  }
  
  public int getLineNumber()
  {
    return -1;
  }
  
  public String getPublicId()
  {
    return null;
  }
  
  public String getSystemId()
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\util\xml\DummyLocation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */