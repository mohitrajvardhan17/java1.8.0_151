package com.sun.xml.internal.stream.events;

import java.io.IOException;
import java.io.Writer;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public abstract class DummyEvent
  implements XMLEvent
{
  private static DummyLocation nowhere = new DummyLocation();
  private int fEventType;
  protected Location fLocation = nowhere;
  
  public DummyEvent() {}
  
  public DummyEvent(int paramInt)
  {
    fEventType = paramInt;
  }
  
  public int getEventType()
  {
    return fEventType;
  }
  
  protected void setEventType(int paramInt)
  {
    fEventType = paramInt;
  }
  
  public boolean isStartElement()
  {
    return fEventType == 1;
  }
  
  public boolean isEndElement()
  {
    return fEventType == 2;
  }
  
  public boolean isEntityReference()
  {
    return fEventType == 9;
  }
  
  public boolean isProcessingInstruction()
  {
    return fEventType == 3;
  }
  
  public boolean isCharacterData()
  {
    return fEventType == 4;
  }
  
  public boolean isStartDocument()
  {
    return fEventType == 7;
  }
  
  public boolean isEndDocument()
  {
    return fEventType == 8;
  }
  
  public Location getLocation()
  {
    return fLocation;
  }
  
  void setLocation(Location paramLocation)
  {
    if (paramLocation == null) {
      fLocation = nowhere;
    } else {
      fLocation = paramLocation;
    }
  }
  
  public Characters asCharacters()
  {
    return (Characters)this;
  }
  
  public EndElement asEndElement()
  {
    return (EndElement)this;
  }
  
  public StartElement asStartElement()
  {
    return (StartElement)this;
  }
  
  public QName getSchemaType()
  {
    return null;
  }
  
  public boolean isAttribute()
  {
    return fEventType == 10;
  }
  
  public boolean isCharacters()
  {
    return fEventType == 4;
  }
  
  public boolean isNamespace()
  {
    return fEventType == 13;
  }
  
  public void writeAsEncodedUnicode(Writer paramWriter)
    throws XMLStreamException
  {
    try
    {
      writeAsEncodedUnicodeEx(paramWriter);
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
  }
  
  protected abstract void writeAsEncodedUnicodeEx(Writer paramWriter)
    throws IOException, XMLStreamException;
  
  protected void charEncode(Writer paramWriter, String paramString)
    throws IOException
  {
    if ((paramString == null) || (paramString == "")) {
      return;
    }
    int i = 0;
    int j = 0;
    int k = paramString.length();
    while (i < k)
    {
      switch (paramString.charAt(i))
      {
      case '<': 
        paramWriter.write(paramString, j, i - j);
        paramWriter.write("&lt;");
        j = i + 1;
        break;
      case '&': 
        paramWriter.write(paramString, j, i - j);
        paramWriter.write("&amp;");
        j = i + 1;
        break;
      case '>': 
        paramWriter.write(paramString, j, i - j);
        paramWriter.write("&gt;");
        j = i + 1;
        break;
      case '"': 
        paramWriter.write(paramString, j, i - j);
        paramWriter.write("&quot;");
        j = i + 1;
      }
      i++;
    }
    paramWriter.write(paramString, j, k - j);
  }
  
  static class DummyLocation
    implements Location
  {
    public DummyLocation() {}
    
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
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\events\DummyEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */