package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.Cancelable;
import com.sun.xml.internal.ws.api.Component;
import com.sun.xml.internal.ws.api.ComponentRegistry;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceException;

public final class Fiber
  implements Runnable, Cancelable, ComponentRegistry
{
  private final List<Listener> _listeners = new ArrayList();
  private Tube[] conts = new Tube[16];
  private int contsSize;
  private Tube next;
  private Packet packet;
  private Throwable throwable;
  public final Engine owner;
  private volatile int suspendedCount = 0;
  private volatile boolean isInsideSuspendCallbacks = false;
  private boolean synchronous;
  private boolean interrupted;
  private final int id;
  private List<FiberContextSwitchInterceptor> interceptors;
  @Nullable
  private ClassLoader contextClassLoader;
  @Nullable
  private CompletionCallback completionCallback;
  private boolean isDeliverThrowableInPacket = false;
  private Thread currentThread;
  private final ReentrantLock lock = new ReentrantLock();
  private final Condition condition = lock.newCondition();
  private volatile boolean isCanceled;
  private boolean started;
  private boolean startedSync;
  private static final PlaceholderTube PLACEHOLDER = new PlaceholderTube(null);
  private static final ThreadLocal<Fiber> CURRENT_FIBER = new ThreadLocal();
  private static final AtomicInteger iotaGen = new AtomicInteger();
  private static final Logger LOGGER = Logger.getLogger(Fiber.class.getName());
  private static final ReentrantLock serializedExecutionLock = new ReentrantLock();
  public static volatile boolean serializeExecution = Boolean.getBoolean(Fiber.class.getName() + ".serialize");
  private final Set<Component> components = new CopyOnWriteArraySet();
  
  /**
   * @deprecated
   */
  public void addListener(Listener paramListener)
  {
    synchronized (_listeners)
    {
      if (!_listeners.contains(paramListener)) {
        _listeners.add(paramListener);
      }
    }
  }
  
  /**
   * @deprecated
   */
  public void removeListener(Listener paramListener)
  {
    synchronized (_listeners)
    {
      _listeners.remove(paramListener);
    }
  }
  
  /* Error */
  List<Listener> getCurrentListeners()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 590	com/sun/xml/internal/ws/api/pipe/Fiber:_listeners	Ljava/util/List;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: new 329	java/util/ArrayList
    //   10: dup
    //   11: aload_0
    //   12: getfield 590	com/sun/xml/internal/ws/api/pipe/Fiber:_listeners	Ljava/util/List;
    //   15: invokespecial 676	java/util/ArrayList:<init>	(Ljava/util/Collection;)V
    //   18: aload_1
    //   19: monitorexit
    //   20: areturn
    //   21: astore_2
    //   22: aload_1
    //   23: monitorexit
    //   24: aload_2
    //   25: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	26	0	this	Fiber
    //   5	18	1	Ljava/lang/Object;	Object
    //   21	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	20	21	finally
    //   21	24	21	finally
  }
  
  private void clearListeners()
  {
    synchronized (_listeners)
    {
      _listeners.clear();
    }
  }
  
  public void setDeliverThrowableInPacket(boolean paramBoolean)
  {
    isDeliverThrowableInPacket = paramBoolean;
  }
  
  Fiber(Engine paramEngine)
  {
    owner = paramEngine;
    id = iotaGen.incrementAndGet();
    if (isTraceEnabled()) {
      LOGGER.log(Level.FINE, "{0} created", getName());
    }
    contextClassLoader = Thread.currentThread().getContextClassLoader();
  }
  
  public void start(@NotNull Tube paramTube, @NotNull Packet paramPacket, @Nullable CompletionCallback paramCompletionCallback)
  {
    start(paramTube, paramPacket, paramCompletionCallback, false);
  }
  
  private void dumpFiberContext(String paramString)
  {
    if (isTraceEnabled())
    {
      String str1 = null;
      String str2 = null;
      if (packet != null) {
        for (SOAPVersion localSOAPVersion : SOAPVersion.values())
        {
          for (AddressingVersion localAddressingVersion : AddressingVersion.values())
          {
            str1 = packet.getMessage() != null ? AddressingUtils.getAction(packet.getMessage().getHeaders(), localAddressingVersion, localSOAPVersion) : null;
            str2 = packet.getMessage() != null ? AddressingUtils.getMessageID(packet.getMessage().getHeaders(), localAddressingVersion, localSOAPVersion) : null;
            if ((str1 != null) || (str2 != null)) {
              break;
            }
          }
          if ((str1 != null) || (str2 != null)) {
            break;
          }
        }
      }
      if ((str1 == null) && (str2 == null)) {
        ??? = "NO ACTION or MSG ID";
      } else {
        ??? = "'" + str1 + "' and msgId '" + str2 + "'";
      }
      String str3;
      if (next != null) {
        str3 = next.toString() + ".processRequest()";
      } else {
        str3 = peekCont() + ".processResponse()";
      }
      LOGGER.log(Level.FINE, "{0} {1} with {2} and ''current'' tube {3} from thread {4} with Packet: {5}", new Object[] { getName(), paramString, ???, str3, Thread.currentThread().getName(), packet != null ? packet.toShortString() : null });
    }
  }
  
  public void start(@NotNull Tube paramTube, @NotNull Packet paramPacket, @Nullable CompletionCallback paramCompletionCallback, boolean paramBoolean)
  {
    next = paramTube;
    packet = paramPacket;
    completionCallback = paramCompletionCallback;
    if (paramBoolean)
    {
      startedSync = true;
      dumpFiberContext("starting (sync)");
      run();
    }
    else
    {
      started = true;
      dumpFiberContext("starting (async)");
      owner.addRunnable(this);
    }
  }
  
  public void resume(@NotNull Packet paramPacket)
  {
    resume(paramPacket, false);
  }
  
  public void resume(@NotNull Packet paramPacket, boolean paramBoolean)
  {
    resume(paramPacket, paramBoolean, null);
  }
  
  public void resume(@NotNull Packet paramPacket, boolean paramBoolean, CompletionCallback paramCompletionCallback)
  {
    lock.lock();
    try
    {
      if (paramCompletionCallback != null) {
        setCompletionCallback(paramCompletionCallback);
      }
      if (isTraceEnabled()) {
        LOGGER.log(Level.FINE, "{0} resuming. Will have suspendedCount={1}", new Object[] { getName(), Integer.valueOf(suspendedCount - 1) });
      }
      packet = paramPacket;
      if (--suspendedCount == 0)
      {
        if (!isInsideSuspendCallbacks)
        {
          List localList = getCurrentListeners();
          Iterator localIterator = localList.iterator();
          while (localIterator.hasNext())
          {
            Listener localListener = (Listener)localIterator.next();
            try
            {
              localListener.fiberResumed(this);
            }
            catch (Throwable localThrowable)
            {
              if (isTraceEnabled()) {
                LOGGER.log(Level.FINE, "Listener {0} threw exception: {1}", new Object[] { localListener, localThrowable.getMessage() });
              }
            }
          }
          if (synchronous)
          {
            condition.signalAll();
          }
          else if ((paramBoolean) || (startedSync))
          {
            run();
          }
          else
          {
            dumpFiberContext("resuming (async)");
            owner.addRunnable(this);
          }
        }
      }
      else if (isTraceEnabled()) {
        LOGGER.log(Level.FINE, "{0} taking no action on resume because suspendedCount != 0: {1}", new Object[] { getName(), Integer.valueOf(suspendedCount) });
      }
    }
    finally
    {
      lock.unlock();
    }
  }
  
  public void resumeAndReturn(@NotNull Packet paramPacket, boolean paramBoolean)
  {
    if (isTraceEnabled()) {
      LOGGER.log(Level.FINE, "{0} resumed with Return Packet", getName());
    }
    next = null;
    resume(paramPacket, paramBoolean);
  }
  
  public void resume(@NotNull Throwable paramThrowable)
  {
    resume(paramThrowable, packet, false);
  }
  
  public void resume(@NotNull Throwable paramThrowable, @NotNull Packet paramPacket)
  {
    resume(paramThrowable, paramPacket, false);
  }
  
  public void resume(@NotNull Throwable paramThrowable, boolean paramBoolean)
  {
    resume(paramThrowable, packet, paramBoolean);
  }
  
  public void resume(@NotNull Throwable paramThrowable, @NotNull Packet paramPacket, boolean paramBoolean)
  {
    if (isTraceEnabled()) {
      LOGGER.log(Level.FINE, "{0} resumed with Return Throwable", getName());
    }
    next = null;
    throwable = paramThrowable;
    resume(paramPacket, paramBoolean);
  }
  
  public void cancel(boolean paramBoolean)
  {
    isCanceled = true;
    if (paramBoolean) {
      synchronized (this)
      {
        if (currentThread != null) {
          currentThread.interrupt();
        }
      }
    }
  }
  
  private boolean suspend(Holder<Boolean> paramHolder, Runnable paramRunnable)
  {
    if (isTraceEnabled())
    {
      LOGGER.log(Level.FINE, "{0} suspending. Will have suspendedCount={1}", new Object[] { getName(), Integer.valueOf(suspendedCount + 1) });
      if (suspendedCount > 0) {
        LOGGER.log(Level.FINE, "WARNING - {0} suspended more than resumed. Will require more than one resume to actually resume this fiber.", getName());
      }
    }
    List localList = getCurrentListeners();
    Iterator localIterator;
    Listener localListener;
    if (++suspendedCount == 1)
    {
      isInsideSuspendCallbacks = true;
      try
      {
        localIterator = localList.iterator();
        while (localIterator.hasNext())
        {
          localListener = (Listener)localIterator.next();
          try
          {
            localListener.fiberSuspended(this);
          }
          catch (Throwable localThrowable2)
          {
            if (isTraceEnabled()) {
              LOGGER.log(Level.FINE, "Listener {0} threw exception: {1}", new Object[] { localListener, localThrowable2.getMessage() });
            }
          }
        }
      }
      finally
      {
        isInsideSuspendCallbacks = false;
      }
    }
    if (suspendedCount <= 0)
    {
      localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        localListener = (Listener)localIterator.next();
        try
        {
          localListener.fiberResumed(this);
        }
        catch (Throwable localThrowable3)
        {
          if (isTraceEnabled()) {
            LOGGER.log(Level.FINE, "Listener {0} threw exception: {1}", new Object[] { localListener, localThrowable3.getMessage() });
          }
        }
      }
    }
    else if (paramRunnable != null)
    {
      if (!synchronous)
      {
        synchronized (this)
        {
          currentThread = null;
        }
        lock.unlock();
        assert (!lock.isHeldByCurrentThread());
        value = Boolean.FALSE;
        try
        {
          paramRunnable.run();
        }
        catch (Throwable localThrowable1)
        {
          throw new OnExitRunnableException(localThrowable1);
        }
        return true;
      }
      if (isTraceEnabled()) {
        LOGGER.fine("onExitRunnable used with synchronous Fiber execution -- not exiting current thread");
      }
      paramRunnable.run();
    }
    return false;
  }
  
  public synchronized void addInterceptor(@NotNull FiberContextSwitchInterceptor paramFiberContextSwitchInterceptor)
  {
    if (interceptors == null)
    {
      interceptors = new ArrayList();
    }
    else
    {
      ArrayList localArrayList = new ArrayList();
      localArrayList.addAll(interceptors);
      interceptors = localArrayList;
    }
    interceptors.add(paramFiberContextSwitchInterceptor);
  }
  
  public synchronized boolean removeInterceptor(@NotNull FiberContextSwitchInterceptor paramFiberContextSwitchInterceptor)
  {
    if (interceptors != null)
    {
      boolean bool = interceptors.remove(paramFiberContextSwitchInterceptor);
      if (interceptors.isEmpty())
      {
        interceptors = null;
      }
      else
      {
        ArrayList localArrayList = new ArrayList();
        localArrayList.addAll(interceptors);
        interceptors = localArrayList;
      }
      return bool;
    }
    return false;
  }
  
  @Nullable
  public ClassLoader getContextClassLoader()
  {
    return contextClassLoader;
  }
  
  public ClassLoader setContextClassLoader(@Nullable ClassLoader paramClassLoader)
  {
    ClassLoader localClassLoader = contextClassLoader;
    contextClassLoader = paramClassLoader;
    return localClassLoader;
  }
  
  /* Error */
  @Deprecated
  public void run()
  {
    // Byte code:
    //   0: invokestatic 645	com/sun/xml/internal/ws/api/server/ContainerResolver:getDefault	()Lcom/sun/xml/internal/ws/api/server/ThreadLocalContainerResolver;
    //   3: aload_0
    //   4: getfield 581	com/sun/xml/internal/ws/api/pipe/Fiber:owner	Lcom/sun/xml/internal/ws/api/pipe/Engine;
    //   7: invokevirtual 618	com/sun/xml/internal/ws/api/pipe/Engine:getContainer	()Lcom/sun/xml/internal/ws/api/server/Container;
    //   10: invokevirtual 647	com/sun/xml/internal/ws/api/server/ThreadLocalContainerResolver:enterContainer	(Lcom/sun/xml/internal/ws/api/server/Container;)Lcom/sun/xml/internal/ws/api/server/Container;
    //   13: astore_1
    //   14: getstatic 571	com/sun/xml/internal/ws/api/pipe/Fiber:$assertionsDisabled	Z
    //   17: ifne +18 -> 35
    //   20: aload_0
    //   21: getfield 579	com/sun/xml/internal/ws/api/pipe/Fiber:synchronous	Z
    //   24: ifeq +11 -> 35
    //   27: new 312	java/lang/AssertionError
    //   30: dup
    //   31: invokespecial 648	java/lang/AssertionError:<init>	()V
    //   34: athrow
    //   35: aload_0
    //   36: invokespecial 622	com/sun/xml/internal/ws/api/pipe/Fiber:doRun	()Z
    //   39: ifne +57 -> 96
    //   42: aload_0
    //   43: getfield 578	com/sun/xml/internal/ws/api/pipe/Fiber:startedSync	Z
    //   46: ifeq +46 -> 92
    //   49: aload_0
    //   50: getfield 570	com/sun/xml/internal/ws/api/pipe/Fiber:suspendedCount	I
    //   53: ifne +39 -> 92
    //   56: aload_0
    //   57: getfield 584	com/sun/xml/internal/ws/api/pipe/Fiber:next	Lcom/sun/xml/internal/ws/api/pipe/Tube;
    //   60: ifnonnull +10 -> 70
    //   63: aload_0
    //   64: getfield 568	com/sun/xml/internal/ws/api/pipe/Fiber:contsSize	I
    //   67: ifle +25 -> 92
    //   70: aload_0
    //   71: iconst_0
    //   72: putfield 578	com/sun/xml/internal/ws/api/pipe/Fiber:startedSync	Z
    //   75: aload_0
    //   76: ldc 21
    //   78: invokespecial 632	com/sun/xml/internal/ws/api/pipe/Fiber:dumpFiberContext	(Ljava/lang/String;)V
    //   81: aload_0
    //   82: getfield 581	com/sun/xml/internal/ws/api/pipe/Fiber:owner	Lcom/sun/xml/internal/ws/api/pipe/Engine;
    //   85: aload_0
    //   86: invokevirtual 617	com/sun/xml/internal/ws/api/pipe/Engine:addRunnable	(Lcom/sun/xml/internal/ws/api/pipe/Fiber;)V
    //   89: goto +7 -> 96
    //   92: aload_0
    //   93: invokespecial 620	com/sun/xml/internal/ws/api/pipe/Fiber:completionCheck	()V
    //   96: invokestatic 645	com/sun/xml/internal/ws/api/server/ContainerResolver:getDefault	()Lcom/sun/xml/internal/ws/api/server/ThreadLocalContainerResolver;
    //   99: aload_1
    //   100: invokevirtual 646	com/sun/xml/internal/ws/api/server/ThreadLocalContainerResolver:exitContainer	(Lcom/sun/xml/internal/ws/api/server/Container;)V
    //   103: goto +13 -> 116
    //   106: astore_2
    //   107: invokestatic 645	com/sun/xml/internal/ws/api/server/ContainerResolver:getDefault	()Lcom/sun/xml/internal/ws/api/server/ThreadLocalContainerResolver;
    //   110: aload_1
    //   111: invokevirtual 646	com/sun/xml/internal/ws/api/server/ThreadLocalContainerResolver:exitContainer	(Lcom/sun/xml/internal/ws/api/server/Container;)V
    //   114: aload_2
    //   115: athrow
    //   116: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	117	0	this	Fiber
    //   13	98	1	localContainer	com.sun.xml.internal.ws.api.server.Container
    //   106	9	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   14	96	106	finally
  }
  
  /* Error */
  @NotNull
  public Packet runSync(@NotNull Tube paramTube, @NotNull Packet paramPacket)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 595	com/sun/xml/internal/ws/api/pipe/Fiber:lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   4: invokevirtual 681	java/util/concurrent/locks/ReentrantLock:lock	()V
    //   7: aload_0
    //   8: getfield 585	com/sun/xml/internal/ws/api/pipe/Fiber:conts	[Lcom/sun/xml/internal/ws/api/pipe/Tube;
    //   11: astore_3
    //   12: aload_0
    //   13: getfield 568	com/sun/xml/internal/ws/api/pipe/Fiber:contsSize	I
    //   16: istore 4
    //   18: aload_0
    //   19: getfield 579	com/sun/xml/internal/ws/api/pipe/Fiber:synchronous	Z
    //   22: istore 5
    //   24: aload_0
    //   25: getfield 584	com/sun/xml/internal/ws/api/pipe/Fiber:next	Lcom/sun/xml/internal/ws/api/pipe/Tube;
    //   28: astore 6
    //   30: iload 4
    //   32: ifle +17 -> 49
    //   35: aload_0
    //   36: bipush 16
    //   38: anewarray 308	com/sun/xml/internal/ws/api/pipe/Tube
    //   41: putfield 585	com/sun/xml/internal/ws/api/pipe/Fiber:conts	[Lcom/sun/xml/internal/ws/api/pipe/Tube;
    //   44: aload_0
    //   45: iconst_0
    //   46: putfield 568	com/sun/xml/internal/ws/api/pipe/Fiber:contsSize	I
    //   49: aload_0
    //   50: iconst_1
    //   51: putfield 579	com/sun/xml/internal/ws/api/pipe/Fiber:synchronous	Z
    //   54: aload_0
    //   55: aload_2
    //   56: putfield 580	com/sun/xml/internal/ws/api/pipe/Fiber:packet	Lcom/sun/xml/internal/ws/api/message/Packet;
    //   59: aload_0
    //   60: aload_1
    //   61: putfield 584	com/sun/xml/internal/ws/api/pipe/Fiber:next	Lcom/sun/xml/internal/ws/api/pipe/Tube;
    //   64: aload_0
    //   65: invokespecial 622	com/sun/xml/internal/ws/api/pipe/Fiber:doRun	()Z
    //   68: pop
    //   69: aload_0
    //   70: getfield 589	com/sun/xml/internal/ws/api/pipe/Fiber:throwable	Ljava/lang/Throwable;
    //   73: ifnull +79 -> 152
    //   76: aload_0
    //   77: getfield 574	com/sun/xml/internal/ws/api/pipe/Fiber:isDeliverThrowableInPacket	Z
    //   80: ifeq +24 -> 104
    //   83: aload_0
    //   84: getfield 580	com/sun/xml/internal/ws/api/pipe/Fiber:packet	Lcom/sun/xml/internal/ws/api/message/Packet;
    //   87: new 307	com/sun/xml/internal/ws/api/pipe/ThrowableContainerPropertySet
    //   90: dup
    //   91: aload_0
    //   92: getfield 589	com/sun/xml/internal/ws/api/pipe/Fiber:throwable	Ljava/lang/Throwable;
    //   95: invokespecial 644	com/sun/xml/internal/ws/api/pipe/ThrowableContainerPropertySet:<init>	(Ljava/lang/Throwable;)V
    //   98: invokevirtual 614	com/sun/xml/internal/ws/api/message/Packet:addSatellite	(Lcom/oracle/webservices/internal/api/message/PropertySet;)V
    //   101: goto +51 -> 152
    //   104: aload_0
    //   105: getfield 589	com/sun/xml/internal/ws/api/pipe/Fiber:throwable	Ljava/lang/Throwable;
    //   108: instanceof 322
    //   111: ifeq +11 -> 122
    //   114: aload_0
    //   115: getfield 589	com/sun/xml/internal/ws/api/pipe/Fiber:throwable	Ljava/lang/Throwable;
    //   118: checkcast 322	java/lang/RuntimeException
    //   121: athrow
    //   122: aload_0
    //   123: getfield 589	com/sun/xml/internal/ws/api/pipe/Fiber:throwable	Ljava/lang/Throwable;
    //   126: instanceof 316
    //   129: ifeq +11 -> 140
    //   132: aload_0
    //   133: getfield 589	com/sun/xml/internal/ws/api/pipe/Fiber:throwable	Ljava/lang/Throwable;
    //   136: checkcast 316	java/lang/Error
    //   139: athrow
    //   140: new 312	java/lang/AssertionError
    //   143: dup
    //   144: aload_0
    //   145: getfield 589	com/sun/xml/internal/ws/api/pipe/Fiber:throwable	Ljava/lang/Throwable;
    //   148: invokespecial 649	java/lang/AssertionError:<init>	(Ljava/lang/Object;)V
    //   151: athrow
    //   152: aload_0
    //   153: getfield 580	com/sun/xml/internal/ws/api/pipe/Fiber:packet	Lcom/sun/xml/internal/ws/api/message/Packet;
    //   156: astore 7
    //   158: aload_0
    //   159: aload_3
    //   160: putfield 585	com/sun/xml/internal/ws/api/pipe/Fiber:conts	[Lcom/sun/xml/internal/ws/api/pipe/Tube;
    //   163: aload_0
    //   164: iload 4
    //   166: putfield 568	com/sun/xml/internal/ws/api/pipe/Fiber:contsSize	I
    //   169: aload_0
    //   170: iload 5
    //   172: putfield 579	com/sun/xml/internal/ws/api/pipe/Fiber:synchronous	Z
    //   175: aload_0
    //   176: aload 6
    //   178: putfield 584	com/sun/xml/internal/ws/api/pipe/Fiber:next	Lcom/sun/xml/internal/ws/api/pipe/Tube;
    //   181: aload_0
    //   182: getfield 572	com/sun/xml/internal/ws/api/pipe/Fiber:interrupted	Z
    //   185: ifeq +14 -> 199
    //   188: invokestatic 670	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   191: invokevirtual 666	java/lang/Thread:interrupt	()V
    //   194: aload_0
    //   195: iconst_0
    //   196: putfield 572	com/sun/xml/internal/ws/api/pipe/Fiber:interrupted	Z
    //   199: aload_0
    //   200: getfield 577	com/sun/xml/internal/ws/api/pipe/Fiber:started	Z
    //   203: ifne +14 -> 217
    //   206: aload_0
    //   207: getfield 578	com/sun/xml/internal/ws/api/pipe/Fiber:startedSync	Z
    //   210: ifne +7 -> 217
    //   213: aload_0
    //   214: invokespecial 620	com/sun/xml/internal/ws/api/pipe/Fiber:completionCheck	()V
    //   217: aload_0
    //   218: getfield 595	com/sun/xml/internal/ws/api/pipe/Fiber:lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   221: invokevirtual 682	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   224: aload 7
    //   226: areturn
    //   227: astore 8
    //   229: aload_0
    //   230: aload_3
    //   231: putfield 585	com/sun/xml/internal/ws/api/pipe/Fiber:conts	[Lcom/sun/xml/internal/ws/api/pipe/Tube;
    //   234: aload_0
    //   235: iload 4
    //   237: putfield 568	com/sun/xml/internal/ws/api/pipe/Fiber:contsSize	I
    //   240: aload_0
    //   241: iload 5
    //   243: putfield 579	com/sun/xml/internal/ws/api/pipe/Fiber:synchronous	Z
    //   246: aload_0
    //   247: aload 6
    //   249: putfield 584	com/sun/xml/internal/ws/api/pipe/Fiber:next	Lcom/sun/xml/internal/ws/api/pipe/Tube;
    //   252: aload_0
    //   253: getfield 572	com/sun/xml/internal/ws/api/pipe/Fiber:interrupted	Z
    //   256: ifeq +14 -> 270
    //   259: invokestatic 670	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   262: invokevirtual 666	java/lang/Thread:interrupt	()V
    //   265: aload_0
    //   266: iconst_0
    //   267: putfield 572	com/sun/xml/internal/ws/api/pipe/Fiber:interrupted	Z
    //   270: aload_0
    //   271: getfield 577	com/sun/xml/internal/ws/api/pipe/Fiber:started	Z
    //   274: ifne +14 -> 288
    //   277: aload_0
    //   278: getfield 578	com/sun/xml/internal/ws/api/pipe/Fiber:startedSync	Z
    //   281: ifne +7 -> 288
    //   284: aload_0
    //   285: invokespecial 620	com/sun/xml/internal/ws/api/pipe/Fiber:completionCheck	()V
    //   288: aload 8
    //   290: athrow
    //   291: astore 9
    //   293: aload_0
    //   294: getfield 595	com/sun/xml/internal/ws/api/pipe/Fiber:lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   297: invokevirtual 682	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   300: aload 9
    //   302: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	303	0	this	Fiber
    //   0	303	1	paramTube	Tube
    //   0	303	2	paramPacket	Packet
    //   11	220	3	arrayOfTube	Tube[]
    //   16	220	4	i	int
    //   22	220	5	bool	boolean
    //   28	220	6	localTube	Tube
    //   156	69	7	localPacket	Packet
    //   227	62	8	localObject1	Object
    //   291	10	9	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   49	158	227	finally
    //   227	229	227	finally
    //   7	217	291	finally
    //   227	293	291	finally
  }
  
  /* Error */
  private void completionCheck()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 595	com/sun/xml/internal/ws/api/pipe/Fiber:lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   4: invokevirtual 681	java/util/concurrent/locks/ReentrantLock:lock	()V
    //   7: aload_0
    //   8: getfield 573	com/sun/xml/internal/ws/api/pipe/Fiber:isCanceled	Z
    //   11: ifne +135 -> 146
    //   14: aload_0
    //   15: getfield 568	com/sun/xml/internal/ws/api/pipe/Fiber:contsSize	I
    //   18: ifne +128 -> 146
    //   21: aload_0
    //   22: getfield 570	com/sun/xml/internal/ws/api/pipe/Fiber:suspendedCount	I
    //   25: ifne +121 -> 146
    //   28: invokestatic 623	com/sun/xml/internal/ws/api/pipe/Fiber:isTraceEnabled	()Z
    //   31: ifeq +18 -> 49
    //   34: getstatic 597	com/sun/xml/internal/ws/api/pipe/Fiber:LOGGER	Ljava/util/logging/Logger;
    //   37: getstatic 606	java/util/logging/Level:FINE	Ljava/util/logging/Level;
    //   40: ldc 26
    //   42: aload_0
    //   43: invokespecial 631	com/sun/xml/internal/ws/api/pipe/Fiber:getName	()Ljava/lang/String;
    //   46: invokevirtual 688	java/util/logging/Logger:log	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Object;)V
    //   49: aload_0
    //   50: invokespecial 619	com/sun/xml/internal/ws/api/pipe/Fiber:clearListeners	()V
    //   53: aload_0
    //   54: getfield 594	com/sun/xml/internal/ws/api/pipe/Fiber:condition	Ljava/util/concurrent/locks/Condition;
    //   57: invokeinterface 713 1 0
    //   62: aload_0
    //   63: getfield 582	com/sun/xml/internal/ws/api/pipe/Fiber:completionCallback	Lcom/sun/xml/internal/ws/api/pipe/Fiber$CompletionCallback;
    //   66: ifnull +80 -> 146
    //   69: aload_0
    //   70: getfield 589	com/sun/xml/internal/ws/api/pipe/Fiber:throwable	Ljava/lang/Throwable;
    //   73: ifnull +60 -> 133
    //   76: aload_0
    //   77: getfield 574	com/sun/xml/internal/ws/api/pipe/Fiber:isDeliverThrowableInPacket	Z
    //   80: ifeq +37 -> 117
    //   83: aload_0
    //   84: getfield 580	com/sun/xml/internal/ws/api/pipe/Fiber:packet	Lcom/sun/xml/internal/ws/api/message/Packet;
    //   87: new 307	com/sun/xml/internal/ws/api/pipe/ThrowableContainerPropertySet
    //   90: dup
    //   91: aload_0
    //   92: getfield 589	com/sun/xml/internal/ws/api/pipe/Fiber:throwable	Ljava/lang/Throwable;
    //   95: invokespecial 644	com/sun/xml/internal/ws/api/pipe/ThrowableContainerPropertySet:<init>	(Ljava/lang/Throwable;)V
    //   98: invokevirtual 614	com/sun/xml/internal/ws/api/message/Packet:addSatellite	(Lcom/oracle/webservices/internal/api/message/PropertySet;)V
    //   101: aload_0
    //   102: getfield 582	com/sun/xml/internal/ws/api/pipe/Fiber:completionCallback	Lcom/sun/xml/internal/ws/api/pipe/Fiber$CompletionCallback;
    //   105: aload_0
    //   106: getfield 580	com/sun/xml/internal/ws/api/pipe/Fiber:packet	Lcom/sun/xml/internal/ws/api/message/Packet;
    //   109: invokeinterface 694 2 0
    //   114: goto +32 -> 146
    //   117: aload_0
    //   118: getfield 582	com/sun/xml/internal/ws/api/pipe/Fiber:completionCallback	Lcom/sun/xml/internal/ws/api/pipe/Fiber$CompletionCallback;
    //   121: aload_0
    //   122: getfield 589	com/sun/xml/internal/ws/api/pipe/Fiber:throwable	Ljava/lang/Throwable;
    //   125: invokeinterface 695 2 0
    //   130: goto +16 -> 146
    //   133: aload_0
    //   134: getfield 582	com/sun/xml/internal/ws/api/pipe/Fiber:completionCallback	Lcom/sun/xml/internal/ws/api/pipe/Fiber$CompletionCallback;
    //   137: aload_0
    //   138: getfield 580	com/sun/xml/internal/ws/api/pipe/Fiber:packet	Lcom/sun/xml/internal/ws/api/message/Packet;
    //   141: invokeinterface 694 2 0
    //   146: aload_0
    //   147: getfield 595	com/sun/xml/internal/ws/api/pipe/Fiber:lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   150: invokevirtual 682	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   153: goto +13 -> 166
    //   156: astore_1
    //   157: aload_0
    //   158: getfield 595	com/sun/xml/internal/ws/api/pipe/Fiber:lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   161: invokevirtual 682	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   164: aload_1
    //   165: athrow
    //   166: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	167	0	this	Fiber
    //   156	9	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   7	146	156	finally
  }
  
  private boolean doRun()
  {
    dumpFiberContext("running");
    if (serializeExecution)
    {
      serializedExecutionLock.lock();
      try
      {
        boolean bool = _doRun(next);
        return bool;
      }
      finally
      {
        serializedExecutionLock.unlock();
      }
    }
    return _doRun(next);
  }
  
  private boolean _doRun(Tube paramTube)
  {
    Holder localHolder = new Holder(Boolean.TRUE);
    lock.lock();
    try
    {
      List localList;
      ClassLoader localClassLoader;
      synchronized (this)
      {
        localList = interceptors;
        currentThread = Thread.currentThread();
        if (isTraceEnabled()) {
          LOGGER.log(Level.FINE, "Thread entering _doRun(): {0}", currentThread);
        }
        localClassLoader = currentThread.getContextClassLoader();
        currentThread.setContextClassLoader(contextClassLoader);
      }
      try
      {
        int i;
        do
        {
          boolean bool2;
          if (localList == null)
          {
            next = paramTube;
            if (__doRun(localHolder, null))
            {
              bool2 = true;
              Thread localThread2 = Thread.currentThread();
              localThread2.setContextClassLoader(localClassLoader);
              if (isTraceEnabled()) {
                LOGGER.log(Level.FINE, "Thread leaving _doRun(): {0}", localThread2);
              }
              return bool2;
            }
          }
          else
          {
            paramTube = new InterceptorHandler(localHolder, localList).invoke(paramTube);
            if (paramTube == PLACEHOLDER)
            {
              bool2 = true;
              ??? = Thread.currentThread();
              ((Thread)???).setContextClassLoader(localClassLoader);
              if (isTraceEnabled()) {
                LOGGER.log(Level.FINE, "Thread leaving _doRun(): {0}", ???);
              }
              return bool2;
            }
          }
          synchronized (this)
          {
            i = localList != interceptors ? 1 : 0;
            if (i != 0) {
              localList = interceptors;
            }
          }
        } while (i != 0);
      }
      catch (OnExitRunnableException localOnExitRunnableException)
      {
        Thread localThread1;
        ??? = target;
        if ((??? instanceof WebServiceException)) {
          throw ((WebServiceException)???);
        }
        throw new WebServiceException((Throwable)???);
      }
      finally
      {
        Thread localThread3 = Thread.currentThread();
        localThread3.setContextClassLoader(localClassLoader);
        if (isTraceEnabled()) {
          LOGGER.log(Level.FINE, "Thread leaving _doRun(): {0}", localThread3);
        }
      }
      boolean bool1 = false;
      return bool1;
    }
    finally
    {
      if (((Boolean)value).booleanValue())
      {
        synchronized (this)
        {
          currentThread = null;
        }
        lock.unlock();
      }
    }
  }
  
  private boolean __doRun(Holder<Boolean> paramHolder, List<FiberContextSwitchInterceptor> paramList)
  {
    assert (lock.isHeldByCurrentThread());
    Fiber localFiber = (Fiber)CURRENT_FIBER.get();
    CURRENT_FIBER.set(this);
    boolean bool1 = LOGGER.isLoggable(Level.FINER);
    try
    {
      int i = 0;
      while (isReady(paramList))
      {
        if (isCanceled)
        {
          next = null;
          throwable = null;
          contsSize = 0;
          break;
        }
        try
        {
          boolean bool2;
          Tube localTube;
          NextAction localNextAction;
          if (throwable != null)
          {
            if ((contsSize == 0) || (i != 0))
            {
              contsSize = 0;
              bool2 = false;
              return bool2;
            }
            localTube = popCont();
            if (bool1) {
              LOGGER.log(Level.FINER, "{0} {1}.processException({2})", new Object[] { getName(), localTube, throwable });
            }
            localNextAction = localTube.processException(throwable);
          }
          else if (next != null)
          {
            if (bool1) {
              LOGGER.log(Level.FINER, "{0} {1}.processRequest({2})", new Object[] { getName(), next, packet != null ? "Packet@" + Integer.toHexString(packet.hashCode()) : "null" });
            }
            localNextAction = next.processRequest(packet);
            localTube = next;
          }
          else
          {
            if ((contsSize == 0) || (i != 0))
            {
              contsSize = 0;
              bool2 = false;
              return bool2;
            }
            localTube = popCont();
            if (bool1) {
              LOGGER.log(Level.FINER, "{0} {1}.processResponse({2})", new Object[] { getName(), localTube, packet != null ? "Packet@" + Integer.toHexString(packet.hashCode()) : "null" });
            }
            localNextAction = localTube.processResponse(packet);
          }
          if (bool1) {
            LOGGER.log(Level.FINER, "{0} {1} returned with {2}", new Object[] { getName(), localTube, localNextAction });
          }
          if (kind != 4)
          {
            if ((kind != 3) && (kind != 5)) {
              packet = packet;
            }
            throwable = throwable;
          }
          switch (kind)
          {
          case 0: 
          case 7: 
            pushCont(localTube);
          case 1: 
            next = next;
            if ((kind != 7) || (!startedSync)) {
              break label697;
            }
            bool2 = false;
            return bool2;
          case 5: 
          case 6: 
            i = 1;
            if (isTraceEnabled()) {
              LOGGER.log(Level.FINE, "Fiber {0} is aborting a response due to exception: {1}", new Object[] { this, throwable });
            }
          case 2: 
          case 3: 
            next = null;
            break;
          case 4: 
            if (next != null) {
              pushCont(localTube);
            }
            next = next;
            if (!suspend(paramHolder, onExitRunnable)) {
              break label697;
            }
            bool2 = true;
            return bool2;
          }
          throw new AssertionError();
        }
        catch (RuntimeException localRuntimeException)
        {
          if (bool1) {
            LOGGER.log(Level.FINER, getName() + " Caught " + localRuntimeException + ". Start stack unwinding", localRuntimeException);
          }
          throwable = localRuntimeException;
        }
        catch (Error localError)
        {
          label697:
          if (bool1) {
            LOGGER.log(Level.FINER, getName() + " Caught " + localError + ". Start stack unwinding", localError);
          }
          throwable = localError;
        }
        dumpFiberContext("After tube execution");
      }
    }
    finally
    {
      CURRENT_FIBER.set(localFiber);
    }
    return false;
  }
  
  private void pushCont(Tube paramTube)
  {
    conts[(contsSize++)] = paramTube;
    int i = conts.length;
    if (contsSize == i)
    {
      Tube[] arrayOfTube = new Tube[i * 2];
      System.arraycopy(conts, 0, arrayOfTube, 0, i);
      conts = arrayOfTube;
    }
  }
  
  private Tube popCont()
  {
    return conts[(--contsSize)];
  }
  
  private Tube peekCont()
  {
    int i = contsSize - 1;
    if ((i >= 0) && (i < conts.length)) {
      return conts[i];
    }
    return null;
  }
  
  public void resetCont(Tube[] paramArrayOfTube, int paramInt)
  {
    conts = paramArrayOfTube;
    contsSize = paramInt;
  }
  
  private boolean isReady(List<FiberContextSwitchInterceptor> paramList)
  {
    if (synchronous)
    {
      while (suspendedCount == 1) {
        try
        {
          if (isTraceEnabled()) {
            LOGGER.log(Level.FINE, "{0} is blocking thread {1}", new Object[] { getName(), Thread.currentThread().getName() });
          }
          condition.await();
        }
        catch (InterruptedException localInterruptedException)
        {
          interrupted = true;
        }
      }
      synchronized (this)
      {
        return interceptors == paramList;
      }
    }
    if (suspendedCount > 0) {
      return false;
    }
    synchronized (this)
    {
      return interceptors == paramList;
    }
  }
  
  private String getName()
  {
    return "engine-" + owner.id + "fiber-" + id;
  }
  
  public String toString()
  {
    return getName();
  }
  
  @Nullable
  public Packet getPacket()
  {
    return packet;
  }
  
  public CompletionCallback getCompletionCallback()
  {
    return completionCallback;
  }
  
  public void setCompletionCallback(CompletionCallback paramCompletionCallback)
  {
    completionCallback = paramCompletionCallback;
  }
  
  public static boolean isSynchronous()
  {
    return currentsynchronous;
  }
  
  public boolean isStartedSync()
  {
    return startedSync;
  }
  
  @NotNull
  public static Fiber current()
  {
    Fiber localFiber = (Fiber)CURRENT_FIBER.get();
    if (localFiber == null) {
      throw new IllegalStateException("Can be only used from fibers");
    }
    return localFiber;
  }
  
  public static Fiber getCurrentIfSet()
  {
    return (Fiber)CURRENT_FIBER.get();
  }
  
  private static boolean isTraceEnabled()
  {
    return LOGGER.isLoggable(Level.FINE);
  }
  
  public <S> S getSPI(Class<S> paramClass)
  {
    Iterator localIterator = components.iterator();
    while (localIterator.hasNext())
    {
      Component localComponent = (Component)localIterator.next();
      Object localObject = localComponent.getSPI(paramClass);
      if (localObject != null) {
        return (S)localObject;
      }
    }
    return null;
  }
  
  public Set<Component> getComponents()
  {
    return components;
  }
  
  public static abstract interface CompletionCallback
  {
    public abstract void onCompletion(@NotNull Packet paramPacket);
    
    public abstract void onCompletion(@NotNull Throwable paramThrowable);
  }
  
  private class InterceptorHandler
    implements FiberContextSwitchInterceptor.Work<Tube, Tube>
  {
    private final Holder<Boolean> isUnlockRequired;
    private final List<FiberContextSwitchInterceptor> ints;
    private int idx;
    
    public InterceptorHandler(List<FiberContextSwitchInterceptor> paramList)
    {
      isUnlockRequired = paramList;
      List localList;
      ints = localList;
    }
    
    Tube invoke(Tube paramTube)
    {
      idx = 0;
      return execute(paramTube);
    }
    
    public Tube execute(Tube paramTube)
    {
      if (idx == ints.size())
      {
        next = paramTube;
        if (Fiber.this.__doRun(isUnlockRequired, ints)) {
          return Fiber.PLACEHOLDER;
        }
      }
      else
      {
        FiberContextSwitchInterceptor localFiberContextSwitchInterceptor = (FiberContextSwitchInterceptor)ints.get(idx++);
        return (Tube)localFiberContextSwitchInterceptor.execute(Fiber.this, paramTube, this);
      }
      return next;
    }
  }
  
  /**
   * @deprecated
   */
  public static abstract interface Listener
  {
    public abstract void fiberSuspended(Fiber paramFiber);
    
    public abstract void fiberResumed(Fiber paramFiber);
  }
  
  private static final class OnExitRunnableException
    extends RuntimeException
  {
    private static final long serialVersionUID = 1L;
    Throwable target;
    
    public OnExitRunnableException(Throwable paramThrowable)
    {
      super();
      target = paramThrowable;
    }
  }
  
  private static class PlaceholderTube
    extends AbstractTubeImpl
  {
    private PlaceholderTube() {}
    
    public NextAction processRequest(Packet paramPacket)
    {
      throw new UnsupportedOperationException();
    }
    
    public NextAction processResponse(Packet paramPacket)
    {
      throw new UnsupportedOperationException();
    }
    
    public NextAction processException(Throwable paramThrowable)
    {
      return doThrow(paramThrowable);
    }
    
    public void preDestroy() {}
    
    public PlaceholderTube copy(TubeCloner paramTubeCloner)
    {
      throw new UnsupportedOperationException();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\pipe\Fiber.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */