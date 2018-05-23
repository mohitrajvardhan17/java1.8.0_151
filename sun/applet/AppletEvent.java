package sun.applet;

import java.util.EventObject;

public class AppletEvent
  extends EventObject
{
  private Object arg;
  private int id;
  
  public AppletEvent(Object paramObject1, int paramInt, Object paramObject2)
  {
    super(paramObject1);
    arg = paramObject2;
    id = paramInt;
  }
  
  public int getID()
  {
    return id;
  }
  
  public Object getArgument()
  {
    return arg;
  }
  
  public String toString()
  {
    String str = getClass().getName() + "[source=" + source + " + id=" + id;
    if (arg != null) {
      str = str + " + arg=" + arg;
    }
    str = str + " ]";
    return str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\applet\AppletEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */