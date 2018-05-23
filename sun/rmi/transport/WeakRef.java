package sun.rmi.transport;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import sun.rmi.runtime.Log;

class WeakRef
  extends WeakReference<Object>
{
  private int hashValue;
  private Object strongRef = null;
  
  public WeakRef(Object paramObject)
  {
    super(paramObject);
    setHashValue(paramObject);
  }
  
  public WeakRef(Object paramObject, ReferenceQueue<Object> paramReferenceQueue)
  {
    super(paramObject, paramReferenceQueue);
    setHashValue(paramObject);
  }
  
  public synchronized void pin()
  {
    if (strongRef == null)
    {
      strongRef = get();
      if (DGCImpl.dgcLog.isLoggable(Log.VERBOSE)) {
        DGCImpl.dgcLog.log(Log.VERBOSE, "strongRef = " + strongRef);
      }
    }
  }
  
  public synchronized void unpin()
  {
    if (strongRef != null)
    {
      if (DGCImpl.dgcLog.isLoggable(Log.VERBOSE)) {
        DGCImpl.dgcLog.log(Log.VERBOSE, "strongRef = " + strongRef);
      }
      strongRef = null;
    }
  }
  
  private void setHashValue(Object paramObject)
  {
    if (paramObject != null) {
      hashValue = System.identityHashCode(paramObject);
    } else {
      hashValue = 0;
    }
  }
  
  public int hashCode()
  {
    return hashValue;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof WeakRef))
    {
      if (paramObject == this) {
        return true;
      }
      Object localObject = get();
      return (localObject != null) && (localObject == ((WeakRef)paramObject).get());
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\transport\WeakRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */