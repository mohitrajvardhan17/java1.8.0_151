package java.lang.ref;

import sun.misc.VM;

public class ReferenceQueue<T>
{
  static ReferenceQueue<Object> NULL = new Null(null);
  static ReferenceQueue<Object> ENQUEUED = new Null(null);
  private Lock lock = new Lock(null);
  private volatile Reference<? extends T> head = null;
  private long queueLength = 0L;
  
  public ReferenceQueue() {}
  
  boolean enqueue(Reference<? extends T> paramReference)
  {
    synchronized (lock)
    {
      ReferenceQueue localReferenceQueue = queue;
      if ((localReferenceQueue == NULL) || (localReferenceQueue == ENQUEUED)) {
        return false;
      }
      assert (localReferenceQueue == this);
      queue = ENQUEUED;
      next = (head == null ? paramReference : head);
      head = paramReference;
      queueLength += 1L;
      if ((paramReference instanceof FinalReference)) {
        VM.addFinalRefCount(1);
      }
      lock.notifyAll();
      return true;
    }
  }
  
  private Reference<? extends T> reallyPoll()
  {
    Reference localReference = head;
    if (localReference != null)
    {
      head = (next == localReference ? null : next);
      queue = NULL;
      next = localReference;
      queueLength -= 1L;
      if ((localReference instanceof FinalReference)) {
        VM.addFinalRefCount(-1);
      }
      return localReference;
    }
    return null;
  }
  
  /* Error */
  public Reference<? extends T> poll()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 101	java/lang/ref/ReferenceQueue:head	Ljava/lang/ref/Reference;
    //   4: ifnonnull +5 -> 9
    //   7: aconst_null
    //   8: areturn
    //   9: aload_0
    //   10: getfield 104	java/lang/ref/ReferenceQueue:lock	Ljava/lang/ref/ReferenceQueue$Lock;
    //   13: dup
    //   14: astore_1
    //   15: monitorenter
    //   16: aload_0
    //   17: invokespecial 112	java/lang/ref/ReferenceQueue:reallyPoll	()Ljava/lang/ref/Reference;
    //   20: aload_1
    //   21: monitorexit
    //   22: areturn
    //   23: astore_2
    //   24: aload_1
    //   25: monitorexit
    //   26: aload_2
    //   27: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	28	0	this	ReferenceQueue
    //   14	11	1	Ljava/lang/Object;	Object
    //   23	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   16	22	23	finally
    //   23	26	23	finally
  }
  
  public Reference<? extends T> remove(long paramLong)
    throws IllegalArgumentException, InterruptedException
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("Negative timeout value");
    }
    synchronized (lock)
    {
      Reference localReference = reallyPoll();
      if (localReference != null) {
        return localReference;
      }
      long l1 = paramLong == 0L ? 0L : System.nanoTime();
      do
      {
        lock.wait(paramLong);
        localReference = reallyPoll();
        if (localReference != null) {
          return localReference;
        }
      } while (paramLong == 0L);
      long l2 = System.nanoTime();
      paramLong -= (l2 - l1) / 1000000L;
      if (paramLong <= 0L) {
        return null;
      }
      l1 = l2;
    }
  }
  
  public Reference<? extends T> remove()
    throws InterruptedException
  {
    return remove(0L);
  }
  
  private static class Lock
  {
    private Lock() {}
  }
  
  private static class Null<S>
    extends ReferenceQueue<S>
  {
    private Null() {}
    
    boolean enqueue(Reference<? extends S> paramReference)
    {
      return false;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\ref\ReferenceQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */