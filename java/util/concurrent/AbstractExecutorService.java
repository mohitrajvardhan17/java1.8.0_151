package java.util.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractExecutorService
  implements ExecutorService
{
  public AbstractExecutorService() {}
  
  protected <T> RunnableFuture<T> newTaskFor(Runnable paramRunnable, T paramT)
  {
    return new FutureTask(paramRunnable, paramT);
  }
  
  protected <T> RunnableFuture<T> newTaskFor(Callable<T> paramCallable)
  {
    return new FutureTask(paramCallable);
  }
  
  public Future<?> submit(Runnable paramRunnable)
  {
    if (paramRunnable == null) {
      throw new NullPointerException();
    }
    RunnableFuture localRunnableFuture = newTaskFor(paramRunnable, null);
    execute(localRunnableFuture);
    return localRunnableFuture;
  }
  
  public <T> Future<T> submit(Runnable paramRunnable, T paramT)
  {
    if (paramRunnable == null) {
      throw new NullPointerException();
    }
    RunnableFuture localRunnableFuture = newTaskFor(paramRunnable, paramT);
    execute(localRunnableFuture);
    return localRunnableFuture;
  }
  
  public <T> Future<T> submit(Callable<T> paramCallable)
  {
    if (paramCallable == null) {
      throw new NullPointerException();
    }
    RunnableFuture localRunnableFuture = newTaskFor(paramCallable);
    execute(localRunnableFuture);
    return localRunnableFuture;
  }
  
  private <T> T doInvokeAny(Collection<? extends Callable<T>> paramCollection, boolean paramBoolean, long paramLong)
    throws InterruptedException, ExecutionException, TimeoutException
  {
    if (paramCollection == null) {
      throw new NullPointerException();
    }
    int i = paramCollection.size();
    if (i == 0) {
      throw new IllegalArgumentException();
    }
    ArrayList localArrayList = new ArrayList(i);
    ExecutorCompletionService localExecutorCompletionService = new ExecutorCompletionService(this);
    try
    {
      Object localObject1 = null;
      long l = paramBoolean ? System.nanoTime() + paramLong : 0L;
      Iterator localIterator = paramCollection.iterator();
      localArrayList.add(localExecutorCompletionService.submit((Callable)localIterator.next()));
      i--;
      int j = 1;
      for (;;)
      {
        Future localFuture = localExecutorCompletionService.poll();
        if (localFuture == null) {
          if (i > 0)
          {
            i--;
            localArrayList.add(localExecutorCompletionService.submit((Callable)localIterator.next()));
            j++;
          }
          else
          {
            if (j == 0) {
              break;
            }
            if (paramBoolean)
            {
              localFuture = localExecutorCompletionService.poll(paramLong, TimeUnit.NANOSECONDS);
              if (localFuture == null) {
                throw new TimeoutException();
              }
              paramLong = l - System.nanoTime();
            }
            else
            {
              localFuture = localExecutorCompletionService.take();
            }
          }
        }
        if (localFuture != null)
        {
          j--;
          try
          {
            Object localObject2 = localFuture.get();
            int k;
            int m;
            return (T)localObject2;
          }
          catch (ExecutionException localExecutionException)
          {
            localObject1 = localExecutionException;
          }
          catch (RuntimeException localRuntimeException)
          {
            localObject1 = new ExecutionException(localRuntimeException);
          }
        }
      }
      if (localObject1 == null) {
        localObject1 = new ExecutionException();
      }
      throw ((Throwable)localObject1);
    }
    finally
    {
      int n = 0;
      int i1 = localArrayList.size();
      while (n < i1)
      {
        ((Future)localArrayList.get(n)).cancel(true);
        n++;
      }
    }
  }
  
  public <T> T invokeAny(Collection<? extends Callable<T>> paramCollection)
    throws InterruptedException, ExecutionException
  {
    try
    {
      return (T)doInvokeAny(paramCollection, false, 0L);
    }
    catch (TimeoutException localTimeoutException)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
    return null;
  }
  
  public <T> T invokeAny(Collection<? extends Callable<T>> paramCollection, long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException, ExecutionException, TimeoutException
  {
    return (T)doInvokeAny(paramCollection, true, paramTimeUnit.toNanos(paramLong));
  }
  
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> paramCollection)
    throws InterruptedException
  {
    if (paramCollection == null) {
      throw new NullPointerException();
    }
    ArrayList localArrayList1 = new ArrayList(paramCollection.size());
    int i = 0;
    try
    {
      Iterator localIterator = paramCollection.iterator();
      Object localObject1;
      while (localIterator.hasNext())
      {
        Callable localCallable = (Callable)localIterator.next();
        localObject1 = newTaskFor(localCallable);
        localArrayList1.add(localObject1);
        execute((Runnable)localObject1);
      }
      int j = 0;
      int k = localArrayList1.size();
      while (j < k)
      {
        localObject1 = (Future)localArrayList1.get(j);
        if (!((Future)localObject1).isDone()) {
          try
          {
            ((Future)localObject1).get();
          }
          catch (CancellationException localCancellationException) {}catch (ExecutionException localExecutionException) {}
        }
        j++;
      }
      i = 1;
      ArrayList localArrayList2 = localArrayList1;
      int m;
      return localArrayList2;
    }
    finally
    {
      if (i == 0)
      {
        int n = 0;
        int i1 = localArrayList1.size();
        while (n < i1)
        {
          ((Future)localArrayList1.get(n)).cancel(true);
          n++;
        }
      }
    }
  }
  
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> paramCollection, long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    if (paramCollection == null) {
      throw new NullPointerException();
    }
    long l1 = paramTimeUnit.toNanos(paramLong);
    ArrayList localArrayList1 = new ArrayList(paramCollection.size());
    int i = 0;
    try
    {
      Iterator localIterator = paramCollection.iterator();
      while (localIterator.hasNext())
      {
        Callable localCallable = (Callable)localIterator.next();
        localArrayList1.add(newTaskFor(localCallable));
      }
      long l2 = System.nanoTime() + l1;
      int j = localArrayList1.size();
      Object localObject1;
      int i2;
      for (int k = 0; k < j; k++)
      {
        execute((Runnable)localArrayList1.get(k));
        l1 = l2 - System.nanoTime();
        if (l1 <= 0L)
        {
          localObject1 = localArrayList1;
          int n;
          return (List<Future<T>>)localObject1;
        }
      }
      for (k = 0; k < j; k++)
      {
        localObject1 = (Future)localArrayList1.get(k);
        if (!((Future)localObject1).isDone())
        {
          int i3;
          if (l1 <= 0L)
          {
            ArrayList localArrayList3 = localArrayList1;
            return localArrayList3;
          }
          try
          {
            ((Future)localObject1).get(l1, TimeUnit.NANOSECONDS);
          }
          catch (CancellationException localCancellationException) {}catch (ExecutionException localExecutionException) {}catch (TimeoutException localTimeoutException)
          {
            ArrayList localArrayList4 = localArrayList1;
            int i4;
            return localArrayList4;
          }
          l1 = l2 - System.nanoTime();
        }
      }
      i = 1;
      ArrayList localArrayList2 = localArrayList1;
      int m;
      int i1;
      return localArrayList2;
    }
    finally
    {
      if (i == 0)
      {
        int i5 = 0;
        int i6 = localArrayList1.size();
        while (i5 < i6)
        {
          ((Future)localArrayList1.get(i5)).cancel(true);
          i5++;
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\AbstractExecutorService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */