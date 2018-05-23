package javax.swing.event;

import java.awt.event.InputEvent;
import java.net.URL;
import java.util.EventObject;
import javax.swing.text.Element;

public class HyperlinkEvent
  extends EventObject
{
  private EventType type;
  private URL u;
  private String desc;
  private Element sourceElement;
  private InputEvent inputEvent;
  
  public HyperlinkEvent(Object paramObject, EventType paramEventType, URL paramURL)
  {
    this(paramObject, paramEventType, paramURL, null);
  }
  
  public HyperlinkEvent(Object paramObject, EventType paramEventType, URL paramURL, String paramString)
  {
    this(paramObject, paramEventType, paramURL, paramString, null);
  }
  
  public HyperlinkEvent(Object paramObject, EventType paramEventType, URL paramURL, String paramString, Element paramElement)
  {
    super(paramObject);
    type = paramEventType;
    u = paramURL;
    desc = paramString;
    sourceElement = paramElement;
  }
  
  public HyperlinkEvent(Object paramObject, EventType paramEventType, URL paramURL, String paramString, Element paramElement, InputEvent paramInputEvent)
  {
    super(paramObject);
    type = paramEventType;
    u = paramURL;
    desc = paramString;
    sourceElement = paramElement;
    inputEvent = paramInputEvent;
  }
  
  public EventType getEventType()
  {
    return type;
  }
  
  public String getDescription()
  {
    return desc;
  }
  
  public URL getURL()
  {
    return u;
  }
  
  public Element getSourceElement()
  {
    return sourceElement;
  }
  
  public InputEvent getInputEvent()
  {
    return inputEvent;
  }
  
  public static final class EventType
  {
    public static final EventType ENTERED = new EventType("ENTERED");
    public static final EventType EXITED = new EventType("EXITED");
    public static final EventType ACTIVATED = new EventType("ACTIVATED");
    private String typeString;
    
    private EventType(String paramString)
    {
      typeString = paramString;
    }
    
    public String toString()
    {
      return typeString;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\event\HyperlinkEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */