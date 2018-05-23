package java.lang;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Set;

class ApplicationShutdownHooks
{
  private static IdentityHashMap<Thread, Thread> hooks;
  
  private ApplicationShutdownHooks() {}
  
  static synchronized void add(Thread paramThread)
  {
    if (hooks == null) {
      throw new IllegalStateException("Shutdown in progress");
    }
    if (paramThread.isAlive()) {
      throw new IllegalArgumentException("Hook already running");
    }
    if (hooks.containsKey(paramThread)) {
      throw new IllegalArgumentException("Hook previously registered");
    }
    hooks.put(paramThread, paramThread);
  }
  
  static synchronized boolean remove(Thread paramThread)
  {
    if (hooks == null) {
      throw new IllegalStateException("Shutdown in progress");
    }
    if (paramThread == null) {
      throw new NullPointerException();
    }
    return hooks.remove(paramThread) != null;
  }
  
  static void runHooks()
  {
    Set localSet;
    synchronized (ApplicationShutdownHooks.class)
    {
      localSet = hooks.keySet();
      hooks = null;
    }
    ??? = localSet.iterator();
    Thread localThread;
    while (((Iterator)???).hasNext())
    {
      localThread = (Thread)((Iterator)???).next();
      localThread.start();
    }
    ??? = localSet.iterator();
    while (((Iterator)???).hasNext())
    {
      localThread = (Thread)((Iterator)???).next();
      try
      {
        localThread.join();
      }
      catch (InterruptedException localInterruptedException) {}
    }
  }
  
  static
  {
    try
    {
      Shutdown.add(1, false, new Runnable()
      {
        public void run() {}
      });
      hooks = new IdentityHashMap();
    }
    catch (IllegalStateException localIllegalStateException)
    {
      hooks = null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\ApplicationShutdownHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */