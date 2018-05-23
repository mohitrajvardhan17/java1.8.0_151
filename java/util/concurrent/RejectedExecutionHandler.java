package java.util.concurrent;

public abstract interface RejectedExecutionHandler
{
  public abstract void rejectedExecution(Runnable paramRunnable, ThreadPoolExecutor paramThreadPoolExecutor);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\RejectedExecutionHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */