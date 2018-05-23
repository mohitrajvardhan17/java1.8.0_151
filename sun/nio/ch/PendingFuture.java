package sun.nio.ch;

import java.io.IOException;
import java.nio.channels.AsynchronousChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

final class PendingFuture<V, A>
  implements Future<V>
{
  private static final CancellationException CANCELLED = new CancellationException();
  private final AsynchronousChannel channel;
  private final CompletionHandler<V, ? super A> handler;
  private final A attachment;
  private volatile boolean haveResult;
  private volatile V result;
  private volatile Throwable exc;
  private CountDownLatch latch;
  private Future<?> timeoutTask;
  private volatile Object context;
  
  PendingFuture(AsynchronousChannel paramAsynchronousChannel, CompletionHandler<V, ? super A> paramCompletionHandler, A paramA, Object paramObject)
  {
    channel = paramAsynchronousChannel;
    handler = paramCompletionHandler;
    attachment = paramA;
    context = paramObject;
  }
  
  PendingFuture(AsynchronousChannel paramAsynchronousChannel, CompletionHandler<V, ? super A> paramCompletionHandler, A paramA)
  {
    channel = paramAsynchronousChannel;
    handler = paramCompletionHandler;
    attachment = paramA;
  }
  
  PendingFuture(AsynchronousChannel paramAsynchronousChannel)
  {
    this(paramAsynchronousChannel, null, null);
  }
  
  PendingFuture(AsynchronousChannel paramAsynchronousChannel, Object paramObject)
  {
    this(paramAsynchronousChannel, null, null, paramObject);
  }
  
  AsynchronousChannel channel()
  {
    return channel;
  }
  
  CompletionHandler<V, ? super A> handler()
  {
    return handler;
  }
  
  A attachment()
  {
    return (A)attachment;
  }
  
  void setContext(Object paramObject)
  {
    context = paramObject;
  }
  
  Object getContext()
  {
    return context;
  }
  
  void setTimeoutTask(Future<?> paramFuture)
  {
    synchronized (this)
    {
      if (haveResult) {
        paramFuture.cancel(false);
      } else {
        timeoutTask = paramFuture;
      }
    }
  }
  
  private boolean prepareForWait()
  {
    synchronized (this)
    {
      if (haveResult) {
        return false;
      }
      if (latch == null) {
        latch = new CountDownLatch(1);
      }
      return true;
    }
  }
  
  void setResult(V paramV)
  {
    synchronized (this)
    {
      if (haveResult) {
        return;
      }
      result = paramV;
      haveResult = true;
      if (timeoutTask != null) {
        timeoutTask.cancel(false);
      }
      if (latch != null) {
        latch.countDown();
      }
    }
  }
  
  void setFailure(Throwable paramThrowable)
  {
    if ((!(paramThrowable instanceof IOException)) && (!(paramThrowable instanceof SecurityException))) {
      paramThrowable = new IOException(paramThrowable);
    }
    synchronized (this)
    {
      if (haveResult) {
        return;
      }
      exc = paramThrowable;
      haveResult = true;
      if (timeoutTask != null) {
        timeoutTask.cancel(false);
      }
      if (latch != null) {
        latch.countDown();
      }
    }
  }
  
  void setResult(V paramV, Throwable paramThrowable)
  {
    if (paramThrowable == null) {
      setResult(paramV);
    } else {
      setFailure(paramThrowable);
    }
  }
  
  public V get()
    throws ExecutionException, InterruptedException
  {
    if (!haveResult)
    {
      boolean bool = prepareForWait();
      if (bool) {
        latch.await();
      }
    }
    if (exc != null)
    {
      if (exc == CANCELLED) {
        throw new CancellationException();
      }
      throw new ExecutionException(exc);
    }
    return (V)result;
  }
  
  public V get(long paramLong, TimeUnit paramTimeUnit)
    throws ExecutionException, InterruptedException, TimeoutException
  {
    if (!haveResult)
    {
      boolean bool = prepareForWait();
      if ((bool) && (!latch.await(paramLong, paramTimeUnit))) {
        throw new TimeoutException();
      }
    }
    if (exc != null)
    {
      if (exc == CANCELLED) {
        throw new CancellationException();
      }
      throw new ExecutionException(exc);
    }
    return (V)result;
  }
  
  Throwable exception()
  {
    return exc != CANCELLED ? exc : null;
  }
  
  V value()
  {
    return (V)result;
  }
  
  public boolean isCancelled()
  {
    return exc == CANCELLED;
  }
  
  public boolean isDone()
  {
    return haveResult;
  }
  
  public boolean cancel(boolean paramBoolean)
  {
    synchronized (this)
    {
      if (haveResult) {
        return false;
      }
      if ((channel() instanceof Cancellable)) {
        ((Cancellable)channel()).onCancel(this);
      }
      exc = CANCELLED;
      haveResult = true;
      if (timeoutTask != null) {
        timeoutTask.cancel(false);
      }
    }
    if (paramBoolean) {
      try
      {
        channel().close();
      }
      catch (IOException localIOException) {}
    }
    if (latch != null) {
      latch.countDown();
    }
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\PendingFuture.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */