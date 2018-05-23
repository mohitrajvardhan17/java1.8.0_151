package org.w3c.dom.events;

public class EventException
  extends RuntimeException
{
  public short code;
  public static final short UNSPECIFIED_EVENT_TYPE_ERR = 0;
  
  public EventException(short paramShort, String paramString)
  {
    super(paramString);
    code = paramShort;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\events\EventException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */