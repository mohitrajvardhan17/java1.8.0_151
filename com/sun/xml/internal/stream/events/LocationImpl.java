package com.sun.xml.internal.stream.events;

import javax.xml.stream.Location;

public class LocationImpl
  implements Location
{
  String systemId;
  String publicId;
  int colNo;
  int lineNo;
  int charOffset;
  
  LocationImpl(Location paramLocation)
  {
    systemId = paramLocation.getSystemId();
    publicId = paramLocation.getPublicId();
    lineNo = paramLocation.getLineNumber();
    colNo = paramLocation.getColumnNumber();
    charOffset = paramLocation.getCharacterOffset();
  }
  
  public int getCharacterOffset()
  {
    return charOffset;
  }
  
  public int getColumnNumber()
  {
    return colNo;
  }
  
  public int getLineNumber()
  {
    return lineNo;
  }
  
  public String getPublicId()
  {
    return publicId;
  }
  
  public String getSystemId()
  {
    return systemId;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("Line number = " + getLineNumber());
    localStringBuffer.append("\n");
    localStringBuffer.append("Column number = " + getColumnNumber());
    localStringBuffer.append("\n");
    localStringBuffer.append("System Id = " + getSystemId());
    localStringBuffer.append("\n");
    localStringBuffer.append("Public Id = " + getPublicId());
    localStringBuffer.append("\n");
    localStringBuffer.append("CharacterOffset = " + getCharacterOffset());
    localStringBuffer.append("\n");
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\events\LocationImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */