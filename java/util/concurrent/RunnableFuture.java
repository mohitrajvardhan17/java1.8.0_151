package java.util.concurrent;

public abstract interface RunnableFuture<V>
  extends Runnable, Future<V>
{
  public abstract void run();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\RunnableFuture.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */