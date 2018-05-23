package java.util.concurrent.locks;

import java.util.concurrent.TimeUnit;

public abstract interface Lock
{
  public abstract void lock();
  
  public abstract void lockInterruptibly()
    throws InterruptedException;
  
  public abstract boolean tryLock();
  
  public abstract boolean tryLock(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException;
  
  public abstract void unlock();
  
  public abstract Condition newCondition();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\locks\Lock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */