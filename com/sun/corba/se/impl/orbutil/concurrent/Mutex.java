package com.sun.corba.se.impl.orbutil.concurrent;

public class Mutex
  implements Sync
{
  protected boolean inuse_ = false;
  
  public Mutex() {}
  
  public void acquire()
    throws InterruptedException
  {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    synchronized (this)
    {
      try
      {
        while (inuse_) {
          wait();
        }
        inuse_ = true;
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
      if (!inuse_)
      {
        inuse_ = true;
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orbutil\concurrent\Mutex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */