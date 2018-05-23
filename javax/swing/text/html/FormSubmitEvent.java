package javax.swing.text.html;

import java.net.URL;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.text.Element;

public class FormSubmitEvent
  extends HTMLFrameHyperlinkEvent
{
  private MethodType method;
  private String data;
  
  FormSubmitEvent(Object paramObject, HyperlinkEvent.EventType paramEventType, URL paramURL, Element paramElement, String paramString1, MethodType paramMethodType, String paramString2)
  {
    super(paramObject, paramEventType, paramURL, paramElement, paramString1);
    method = paramMethodType;
    data = paramString2;
  }
  
  public MethodType getMethod()
  {
    return method;
  }
  
  public String getData()
  {
    return data;
  }
  
  public static enum MethodType
  {
    GET,  POST;
    
    private MethodType() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\FormSubmitEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */