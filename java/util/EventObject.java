package java.util;

import java.io.Serializable;

public class EventObject
  implements Serializable
{
  private static final long serialVersionUID = 5516075349620653480L;
  protected transient Object source;
  
  public EventObject(Object paramObject)
  {
    if (paramObject == null) {
      throw new IllegalArgumentException("null source");
    }
    source = paramObject;
  }
  
  public Object getSource()
  {
    return source;
  }
  
  public String toString()
  {
    return getClass().getName() + "[source=" + source + "]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\EventObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */