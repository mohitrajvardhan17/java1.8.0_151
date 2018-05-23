package java.lang;

class Shutdown
{
  private static final int RUNNING = 0;
  private static final int HOOKS = 1;
  private static final int FINALIZERS = 2;
  private static int state = 0;
  private static boolean runFinalizersOnExit = false;
  private static final int MAX_SYSTEM_HOOKS = 10;
  private static final Runnable[] hooks = new Runnable[10];
  private static int currentRunningHook = 0;
  private static Object lock = new Lock(null);
  private static Object haltLock = new Lock(null);
  
  Shutdown() {}
  
  static void setRunFinalizersOnExit(boolean paramBoolean)
  {
    synchronized (lock)
    {
      runFinalizersOnExit = paramBoolean;
    }
  }
  
  static void add(int paramInt, boolean paramBoolean, Runnable paramRunnable)
  {
    synchronized (lock)
    {
      if (hooks[paramInt] != null) {
        throw new InternalError("Shutdown hook at slot " + paramInt + " already registered");
      }
      if (!paramBoolean)
      {
        if (state > 0) {
          throw new IllegalStateException("Shutdown in progress");
        }
      }
      else if ((state > 1) || ((state == 1) && (paramInt <= currentRunningHook))) {
        throw new IllegalStateException("Shutdown in progress");
      }
      hooks[paramInt] = paramRunnable;
    }
  }
  
  private static void runHooks()
  {
    for (int i = 0; i < 10; i++) {
      try
      {
        Runnable localRunnable;
        synchronized (lock)
        {
          currentRunningHook = i;
          localRunnable = hooks[i];
        }
        if (localRunnable != null) {
          localRunnable.run();
        }
      }
      catch (Throwable localThrowable)
      {
        if ((localThrowable instanceof ThreadDeath))
        {
          ??? = (ThreadDeath)localThrowable;
          throw ((Throwable)???);
        }
      }
    }
  }
  
  static void halt(int paramInt)
  {
    synchronized (haltLock)
    {
      halt0(paramInt);
    }
  }
  
  static native void halt0(int paramInt);
  
  private static native void runAllFinalizers();
  
  private static void sequence()
  {
    synchronized (lock)
    {
      if (state != 1) {
        return;
      }
    }
    runHooks();
    boolean bool;
    synchronized (lock)
    {
      state = 2;
      bool = runFinalizersOnExit;
    }
    if (bool) {
      runAllFinalizers();
    }
  }
  
  static void exit(int paramInt)
  {
    boolean bool = false;
    synchronized (lock)
    {
      if (paramInt != 0) {
        runFinalizersOnExit = false;
      }
      switch (state)
      {
      case 0: 
        state = 1;
        break;
      case 1: 
        break;
      case 2: 
        if (paramInt != 0) {
          halt(paramInt);
        } else {
          bool = runFinalizersOnExit;
        }
        break;
      }
    }
    if (bool)
    {
      runAllFinalizers();
      halt(paramInt);
    }
    synchronized (Shutdown.class)
    {
      sequence();
      halt(paramInt);
    }
  }
  
  static void shutdown()
  {
    synchronized (lock)
    {
      switch (state)
      {
      case 0: 
        state = 1;
      }
    }
    synchronized (Shutdown.class)
    {
      sequence();
    }
  }
  
  private static class Lock
  {
    private Lock() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\Shutdown.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */