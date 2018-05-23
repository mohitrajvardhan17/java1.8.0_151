package sun.awt;

import java.awt.Window;
import java.awt.event.WindowEvent;

public class TimedWindowEvent
  extends WindowEvent
{
  private long time;
  
  public long getWhen()
  {
    return time;
  }
  
  public TimedWindowEvent(Window paramWindow1, int paramInt, Window paramWindow2, long paramLong)
  {
    super(paramWindow1, paramInt, paramWindow2);
    time = paramLong;
  }
  
  public TimedWindowEvent(Window paramWindow1, int paramInt1, Window paramWindow2, int paramInt2, int paramInt3, long paramLong)
  {
    super(paramWindow1, paramInt1, paramWindow2, paramInt2, paramInt3);
    time = paramLong;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\TimedWindowEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */