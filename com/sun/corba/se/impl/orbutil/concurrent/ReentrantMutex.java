package com.sun.corba.se.impl.orbutil.concurrent;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import org.omg.CORBA.INTERNAL;

public class ReentrantMutex
  implements Sync
{
  protected Thread holder_ = null;
  protected int counter_ = 0;
  protected boolean debug = false;
  
  public ReentrantMutex()
  {
    this(false);
  }
  
  public ReentrantMutex(boolean paramBoolean)
  {
    debug = paramBoolean;
  }
  
  public void acquire()
    throws InterruptedException
  {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    synchronized (this)
    {
      try
      {
        if (debug) {
          ORBUtility.dprintTrace(this, "acquire enter: holder_=" + ORBUtility.getThreadName(holder_) + " counter_=" + counter_);
        }
        Thread localThread = Thread.currentThread();
        if (holder_ != localThread) {
          try
          {
            while (counter_ > 0) {
              wait();
            }
            if (counter_ != 0) {
              throw new INTERNAL("counter not 0 when first acquiring mutex");
            }
            holder_ = localThread;
          }
          catch (InterruptedException localInterruptedException)
          {
            notify();
            throw localInterruptedException;
          }
        }
        counter_ += 1;
      }
      finally
      {
        if (debug) {
          ORBUtility.dprintTrace(this, "acquire exit: holder_=" + ORBUtility.getThreadName(holder_) + " counter_=" + counter_);
        }
      }
    }
  }
  
  void acquireAll(int paramInt)
    throws InterruptedException
  {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    synchronized (this)
    {
      try
      {
        if (debug) {
          ORBUtility.dprintTrace(this, "acquireAll enter: count=" + paramInt + " holder_=" + ORBUtility.getThreadName(holder_) + " counter_=" + counter_);
        }
        Thread localThread = Thread.currentThread();
        if (holder_ == localThread) {
          throw new INTERNAL("Cannot acquireAll while holding the mutex");
        }
        try
        {
          while (counter_ > 0) {
            wait();
          }
          if (counter_ != 0) {
            throw new INTERNAL("counter not 0 when first acquiring mutex");
          }
          holder_ = localThread;
        }
        catch (InterruptedException localInterruptedException)
        {
          notify();
          throw localInterruptedException;
        }
        counter_ = paramInt;
      }
      finally
      {
        if (debug) {
          ORBUtility.dprintTrace(this, "acquireAll exit: count=" + paramInt + " holder_=" + ORBUtility.getThreadName(holder_) + " counter_=" + counter_);
        }
      }
    }
  }
  
  /* Error */
  public synchronized void release()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 112	com/sun/corba/se/impl/orbutil/concurrent/ReentrantMutex:debug	Z
    //   4: ifeq +44 -> 48
    //   7: aload_0
    //   8: new 81	java/lang/StringBuilder
    //   11: dup
    //   12: invokespecial 122	java/lang/StringBuilder:<init>	()V
    //   15: ldc 13
    //   17: invokevirtual 126	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   20: aload_0
    //   21: getfield 113	com/sun/corba/se/impl/orbutil/concurrent/ReentrantMutex:holder_	Ljava/lang/Thread;
    //   24: invokestatic 115	com/sun/corba/se/impl/orbutil/ORBUtility:getThreadName	(Ljava/lang/Thread;)Ljava/lang/String;
    //   27: invokevirtual 126	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   30: ldc 1
    //   32: invokevirtual 126	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   35: aload_0
    //   36: getfield 111	com/sun/corba/se/impl/orbutil/concurrent/ReentrantMutex:counter_	I
    //   39: invokevirtual 124	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   42: invokevirtual 123	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   45: invokestatic 114	com/sun/corba/se/impl/orbutil/ORBUtility:dprintTrace	(Ljava/lang/Object;Ljava/lang/String;)V
    //   48: invokestatic 129	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   51: astore_1
    //   52: aload_1
    //   53: aload_0
    //   54: getfield 113	com/sun/corba/se/impl/orbutil/concurrent/ReentrantMutex:holder_	Ljava/lang/Thread;
    //   57: if_acmpeq +13 -> 70
    //   60: new 85	org/omg/CORBA/INTERNAL
    //   63: dup
    //   64: ldc 3
    //   66: invokespecial 130	org/omg/CORBA/INTERNAL:<init>	(Ljava/lang/String;)V
    //   69: athrow
    //   70: aload_0
    //   71: dup
    //   72: getfield 111	com/sun/corba/se/impl/orbutil/concurrent/ReentrantMutex:counter_	I
    //   75: iconst_1
    //   76: isub
    //   77: putfield 111	com/sun/corba/se/impl/orbutil/concurrent/ReentrantMutex:counter_	I
    //   80: aload_0
    //   81: getfield 111	com/sun/corba/se/impl/orbutil/concurrent/ReentrantMutex:counter_	I
    //   84: ifne +12 -> 96
    //   87: aload_0
    //   88: aconst_null
    //   89: putfield 113	com/sun/corba/se/impl/orbutil/concurrent/ReentrantMutex:holder_	Ljava/lang/Thread;
    //   92: aload_0
    //   93: invokevirtual 119	java/lang/Object:notify	()V
    //   96: aload_0
    //   97: getfield 112	com/sun/corba/se/impl/orbutil/concurrent/ReentrantMutex:debug	Z
    //   100: ifeq +98 -> 198
    //   103: aload_0
    //   104: new 81	java/lang/StringBuilder
    //   107: dup
    //   108: invokespecial 122	java/lang/StringBuilder:<init>	()V
    //   111: ldc 14
    //   113: invokevirtual 126	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   116: aload_0
    //   117: getfield 113	com/sun/corba/se/impl/orbutil/concurrent/ReentrantMutex:holder_	Ljava/lang/Thread;
    //   120: invokestatic 115	com/sun/corba/se/impl/orbutil/ORBUtility:getThreadName	(Ljava/lang/Thread;)Ljava/lang/String;
    //   123: invokevirtual 126	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   126: ldc 1
    //   128: invokevirtual 126	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   131: aload_0
    //   132: getfield 111	com/sun/corba/se/impl/orbutil/concurrent/ReentrantMutex:counter_	I
    //   135: invokevirtual 124	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   138: invokevirtual 123	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   141: invokestatic 114	com/sun/corba/se/impl/orbutil/ORBUtility:dprintTrace	(Ljava/lang/Object;Ljava/lang/String;)V
    //   144: goto +54 -> 198
    //   147: astore_2
    //   148: aload_0
    //   149: getfield 112	com/sun/corba/se/impl/orbutil/concurrent/ReentrantMutex:debug	Z
    //   152: ifeq +44 -> 196
    //   155: aload_0
    //   156: new 81	java/lang/StringBuilder
    //   159: dup
    //   160: invokespecial 122	java/lang/StringBuilder:<init>	()V
    //   163: ldc 14
    //   165: invokevirtual 126	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   168: aload_0
    //   169: getfield 113	com/sun/corba/se/impl/orbutil/concurrent/ReentrantMutex:holder_	Ljava/lang/Thread;
    //   172: invokestatic 115	com/sun/corba/se/impl/orbutil/ORBUtility:getThreadName	(Ljava/lang/Thread;)Ljava/lang/String;
    //   175: invokevirtual 126	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   178: ldc 1
    //   180: invokevirtual 126	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   183: aload_0
    //   184: getfield 111	com/sun/corba/se/impl/orbutil/concurrent/ReentrantMutex:counter_	I
    //   187: invokevirtual 124	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   190: invokevirtual 123	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   193: invokestatic 114	com/sun/corba/se/impl/orbutil/ORBUtility:dprintTrace	(Ljava/lang/Object;Ljava/lang/String;)V
    //   196: aload_2
    //   197: athrow
    //   198: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	199	0	this	ReentrantMutex
    //   51	2	1	localThread	Thread
    //   147	50	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	96	147	finally
  }
  
  synchronized int releaseAll()
  {
    try
    {
      if (debug) {
        ORBUtility.dprintTrace(this, "releaseAll enter:  holder_=" + ORBUtility.getThreadName(holder_) + " counter_=" + counter_);
      }
      Thread localThread = Thread.currentThread();
      if (localThread != holder_) {
        throw new INTERNAL("Attempt to releaseAll Mutex by thread not holding the Mutex");
      }
      int i = counter_;
      counter_ = 0;
      holder_ = null;
      notify();
      int j = i;
      return j;
    }
    finally
    {
      if (debug) {
        ORBUtility.dprintTrace(this, "releaseAll exit:  holder_=" + ORBUtility.getThreadName(holder_) + " counter_=" + counter_);
      }
    }
  }
  
  public boolean attempt(long paramLong)
    throws InterruptedException
  {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    synchronized (this)
    {
      try
      {
        if (debug) {
          ORBUtility.dprintTrace(this, "attempt enter: msecs=" + paramLong + " holder_=" + ORBUtility.getThreadName(holder_) + " counter_=" + counter_);
        }
        Thread localThread = Thread.currentThread();
        boolean bool1;
        if (counter_ == 0)
        {
          holder_ = localThread;
          counter_ = 1;
          bool1 = true;
          if (debug) {
            ORBUtility.dprintTrace(this, "attempt exit:  holder_=" + ORBUtility.getThreadName(holder_) + " counter_=" + counter_);
          }
          return bool1;
        }
        if (paramLong <= 0L)
        {
          bool1 = false;
          if (debug) {
            ORBUtility.dprintTrace(this, "attempt exit:  holder_=" + ORBUtility.getThreadName(holder_) + " counter_=" + counter_);
          }
          return bool1;
        }
        long l1 = paramLong;
        long l2 = System.currentTimeMillis();
        try
        {
          do
          {
            wait(l1);
            if (counter_ == 0)
            {
              holder_ = localThread;
              counter_ = 1;
              bool2 = true;
              if (debug) {
                ORBUtility.dprintTrace(this, "attempt exit:  holder_=" + ORBUtility.getThreadName(holder_) + " counter_=" + counter_);
              }
              return bool2;
            }
            l1 = paramLong - (System.currentTimeMillis() - l2);
          } while (l1 > 0L);
          boolean bool2 = false;
          if (debug) {
            ORBUtility.dprintTrace(this, "attempt exit:  holder_=" + ORBUtility.getThreadName(holder_) + " counter_=" + counter_);
          }
          return bool2;
        }
        catch (InterruptedException localInterruptedException)
        {
          notify();
          throw localInterruptedException;
        }
        localObject2 = finally;
      }
      finally
      {
        if (debug) {
          ORBUtility.dprintTrace(this, "attempt exit:  holder_=" + ORBUtility.getThreadName(holder_) + " counter_=" + counter_);
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orbutil\concurrent\ReentrantMutex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */