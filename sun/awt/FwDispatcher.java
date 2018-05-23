package sun.awt;

import java.awt.SecondaryLoop;

public abstract interface FwDispatcher
{
  public abstract boolean isDispatchThread();
  
  public abstract void scheduleDispatch(Runnable paramRunnable);
  
  public abstract SecondaryLoop createSecondaryLoop();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\FwDispatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */