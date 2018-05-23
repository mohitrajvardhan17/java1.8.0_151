package sun.nio.ch;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

final class CompletedFuture<V>
  implements Future<V>
{
  private final V result;
  private final Throwable exc;
  
  private CompletedFuture(V paramV, Throwable paramThrowable)
  {
    result = paramV;
    exc = paramThrowable;
  }
  
  static <V> CompletedFuture<V> withResult(V paramV)
  {
    return new CompletedFuture(paramV, null);
  }
  
  static <V> CompletedFuture<V> withFailure(Throwable paramThrowable)
  {
    if ((!(paramThrowable instanceof IOException)) && (!(paramThrowable instanceof SecurityException))) {
      paramThrowable = new IOException(paramThrowable);
    }
    return new CompletedFuture(null, paramThrowable);
  }
  
  static <V> CompletedFuture<V> withResult(V paramV, Throwable paramThrowable)
  {
    if (paramThrowable == null) {
      return withResult(paramV);
    }
    return withFailure(paramThrowable);
  }
  
  public V get()
    throws ExecutionException
  {
    if (exc != null) {
      throw new ExecutionException(exc);
    }
    return (V)result;
  }
  
  public V get(long paramLong, TimeUnit paramTimeUnit)
    throws ExecutionException
  {
    if (paramTimeUnit == null) {
      throw new NullPointerException();
    }
    if (exc != null) {
      throw new ExecutionException(exc);
    }
    return (V)result;
  }
  
  public boolean isCancelled()
  {
    return false;
  }
  
  public boolean isDone()
  {
    return true;
  }
  
  public boolean cancel(boolean paramBoolean)
  {
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\CompletedFuture.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */