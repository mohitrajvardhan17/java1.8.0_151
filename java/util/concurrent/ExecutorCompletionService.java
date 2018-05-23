package java.util.concurrent;

public class ExecutorCompletionService<V>
  implements CompletionService<V>
{
  private final Executor executor;
  private final AbstractExecutorService aes;
  private final BlockingQueue<Future<V>> completionQueue;
  
  private RunnableFuture<V> newTaskFor(Callable<V> paramCallable)
  {
    if (aes == null) {
      return new FutureTask(paramCallable);
    }
    return aes.newTaskFor(paramCallable);
  }
  
  private RunnableFuture<V> newTaskFor(Runnable paramRunnable, V paramV)
  {
    if (aes == null) {
      return new FutureTask(paramRunnable, paramV);
    }
    return aes.newTaskFor(paramRunnable, paramV);
  }
  
  public ExecutorCompletionService(Executor paramExecutor)
  {
    if (paramExecutor == null) {
      throw new NullPointerException();
    }
    executor = paramExecutor;
    aes = ((paramExecutor instanceof AbstractExecutorService) ? (AbstractExecutorService)paramExecutor : null);
    completionQueue = new LinkedBlockingQueue();
  }
  
  public ExecutorCompletionService(Executor paramExecutor, BlockingQueue<Future<V>> paramBlockingQueue)
  {
    if ((paramExecutor == null) || (paramBlockingQueue == null)) {
      throw new NullPointerException();
    }
    executor = paramExecutor;
    aes = ((paramExecutor instanceof AbstractExecutorService) ? (AbstractExecutorService)paramExecutor : null);
    completionQueue = paramBlockingQueue;
  }
  
  public Future<V> submit(Callable<V> paramCallable)
  {
    if (paramCallable == null) {
      throw new NullPointerException();
    }
    RunnableFuture localRunnableFuture = newTaskFor(paramCallable);
    executor.execute(new QueueingFuture(localRunnableFuture));
    return localRunnableFuture;
  }
  
  public Future<V> submit(Runnable paramRunnable, V paramV)
  {
    if (paramRunnable == null) {
      throw new NullPointerException();
    }
    RunnableFuture localRunnableFuture = newTaskFor(paramRunnable, paramV);
    executor.execute(new QueueingFuture(localRunnableFuture));
    return localRunnableFuture;
  }
  
  public Future<V> take()
    throws InterruptedException
  {
    return (Future)completionQueue.take();
  }
  
  public Future<V> poll()
  {
    return (Future)completionQueue.poll();
  }
  
  public Future<V> poll(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    return (Future)completionQueue.poll(paramLong, paramTimeUnit);
  }
  
  private class QueueingFuture
    extends FutureTask<Void>
  {
    private final Future<V> task;
    
    QueueingFuture()
    {
      super(null);
      task = localRunnable;
    }
    
    protected void done()
    {
      completionQueue.add(task);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\ExecutorCompletionService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */