package sun.awt;

import java.awt.AWTEvent;
import java.awt.Component;

public class UngrabEvent
  extends AWTEvent
{
  private static final int UNGRAB_EVENT_ID = 1998;
  
  public UngrabEvent(Component paramComponent)
  {
    super(paramComponent, 1998);
  }
  
  public String toString()
  {
    return "sun.awt.UngrabEvent[" + getSource() + "]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\UngrabEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */