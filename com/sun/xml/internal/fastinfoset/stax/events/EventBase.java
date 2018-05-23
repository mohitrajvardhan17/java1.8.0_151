package com.sun.xml.internal.fastinfoset.stax.events;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import java.io.Writer;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public abstract class EventBase
  implements XMLEvent
{
  protected int _eventType;
  protected Location _location = null;
  
  public EventBase() {}
  
  public EventBase(int paramInt)
  {
    _eventType = paramInt;
  }
  
  public int getEventType()
  {
    return _eventType;
  }
  
  protected void setEventType(int paramInt)
  {
    _eventType = paramInt;
  }
  
  public boolean isStartElement()
  {
    return _eventType == 1;
  }
  
  public boolean isEndElement()
  {
    return _eventType == 2;
  }
  
  public boolean isEntityReference()
  {
    return _eventType == 9;
  }
  
  public boolean isProcessingInstruction()
  {
    return _eventType == 3;
  }
  
  public boolean isStartDocument()
  {
    return _eventType == 7;
  }
  
  public boolean isEndDocument()
  {
    return _eventType == 8;
  }
  
  public Location getLocation()
  {
    return _location;
  }
  
  public void setLocation(Location paramLocation)
  {
    _location = paramLocation;
  }
  
  public String getSystemId()
  {
    if (_location == null) {
      return "";
    }
    return _location.getSystemId();
  }
  
  public Characters asCharacters()
  {
    if (isCharacters()) {
      return (Characters)this;
    }
    throw new ClassCastException(CommonResourceBundle.getInstance().getString("message.charactersCast", new Object[] { getEventTypeString() }));
  }
  
  public EndElement asEndElement()
  {
    if (isEndElement()) {
      return (EndElement)this;
    }
    throw new ClassCastException(CommonResourceBundle.getInstance().getString("message.endElementCase", new Object[] { getEventTypeString() }));
  }
  
  public StartElement asStartElement()
  {
    if (isStartElement()) {
      return (StartElement)this;
    }
    throw new ClassCastException(CommonResourceBundle.getInstance().getString("message.startElementCase", new Object[] { getEventTypeString() }));
  }
  
  public QName getSchemaType()
  {
    return null;
  }
  
  public boolean isAttribute()
  {
    return _eventType == 10;
  }
  
  public boolean isCharacters()
  {
    return _eventType == 4;
  }
  
  public boolean isNamespace()
  {
    return _eventType == 13;
  }
  
  public void writeAsEncodedUnicode(Writer paramWriter)
    throws XMLStreamException
  {}
  
  private String getEventTypeString()
  {
    switch (_eventType)
    {
    case 1: 
      return "StartElementEvent";
    case 2: 
      return "EndElementEvent";
    case 3: 
      return "ProcessingInstructionEvent";
    case 4: 
      return "CharacterEvent";
    case 5: 
      return "CommentEvent";
    case 7: 
      return "StartDocumentEvent";
    case 8: 
      return "EndDocumentEvent";
    case 9: 
      return "EntityReferenceEvent";
    case 10: 
      return "AttributeBase";
    case 11: 
      return "DTDEvent";
    case 12: 
      return "CDATA";
    }
    return "UNKNOWN_EVENT_TYPE";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\stax\events\EventBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */