package com.sun.xml.internal.ws.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class CompletedFuture<T>
  implements Future<T>
{
  private final T v;
  private final Throwable re;
  
  public CompletedFuture(T paramT, Throwable paramThrowable)
  {
    v = paramT;
    re = paramThrowable;
  }
  
  public boolean cancel(boolean paramBoolean)
  {
    return false;
  }
  
  public boolean isCancelled()
  {
    return false;
  }
  
  public boolean isDone()
  {
    return true;
  }
  
  public T get()
    throws ExecutionException
  {
    if (re != null) {
      throw new ExecutionException(re);
    }
    return (T)v;
  }
  
  public T get(long paramLong, TimeUnit paramTimeUnit)
    throws ExecutionException
  {
    return (T)get();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\util\CompletedFuture.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */