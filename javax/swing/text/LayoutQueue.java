package javax.swing.text;

import java.util.Vector;
import sun.awt.AppContext;

public class LayoutQueue
{
  private static final Object DEFAULT_QUEUE = new Object();
  private Vector<Runnable> tasks = new Vector();
  private Thread worker;
  
  public LayoutQueue() {}
  
  public static LayoutQueue getDefaultQueue()
  {
    AppContext localAppContext = AppContext.getAppContext();
    synchronized (DEFAULT_QUEUE)
    {
      LayoutQueue localLayoutQueue = (LayoutQueue)localAppContext.get(DEFAULT_QUEUE);
      if (localLayoutQueue == null)
      {
        localLayoutQueue = new LayoutQueue();
        localAppContext.put(DEFAULT_QUEUE, localLayoutQueue);
      }
      return localLayoutQueue;
    }
  }
  
  public static void setDefaultQueue(LayoutQueue paramLayoutQueue)
  {
    synchronized (DEFAULT_QUEUE)
    {
      AppContext.getAppContext().put(DEFAULT_QUEUE, paramLayoutQueue);
    }
  }
  
  public synchronized void addTask(Runnable paramRunnable)
  {
    if (worker == null)
    {
      worker = new LayoutThread();
      worker.start();
    }
    tasks.addElement(paramRunnable);
    notifyAll();
  }
  
  protected synchronized Runnable waitForWork()
  {
    while (tasks.size() == 0) {
      try
      {
        wait();
      }
      catch (InterruptedException localInterruptedException)
      {
        return null;
      }
    }
    Runnable localRunnable = (Runnable)tasks.firstElement();
    tasks.removeElementAt(0);
    return localRunnable;
  }
  
  class LayoutThread
    extends Thread
  {
    LayoutThread()
    {
      super();
      setPriority(1);
    }
    
    public void run()
    {
      Runnable localRunnable;
      do
      {
        localRunnable = waitForWork();
        if (localRunnable != null) {
          localRunnable.run();
        }
      } while (localRunnable != null);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\LayoutQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */