package java.util.concurrent;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CyclicBarrier
{
  private final ReentrantLock lock = new ReentrantLock();
  private final Condition trip = lock.newCondition();
  private final int parties;
  private final Runnable barrierCommand;
  private Generation generation = new Generation(null);
  private int count;
  
  private void nextGeneration()
  {
    trip.signalAll();
    count = parties;
    generation = new Generation(null);
  }
  
  private void breakBarrier()
  {
    generation.broken = true;
    count = parties;
    trip.signalAll();
  }
  
  private int dowait(boolean paramBoolean, long paramLong)
    throws InterruptedException, BrokenBarrierException, TimeoutException
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      Generation localGeneration = generation;
      if (broken) {
        throw new BrokenBarrierException();
      }
      if (Thread.interrupted())
      {
        breakBarrier();
        throw new InterruptedException();
      }
      InterruptedException localInterruptedException1 = --count;
      if (localInterruptedException1 == 0)
      {
        int i = 0;
        try
        {
          Runnable localRunnable = barrierCommand;
          if (localRunnable != null) {
            localRunnable.run();
          }
          i = 1;
          nextGeneration();
          int j = 0;
          if (i == 0) {
            breakBarrier();
          }
          return j;
        }
        finally
        {
          if (i == 0) {
            breakBarrier();
          }
        }
      }
      do
      {
        try
        {
          if (!paramBoolean) {
            trip.await();
          } else if (paramLong > 0L) {
            paramLong = trip.awaitNanos(paramLong);
          }
        }
        catch (InterruptedException localInterruptedException2)
        {
          if ((localGeneration == generation) && (!broken))
          {
            breakBarrier();
            throw localInterruptedException2;
          }
          Thread.currentThread().interrupt();
        }
        if (broken) {
          throw new BrokenBarrierException();
        }
        if (localGeneration != generation)
        {
          localInterruptedException2 = localInterruptedException1;
          return localInterruptedException2;
        }
      } while ((!paramBoolean) || (paramLong > 0L));
      breakBarrier();
      throw new TimeoutException();
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public CyclicBarrier(int paramInt, Runnable paramRunnable)
  {
    if (paramInt <= 0) {
      throw new IllegalArgumentException();
    }
    parties = paramInt;
    count = paramInt;
    barrierCommand = paramRunnable;
  }
  
  public CyclicBarrier(int paramInt)
  {
    this(paramInt, null);
  }
  
  public int getParties()
  {
    return parties;
  }
  
  public int await()
    throws InterruptedException, BrokenBarrierException
  {
    try
    {
      return dowait(false, 0L);
    }
    catch (TimeoutException localTimeoutException)
    {
      throw new Error(localTimeoutException);
    }
  }
  
  public int await(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException, BrokenBarrierException, TimeoutException
  {
    return dowait(true, paramTimeUnit.toNanos(paramLong));
  }
  
  public boolean isBroken()
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      boolean bool = generation.broken;
      return bool;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  /* Error */
  public void reset()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 108	java/util/concurrent/CyclicBarrier:lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   4: astore_1
    //   5: aload_1
    //   6: invokevirtual 126	java/util/concurrent/locks/ReentrantLock:lock	()V
    //   9: aload_0
    //   10: invokespecial 118	java/util/concurrent/CyclicBarrier:breakBarrier	()V
    //   13: aload_0
    //   14: invokespecial 119	java/util/concurrent/CyclicBarrier:nextGeneration	()V
    //   17: aload_1
    //   18: invokevirtual 127	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   21: goto +10 -> 31
    //   24: astore_2
    //   25: aload_1
    //   26: invokevirtual 127	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   29: aload_2
    //   30: athrow
    //   31: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	32	0	this	CyclicBarrier
    //   4	22	1	localReentrantLock	ReentrantLock
    //   24	6	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   9	17	24	finally
  }
  
  public int getNumberWaiting()
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      int i = parties - count;
      return i;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  private static class Generation
  {
    boolean broken = false;
    
    private Generation() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\CyclicBarrier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */