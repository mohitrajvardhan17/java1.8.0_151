package com.sun.org.apache.xml.internal.utils;

public class ThreadControllerWrapper
{
  private static ThreadController m_tpool = new ThreadController();
  
  public ThreadControllerWrapper() {}
  
  public static Thread runThread(Runnable paramRunnable, int paramInt)
  {
    return m_tpool.run(paramRunnable, paramInt);
  }
  
  public static void waitThread(Thread paramThread, Runnable paramRunnable)
    throws InterruptedException
  {
    m_tpool.waitThread(paramThread, paramRunnable);
  }
  
  public static class ThreadController
  {
    public ThreadController() {}
    
    public Thread run(Runnable paramRunnable, int paramInt)
    {
      SafeThread localSafeThread = new SafeThread(paramRunnable);
      localSafeThread.start();
      return localSafeThread;
    }
    
    public void waitThread(Thread paramThread, Runnable paramRunnable)
      throws InterruptedException
    {
      paramThread.join();
    }
    
    final class SafeThread
      extends Thread
    {
      private volatile boolean ran = false;
      
      public SafeThread(Runnable paramRunnable)
      {
        super();
      }
      
      public final void run()
      {
        if (Thread.currentThread() != this) {
          throw new IllegalStateException("The run() method in a SafeThread cannot be called from another thread.");
        }
        synchronized (this)
        {
          if (!ran) {
            ran = true;
          } else {
            throw new IllegalStateException("The run() method in a SafeThread cannot be called more than once.");
          }
        }
        super.run();
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\ThreadControllerWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */