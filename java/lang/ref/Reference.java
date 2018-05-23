package java.lang.ref;

import sun.misc.Cleaner;
import sun.misc.JavaLangRefAccess;
import sun.misc.SharedSecrets;

public abstract class Reference<T>
{
  private T referent;
  volatile ReferenceQueue<? super T> queue;
  Reference next;
  private transient Reference<T> discovered;
  private static Lock lock = new Lock(null);
  private static Reference<Object> pending = null;
  
  static boolean tryHandlePending(boolean paramBoolean)
  {
    Reference localReference;
    Object localObject1;
    try
    {
      synchronized (lock)
      {
        if (pending != null)
        {
          localReference = pending;
          localObject1 = (localReference instanceof Cleaner) ? (Cleaner)localReference : null;
          pending = discovered;
          discovered = null;
        }
        else
        {
          if (paramBoolean) {
            lock.wait();
          }
          return paramBoolean;
        }
      }
    }
    catch (OutOfMemoryError localOutOfMemoryError)
    {
      Thread.yield();
      return true;
    }
    catch (InterruptedException localInterruptedException)
    {
      return true;
    }
    if (localObject1 != null)
    {
      ((Cleaner)localObject1).clean();
      return true;
    }
    ReferenceQueue localReferenceQueue = queue;
    if (localReferenceQueue != ReferenceQueue.NULL) {
      localReferenceQueue.enqueue(localReference);
    }
    return true;
  }
  
  public T get()
  {
    return (T)referent;
  }
  
  public void clear()
  {
    referent = null;
  }
  
  public boolean isEnqueued()
  {
    return queue == ReferenceQueue.ENQUEUED;
  }
  
  public boolean enqueue()
  {
    return queue.enqueue(this);
  }
  
  Reference(T paramT)
  {
    this(paramT, null);
  }
  
  Reference(T paramT, ReferenceQueue<? super T> paramReferenceQueue)
  {
    referent = paramT;
    queue = (paramReferenceQueue == null ? ReferenceQueue.NULL : paramReferenceQueue);
  }
  
  static
  {
    Object localObject1 = Thread.currentThread().getThreadGroup();
    for (Object localObject2 = localObject1; localObject2 != null; localObject2 = ((ThreadGroup)localObject1).getParent()) {
      localObject1 = localObject2;
    }
    localObject2 = new ReferenceHandler((ThreadGroup)localObject1, "Reference Handler");
    ((Thread)localObject2).setPriority(10);
    ((Thread)localObject2).setDaemon(true);
    ((Thread)localObject2).start();
    SharedSecrets.setJavaLangRefAccess(new JavaLangRefAccess()
    {
      public boolean tryHandlePendingReference()
      {
        return Reference.tryHandlePending(false);
      }
    });
  }
  
  private static class Lock
  {
    private Lock() {}
  }
  
  private static class ReferenceHandler
    extends Thread
  {
    private static void ensureClassInitialized(Class<?> paramClass)
    {
      try
      {
        Class.forName(paramClass.getName(), true, paramClass.getClassLoader());
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        throw ((Error)new NoClassDefFoundError(localClassNotFoundException.getMessage()).initCause(localClassNotFoundException));
      }
    }
    
    ReferenceHandler(ThreadGroup paramThreadGroup, String paramString)
    {
      super(paramString);
    }
    
    public void run()
    {
      for (;;)
      {
        Reference.tryHandlePending(true);
      }
    }
    
    static
    {
      ensureClassInitialized(InterruptedException.class);
      ensureClassInitialized(Cleaner.class);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\ref\Reference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */