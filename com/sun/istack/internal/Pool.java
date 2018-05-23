package com.sun.istack.internal;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract interface Pool<T>
{
  @NotNull
  public abstract T take();
  
  public abstract void recycle(@NotNull T paramT);
  
  public static abstract class Impl<T>
    implements Pool<T>
  {
    private volatile WeakReference<ConcurrentLinkedQueue<T>> queue;
    
    public Impl() {}
    
    @NotNull
    public final T take()
    {
      Object localObject = getQueue().poll();
      if (localObject == null) {
        return (T)create();
      }
      return (T)localObject;
    }
    
    public final void recycle(T paramT)
    {
      getQueue().offer(paramT);
    }
    
    private ConcurrentLinkedQueue<T> getQueue()
    {
      WeakReference localWeakReference = queue;
      if (localWeakReference != null)
      {
        localConcurrentLinkedQueue = (ConcurrentLinkedQueue)localWeakReference.get();
        if (localConcurrentLinkedQueue != null) {
          return localConcurrentLinkedQueue;
        }
      }
      ConcurrentLinkedQueue localConcurrentLinkedQueue = new ConcurrentLinkedQueue();
      queue = new WeakReference(localConcurrentLinkedQueue);
      return localConcurrentLinkedQueue;
    }
    
    @NotNull
    protected abstract T create();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\istack\internal\Pool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */