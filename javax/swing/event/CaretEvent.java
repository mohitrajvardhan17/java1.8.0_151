package javax.swing.event;

import java.util.EventObject;

public abstract class CaretEvent
  extends EventObject
{
  public CaretEvent(Object paramObject)
  {
    super(paramObject);
  }
  
  public abstract int getDot();
  
  public abstract int getMark();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\event\CaretEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */