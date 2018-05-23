package sun.awt;

import java.awt.event.InvocationEvent;

public class PeerEvent
  extends InvocationEvent
{
  public static final long PRIORITY_EVENT = 1L;
  public static final long ULTIMATE_PRIORITY_EVENT = 2L;
  public static final long LOW_PRIORITY_EVENT = 4L;
  private long flags;
  
  public PeerEvent(Object paramObject, Runnable paramRunnable, long paramLong)
  {
    this(paramObject, paramRunnable, null, false, paramLong);
  }
  
  public PeerEvent(Object paramObject1, Runnable paramRunnable, Object paramObject2, boolean paramBoolean, long paramLong)
  {
    super(paramObject1, paramRunnable, paramObject2, paramBoolean);
    flags = paramLong;
  }
  
  public long getFlags()
  {
    return flags;
  }
  
  public PeerEvent coalesceEvents(PeerEvent paramPeerEvent)
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\PeerEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */