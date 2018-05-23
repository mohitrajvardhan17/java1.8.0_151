package javax.swing.text.html;

import java.awt.event.InputEvent;
import java.net.URL;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.text.Element;

public class HTMLFrameHyperlinkEvent
  extends HyperlinkEvent
{
  private String targetFrame;
  
  public HTMLFrameHyperlinkEvent(Object paramObject, HyperlinkEvent.EventType paramEventType, URL paramURL, String paramString)
  {
    super(paramObject, paramEventType, paramURL);
    targetFrame = paramString;
  }
  
  public HTMLFrameHyperlinkEvent(Object paramObject, HyperlinkEvent.EventType paramEventType, URL paramURL, String paramString1, String paramString2)
  {
    super(paramObject, paramEventType, paramURL, paramString1);
    targetFrame = paramString2;
  }
  
  public HTMLFrameHyperlinkEvent(Object paramObject, HyperlinkEvent.EventType paramEventType, URL paramURL, Element paramElement, String paramString)
  {
    super(paramObject, paramEventType, paramURL, null, paramElement);
    targetFrame = paramString;
  }
  
  public HTMLFrameHyperlinkEvent(Object paramObject, HyperlinkEvent.EventType paramEventType, URL paramURL, String paramString1, Element paramElement, String paramString2)
  {
    super(paramObject, paramEventType, paramURL, paramString1, paramElement);
    targetFrame = paramString2;
  }
  
  public HTMLFrameHyperlinkEvent(Object paramObject, HyperlinkEvent.EventType paramEventType, URL paramURL, String paramString1, Element paramElement, InputEvent paramInputEvent, String paramString2)
  {
    super(paramObject, paramEventType, paramURL, paramString1, paramElement, paramInputEvent);
    targetFrame = paramString2;
  }
  
  public String getTarget()
  {
    return targetFrame;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\HTMLFrameHyperlinkEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */