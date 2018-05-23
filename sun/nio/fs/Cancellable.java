package sun.nio.fs;

import java.util.concurrent.ExecutionException;
import sun.misc.Unsafe;

abstract class Cancellable
  implements Runnable
{
  private static final Unsafe unsafe = ;
  private final long pollingAddress = unsafe.allocateMemory(4L);
  private final Object lock = new Object();
  private boolean completed;
  private Throwable exception;
  
  protected Cancellable()
  {
    unsafe.putIntVolatile(null, pollingAddress, 0);
  }
  
  protected long addressToPollForCancel()
  {
    return pollingAddress;
  }
  
  protected int cancelValue()
  {
    return Integer.MAX_VALUE;
  }
  
  final void cancel()
  {
    synchronized (lock)
    {
      if (!completed) {
        unsafe.putIntVolatile(null, pollingAddress, cancelValue());
      }
    }
  }
  
  /* Error */
  private Throwable exception()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 86	sun/nio/fs/Cancellable:lock	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 87	sun/nio/fs/Cancellable:exception	Ljava/lang/Throwable;
    //   11: aload_1
    //   12: monitorexit
    //   13: areturn
    //   14: astore_2
    //   15: aload_1
    //   16: monitorexit
    //   17: aload_2
    //   18: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	19	0	this	Cancellable
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  public final void run()
  {
    try
    {
      implRun();
    }
    catch (Throwable ???)
    {
      synchronized (lock)
      {
        exception = ((Throwable)???);
      }
    }
    finally
    {
      synchronized (lock)
      {
        completed = true;
        unsafe.freeMemory(pollingAddress);
      }
    }
  }
  
  abstract void implRun()
    throws Throwable;
  
  static void runInterruptibly(Cancellable paramCancellable)
    throws ExecutionException
  {
    Thread localThread = new Thread(paramCancellable);
    localThread.start();
    int i = 0;
    while (localThread.isAlive()) {
      try
      {
        localThread.join();
      }
      catch (InterruptedException localInterruptedException)
      {
        i = 1;
        paramCancellable.cancel();
      }
    }
    if (i != 0) {
      Thread.currentThread().interrupt();
    }
    Throwable localThrowable = paramCancellable.exception();
    if (localThrowable != null) {
      throw new ExecutionException(localThrowable);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\fs\Cancellable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */