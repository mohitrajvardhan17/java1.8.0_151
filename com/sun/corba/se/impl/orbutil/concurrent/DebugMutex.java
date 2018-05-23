package com.sun.corba.se.impl.orbutil.concurrent;

import org.omg.CORBA.INTERNAL;

public class DebugMutex
  implements Sync
{
  protected boolean inuse_ = false;
  protected Thread holder_ = null;
  
  public DebugMutex() {}
  
  public void acquire()
    throws InterruptedException
  {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    synchronized (this)
    {
      Thread localThread = Thread.currentThread();
      if (holder_ == localThread) {
        throw new INTERNAL("Attempt to acquire Mutex by thread holding the Mutex");
      }
      try
      {
        while (inuse_) {
          wait();
        }
        inuse_ = true;
        holder_ = Thread.currentThread();
      }
      catch (InterruptedException localInterruptedException)
      {
        notify();
        throw localInterruptedException;
      }
    }
  }
  
  public synchronized void release()
  {
    Thread localThread = Thread.currentThread();
    if (localThread != holder_) {
      throw new INTERNAL("Attempt to release Mutex by thread not holding the Mutex");
    }
    holder_ = null;
    inuse_ = false;
    notify();
  }
  
  public boolean attempt(long paramLong)
    throws InterruptedException
  {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    synchronized (this)
    {
      Thread localThread = Thread.currentThread();
      if (!inuse_)
      {
        inuse_ = true;
        holder_ = localThread;
        return true;
      }
      if (paramLong <= 0L) {
        return false;
      }
      long l1 = paramLong;
      long l2 = System.currentTimeMillis();
      try
      {
        do
        {
          wait(l1);
          if (!inuse_)
          {
            inuse_ = true;
            holder_ = localThread;
            return true;
          }
          l1 = paramLong - (System.currentTimeMillis() - l2);
        } while (l1 > 0L);
        return false;
      }
      catch (InterruptedException localInterruptedException)
      {
        notify();
        throw localInterruptedException;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orbutil\concurrent\DebugMutex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */