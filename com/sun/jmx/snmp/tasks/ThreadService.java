package com.sun.jmx.snmp.tasks;

import java.util.ArrayList;

public class ThreadService
  implements TaskServer
{
  private ArrayList<Runnable> jobList = new ArrayList(0);
  private ExecutorThread[] threadList;
  private int minThreads = 1;
  private int currThreds = 0;
  private int idle = 0;
  private boolean terminated = false;
  private int priority;
  private ThreadGroup threadGroup = new ThreadGroup("ThreadService");
  private ClassLoader cloader;
  private static long counter = 0L;
  private int addedJobs = 1;
  private int doneJobs = 1;
  
  public ThreadService(int paramInt)
  {
    if (paramInt <= 0) {
      throw new IllegalArgumentException("The thread number should bigger than zero.");
    }
    minThreads = paramInt;
    threadList = new ExecutorThread[paramInt];
    priority = Thread.currentThread().getPriority();
    cloader = Thread.currentThread().getContextClassLoader();
  }
  
  public void submitTask(Task paramTask)
    throws IllegalArgumentException
  {
    submitTask(paramTask);
  }
  
  public void submitTask(Runnable paramRunnable)
    throws IllegalArgumentException
  {
    stateCheck();
    if (paramRunnable == null) {
      throw new IllegalArgumentException("No task specified.");
    }
    synchronized (jobList)
    {
      jobList.add(jobList.size(), paramRunnable);
      jobList.notify();
    }
    createThread();
  }
  
  public Runnable removeTask(Runnable paramRunnable)
  {
    stateCheck();
    Runnable localRunnable = null;
    synchronized (jobList)
    {
      int i = jobList.indexOf(paramRunnable);
      if (i >= 0) {
        localRunnable = (Runnable)jobList.remove(i);
      }
    }
    if ((localRunnable != null) && ((localRunnable instanceof Task))) {
      ((Task)localRunnable).cancel();
    }
    return localRunnable;
  }
  
  public void removeAll()
  {
    stateCheck();
    Object[] arrayOfObject;
    synchronized (jobList)
    {
      arrayOfObject = jobList.toArray();
      jobList.clear();
    }
    ??? = arrayOfObject.length;
    for (Object localObject2 = 0; localObject2 < ???; localObject2++)
    {
      Object localObject3 = arrayOfObject[localObject2];
      if ((localObject3 != null) && ((localObject3 instanceof Task))) {
        ((Task)localObject3).cancel();
      }
    }
  }
  
  public void terminate()
  {
    if (terminated == true) {
      return;
    }
    terminated = true;
    synchronized (jobList)
    {
      jobList.notifyAll();
    }
    removeAll();
    for (int i = 0; i < currThreds; i++) {
      try
      {
        threadList[i].interrupt();
      }
      catch (Exception localException) {}
    }
    threadList = null;
  }
  
  private void stateCheck()
    throws IllegalStateException
  {
    if (terminated) {
      throw new IllegalStateException("The thread service has been terminated.");
    }
  }
  
  private void createThread()
  {
    if (idle < 1) {
      synchronized (threadList)
      {
        if ((jobList.size() > 0) && (currThreds < minThreads))
        {
          ExecutorThread localExecutorThread = new ExecutorThread();
          localExecutorThread.start();
          threadList[(currThreds++)] = localExecutorThread;
        }
      }
    }
  }
  
  private class ExecutorThread
    extends Thread
  {
    public ExecutorThread()
    {
      super("ThreadService-" + ThreadService.access$108());
      setDaemon(true);
      setPriority(priority);
      setContextClassLoader(cloader);
      ThreadService.access$408(ThreadService.this);
    }
    
    public void run()
    {
      while (!terminated)
      {
        Runnable localRunnable = null;
        synchronized (jobList)
        {
          if (jobList.size() > 0)
          {
            localRunnable = (Runnable)jobList.remove(0);
            if (jobList.size() > 0) {
              jobList.notify();
            }
          }
          else
          {
            try
            {
              jobList.wait();
            }
            catch (InterruptedException localInterruptedException)
            {
              localInterruptedException = localInterruptedException;
            }
            finally {}
            continue;
          }
        }
        if (localRunnable != null) {
          try
          {
            ThreadService.access$410(ThreadService.this);
            localRunnable.run();
          }
          catch (Exception localException)
          {
            localException.printStackTrace();
          }
          finally
          {
            ThreadService.access$408(ThreadService.this);
          }
        }
        setPriority(priority);
        Thread.interrupted();
        setContextClassLoader(cloader);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\tasks\ThreadService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */