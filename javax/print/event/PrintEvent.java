package javax.print.event;

import java.util.EventObject;

public class PrintEvent
  extends EventObject
{
  private static final long serialVersionUID = 2286914924430763847L;
  
  public PrintEvent(Object paramObject)
  {
    super(paramObject);
  }
  
  public String toString()
  {
    return "PrintEvent on " + getSource().toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\event\PrintEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */