package java.lang.ref;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;
import sun.misc.VM;

final class Finalizer
  extends FinalReference<Object>
{
  private static ReferenceQueue<Object> queue = new ReferenceQueue();
  private static Finalizer unfinalized = null;
  private static final Object lock = new Object();
  private Finalizer next = null;
  private Finalizer prev = null;
  
  private boolean hasBeenFinalized()
  {
    return next == this;
  }
  
  private void add()
  {
    synchronized (lock)
    {
      if (unfinalized != null)
      {
        next = unfinalized;
        unfinalizedprev = this;
      }
      unfinalized = this;
    }
  }
  
  private void remove()
  {
    synchronized (lock)
    {
      if (unfinalized == this) {
        if (next != null) {
          unfinalized = next;
        } else {
          unfinalized = prev;
        }
      }
      if (next != null) {
        next.prev = prev;
      }
      if (prev != null) {
        prev.next = next;
      }
      next = this;
      prev = this;
    }
  }
  
  private Finalizer(Object paramObject)
  {
    super(paramObject, queue);
    add();
  }
  
  static void register(Object paramObject)
  {
    new Finalizer(paramObject);
  }
  
  private void runFinalizer(JavaLangAccess paramJavaLangAccess)
  {
    synchronized (this)
    {
      if (hasBeenFinalized()) {
        return;
      }
      remove();
    }
    try
    {
      ??? = get();
      if ((??? != null) && (!(??? instanceof Enum)))
      {
        paramJavaLangAccess.invokeFinalize(???);
        ??? = null;
      }
    }
    catch (Throwable localThrowable) {}
    super.clear();
  }
  
  private static void forkSecondaryFinalizer(Runnable paramRunnable)
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        Object localObject1 = Thread.currentThread().getThreadGroup();
        for (Object localObject2 = localObject1; localObject2 != null; localObject2 = ((ThreadGroup)localObject1).getParent()) {
          localObject1 = localObject2;
        }
        localObject2 = new Thread((ThreadGroup)localObject1, val$proc, "Secondary finalizer");
        ((Thread)localObject2).start();
        try
        {
          ((Thread)localObject2).join();
        }
        catch (InterruptedException localInterruptedException) {}
        return null;
      }
    });
  }
  
  static void runFinalization()
  {
    if (!VM.isBooted()) {
      return;
    }
    forkSecondaryFinalizer(new Runnable()
    {
      private volatile boolean running;
      
      public void run()
      {
        if (running) {
          return;
        }
        JavaLangAccess localJavaLangAccess = SharedSecrets.getJavaLangAccess();
        running = true;
        for (;;)
        {
          Finalizer localFinalizer = (Finalizer)Finalizer.queue.poll();
          if (localFinalizer == null) {
            break;
          }
          localFinalizer.runFinalizer(localJavaLangAccess);
        }
      }
    });
  }
  
  static void runAllFinalizers()
  {
    if (!VM.isBooted()) {
      return;
    }
    forkSecondaryFinalizer(new Runnable()
    {
      private volatile boolean running;
      
      public void run()
      {
        if (running) {
          return;
        }
        JavaLangAccess localJavaLangAccess = SharedSecrets.getJavaLangAccess();
        running = true;
        for (;;)
        {
          Finalizer localFinalizer;
          synchronized (Finalizer.lock)
          {
            localFinalizer = Finalizer.unfinalized;
            if (localFinalizer == null) {
              break;
            }
            Finalizer.access$302(next);
          }
          localFinalizer.runFinalizer(localJavaLangAccess);
        }
      }
    });
  }
  
  static
  {
    Object localObject1 = Thread.currentThread().getThreadGroup();
    for (Object localObject2 = localObject1; localObject2 != null; localObject2 = ((ThreadGroup)localObject1).getParent()) {
      localObject1 = localObject2;
    }
    localObject2 = new FinalizerThread((ThreadGroup)localObject1);
    ((Thread)localObject2).setPriority(8);
    ((Thread)localObject2).setDaemon(true);
    ((Thread)localObject2).start();
  }
  
  private static class FinalizerThread
    extends Thread
  {
    private volatile boolean running;
    
    FinalizerThread(ThreadGroup paramThreadGroup)
    {
      super("Finalizer");
    }
    
    public void run()
    {
      if (running) {
        return;
      }
      while (!VM.isBooted()) {
        try
        {
          VM.awaitBooted();
        }
        catch (InterruptedException localInterruptedException1) {}
      }
      JavaLangAccess localJavaLangAccess = SharedSecrets.getJavaLangAccess();
      running = true;
      try
      {
        for (;;)
        {
          Finalizer localFinalizer = (Finalizer)Finalizer.queue.remove();
          localFinalizer.runFinalizer(localJavaLangAccess);
        }
      }
      catch (InterruptedException localInterruptedException2) {}
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\ref\Finalizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */