package sun.awt.image;

import java.awt.image.BufferStrategy;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;

public abstract class VSyncedBSManager
{
  private static VSyncedBSManager theInstance;
  private static final boolean vSyncLimit = Boolean.valueOf((String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.vsynclimit", "true"))).booleanValue();
  
  public VSyncedBSManager() {}
  
  private static VSyncedBSManager getInstance(boolean paramBoolean)
  {
    if ((theInstance == null) && (paramBoolean)) {
      theInstance = vSyncLimit ? new SingleVSyncedBSMgr(null) : new NoLimitVSyncBSMgr(null);
    }
    return theInstance;
  }
  
  abstract boolean checkAllowed(BufferStrategy paramBufferStrategy);
  
  abstract void relinquishVsync(BufferStrategy paramBufferStrategy);
  
  public static boolean vsyncAllowed(BufferStrategy paramBufferStrategy)
  {
    VSyncedBSManager localVSyncedBSManager = getInstance(true);
    return localVSyncedBSManager.checkAllowed(paramBufferStrategy);
  }
  
  public static synchronized void releaseVsync(BufferStrategy paramBufferStrategy)
  {
    VSyncedBSManager localVSyncedBSManager = getInstance(false);
    if (localVSyncedBSManager != null) {
      localVSyncedBSManager.relinquishVsync(paramBufferStrategy);
    }
  }
  
  private static final class NoLimitVSyncBSMgr
    extends VSyncedBSManager
  {
    private NoLimitVSyncBSMgr() {}
    
    boolean checkAllowed(BufferStrategy paramBufferStrategy)
    {
      return true;
    }
    
    void relinquishVsync(BufferStrategy paramBufferStrategy) {}
  }
  
  private static final class SingleVSyncedBSMgr
    extends VSyncedBSManager
  {
    private WeakReference<BufferStrategy> strategy;
    
    private SingleVSyncedBSMgr() {}
    
    public synchronized boolean checkAllowed(BufferStrategy paramBufferStrategy)
    {
      if (strategy != null)
      {
        BufferStrategy localBufferStrategy = (BufferStrategy)strategy.get();
        if (localBufferStrategy != null) {
          return localBufferStrategy == paramBufferStrategy;
        }
      }
      strategy = new WeakReference(paramBufferStrategy);
      return true;
    }
    
    public synchronized void relinquishVsync(BufferStrategy paramBufferStrategy)
    {
      if (strategy != null)
      {
        BufferStrategy localBufferStrategy = (BufferStrategy)strategy.get();
        if (localBufferStrategy == paramBufferStrategy)
        {
          strategy.clear();
          strategy = null;
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\image\VSyncedBSManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */