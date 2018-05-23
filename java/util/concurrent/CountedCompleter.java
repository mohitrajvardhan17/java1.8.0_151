package java.util.concurrent;

import sun.misc.Unsafe;

public abstract class CountedCompleter<T>
  extends ForkJoinTask<T>
{
  private static final long serialVersionUID = 5232453752276485070L;
  final CountedCompleter<?> completer;
  volatile int pending;
  private static final Unsafe U;
  private static final long PENDING;
  
  protected CountedCompleter(CountedCompleter<?> paramCountedCompleter, int paramInt)
  {
    completer = paramCountedCompleter;
    pending = paramInt;
  }
  
  protected CountedCompleter(CountedCompleter<?> paramCountedCompleter)
  {
    completer = paramCountedCompleter;
  }
  
  protected CountedCompleter()
  {
    completer = null;
  }
  
  public abstract void compute();
  
  public void onCompletion(CountedCompleter<?> paramCountedCompleter) {}
  
  public boolean onExceptionalCompletion(Throwable paramThrowable, CountedCompleter<?> paramCountedCompleter)
  {
    return true;
  }
  
  public final CountedCompleter<?> getCompleter()
  {
    return completer;
  }
  
  public final int getPendingCount()
  {
    return pending;
  }
  
  public final void setPendingCount(int paramInt)
  {
    pending = paramInt;
  }
  
  public final void addToPendingCount(int paramInt)
  {
    U.getAndAddInt(this, PENDING, paramInt);
  }
  
  public final boolean compareAndSetPendingCount(int paramInt1, int paramInt2)
  {
    return U.compareAndSwapInt(this, PENDING, paramInt1, paramInt2);
  }
  
  public final int decrementPendingCountUnlessZero()
  {
    int i;
    while (((i = pending) != 0) && (!U.compareAndSwapInt(this, PENDING, i, i - 1))) {}
    return i;
  }
  
  public final CountedCompleter<?> getRoot()
  {
    CountedCompleter localCountedCompleter;
    for (Object localObject = this; (localCountedCompleter = completer) != null; localObject = localCountedCompleter) {}
    return (CountedCompleter<?>)localObject;
  }
  
  public final void tryComplete()
  {
    CountedCompleter localCountedCompleter1 = this;
    CountedCompleter localCountedCompleter2 = localCountedCompleter1;
    int i;
    do
    {
      while ((i = pending) == 0)
      {
        localCountedCompleter1.onCompletion(localCountedCompleter2);
        if ((localCountedCompleter1 = completer) == null)
        {
          localCountedCompleter2.quietlyComplete();
          return;
        }
      }
    } while (!U.compareAndSwapInt(localCountedCompleter1, PENDING, i, i - 1));
  }
  
  public final void propagateCompletion()
  {
    CountedCompleter localCountedCompleter1 = this;
    CountedCompleter localCountedCompleter2 = localCountedCompleter1;
    int i;
    do
    {
      while ((i = pending) == 0) {
        if ((localCountedCompleter1 = completer) == null)
        {
          localCountedCompleter2.quietlyComplete();
          return;
        }
      }
    } while (!U.compareAndSwapInt(localCountedCompleter1, PENDING, i, i - 1));
  }
  
  public void complete(T paramT)
  {
    setRawResult(paramT);
    onCompletion(this);
    quietlyComplete();
    CountedCompleter localCountedCompleter;
    if ((localCountedCompleter = completer) != null) {
      localCountedCompleter.tryComplete();
    }
  }
  
  public final CountedCompleter<?> firstComplete()
  {
    int i;
    do
    {
      if ((i = pending) == 0) {
        return this;
      }
    } while (!U.compareAndSwapInt(this, PENDING, i, i - 1));
    return null;
  }
  
  public final CountedCompleter<?> nextComplete()
  {
    CountedCompleter localCountedCompleter;
    if ((localCountedCompleter = completer) != null) {
      return localCountedCompleter.firstComplete();
    }
    quietlyComplete();
    return null;
  }
  
  public final void quietlyCompleteRoot()
  {
    CountedCompleter localCountedCompleter;
    for (Object localObject = this;; localObject = localCountedCompleter) {
      if ((localCountedCompleter = completer) == null)
      {
        ((CountedCompleter)localObject).quietlyComplete();
        return;
      }
    }
  }
  
  public final void helpComplete(int paramInt)
  {
    if ((paramInt > 0) && (status >= 0))
    {
      Thread localThread;
      if (((localThread = Thread.currentThread()) instanceof ForkJoinWorkerThread))
      {
        ForkJoinWorkerThread localForkJoinWorkerThread;
        pool.helpComplete(workQueue, this, paramInt);
      }
      else
      {
        ForkJoinPool.common.externalHelpComplete(this, paramInt);
      }
    }
  }
  
  void internalPropagateException(Throwable paramThrowable)
  {
    CountedCompleter localCountedCompleter1 = this;
    CountedCompleter localCountedCompleter2 = localCountedCompleter1;
    while ((localCountedCompleter1.onExceptionalCompletion(paramThrowable, localCountedCompleter2)) && ((localCountedCompleter1 = completer) != null) && (status >= 0) && (localCountedCompleter1.recordExceptionalCompletion(paramThrowable) == Integer.MIN_VALUE)) {}
  }
  
  protected final boolean exec()
  {
    compute();
    return false;
  }
  
  public T getRawResult()
  {
    return null;
  }
  
  protected void setRawResult(T paramT) {}
  
  static
  {
    try
    {
      U = Unsafe.getUnsafe();
      PENDING = U.objectFieldOffset(CountedCompleter.class.getDeclaredField("pending"));
    }
    catch (Exception localException)
    {
      throw new Error(localException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\CountedCompleter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */