package java.util.concurrent.locks;

import java.io.Serializable;

public abstract class AbstractOwnableSynchronizer
  implements Serializable
{
  private static final long serialVersionUID = 3737899427754241961L;
  private transient Thread exclusiveOwnerThread;
  
  protected AbstractOwnableSynchronizer() {}
  
  protected final void setExclusiveOwnerThread(Thread paramThread)
  {
    exclusiveOwnerThread = paramThread;
  }
  
  protected final Thread getExclusiveOwnerThread()
  {
    return exclusiveOwnerThread;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\locks\AbstractOwnableSynchronizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */