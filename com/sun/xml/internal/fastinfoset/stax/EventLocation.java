package com.sun.xml.internal.fastinfoset.stax;

import javax.xml.stream.Location;

public class EventLocation
  implements Location
{
  String _systemId = null;
  String _publicId = null;
  int _column = -1;
  int _line = -1;
  int _charOffset = -1;
  
  EventLocation() {}
  
  public static Location getNilLocation()
  {
    return new EventLocation();
  }
  
  public int getLineNumber()
  {
    return _line;
  }
  
  public int getColumnNumber()
  {
    return _column;
  }
  
  public int getCharacterOffset()
  {
    return _charOffset;
  }
  
  public String getPublicId()
  {
    return _publicId;
  }
  
  public String getSystemId()
  {
    return _systemId;
  }
  
  public void setLineNumber(int paramInt)
  {
    _line = paramInt;
  }
  
  public void setColumnNumber(int paramInt)
  {
    _column = paramInt;
  }
  
  public void setCharacterOffset(int paramInt)
  {
    _charOffset = paramInt;
  }
  
  public void setPublicId(String paramString)
  {
    _publicId = paramString;
  }
  
  public void setSystemId(String paramString)
  {
    _systemId = paramString;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("Line number = " + _line);
    localStringBuffer.append("\n");
    localStringBuffer.append("Column number = " + _column);
    localStringBuffer.append("\n");
    localStringBuffer.append("System Id = " + _systemId);
    localStringBuffer.append("\n");
    localStringBuffer.append("Public Id = " + _publicId);
    localStringBuffer.append("\n");
    localStringBuffer.append("CharacterOffset = " + _charOffset);
    localStringBuffer.append("\n");
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\stax\EventLocation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */