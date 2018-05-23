package com.sun.corba.se.impl.orbutil.concurrent;

import com.sun.corba.se.impl.orbutil.ORBUtility;

public class CondVar
{
  protected boolean debug_;
  protected final Sync mutex_;
  protected final ReentrantMutex remutex_;
  
  private int releaseMutex()
  {
    int i = 1;
    if (remutex_ != null) {
      i = remutex_.releaseAll();
    } else {
      mutex_.release();
    }
    return i;
  }
  
  private void acquireMutex(int paramInt)
    throws InterruptedException
  {
    if (remutex_ != null) {
      remutex_.acquireAll(paramInt);
    } else {
      mutex_.acquire();
    }
  }
  
  public CondVar(Sync paramSync, boolean paramBoolean)
  {
    debug_ = paramBoolean;
    mutex_ = paramSync;
    if ((paramSync instanceof ReentrantMutex)) {
      remutex_ = ((ReentrantMutex)paramSync);
    } else {
      remutex_ = null;
    }
  }
  
  public CondVar(Sync paramSync)
  {
    this(paramSync, false);
  }
  
  public void await()
    throws InterruptedException
  {
    int i = 0;
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    try
    {
      if (debug_) {
        ORBUtility.dprintTrace(this, "await enter");
      }
      synchronized (this)
      {
        i = releaseMutex();
        try
        {
          wait();
        }
        catch (InterruptedException localInterruptedException1)
        {
          notify();
          throw localInterruptedException1;
        }
      }
    }
    finally
    {
      int j;
      int k = 0;
      for (;;)
      {
        try
        {
          acquireMutex(i);
        }
        catch (InterruptedException localInterruptedException3)
        {
          k = 1;
        }
      }
      if (k != 0) {
        Thread.currentThread().interrupt();
      }
      if (debug_) {
        ORBUtility.dprintTrace(this, "await exit");
      }
    }
  }
  
  public boolean timedwait(long paramLong)
    throws InterruptedException
  {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    boolean bool = false;
    int i = 0;
    try
    {
      if (debug_) {
        ORBUtility.dprintTrace(this, "timedwait enter");
      }
      synchronized (this)
      {
        i = releaseMutex();
        try
        {
          if (paramLong > 0L)
          {
            long l = System.currentTimeMillis();
            wait(paramLong);
            bool = System.currentTimeMillis() - l <= paramLong;
          }
        }
        catch (InterruptedException localInterruptedException1)
        {
          notify();
          throw localInterruptedException1;
        }
      }
    }
    finally
    {
      int j;
      int k = 0;
      for (;;)
      {
        try
        {
          acquireMutex(i);
        }
        catch (InterruptedException localInterruptedException3)
        {
          k = 1;
        }
      }
      if (k != 0) {
        Thread.currentThread().interrupt();
      }
      if (debug_) {
        ORBUtility.dprintTrace(this, "timedwait exit");
      }
    }
    return bool;
  }
  
  public synchronized void signal()
  {
    notify();
  }
  
  public synchronized void broadcast()
  {
    notifyAll();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orbutil\concurrent\CondVar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */