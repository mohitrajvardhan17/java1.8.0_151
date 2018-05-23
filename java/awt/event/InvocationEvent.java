package java.awt.event;

import java.awt.AWTEvent;
import java.awt.ActiveEvent;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.InvocationEventAccessor;

public class InvocationEvent
  extends AWTEvent
  implements ActiveEvent
{
  public static final int INVOCATION_FIRST = 1200;
  public static final int INVOCATION_DEFAULT = 1200;
  public static final int INVOCATION_LAST = 1200;
  protected Runnable runnable;
  protected volatile Object notifier;
  private final Runnable listener;
  private volatile boolean dispatched = false;
  protected boolean catchExceptions;
  private Exception exception = null;
  private Throwable throwable = null;
  private long when;
  private static final long serialVersionUID = 436056344909459450L;
  
  public InvocationEvent(Object paramObject, Runnable paramRunnable)
  {
    this(paramObject, 1200, paramRunnable, null, null, false);
  }
  
  public InvocationEvent(Object paramObject1, Runnable paramRunnable, Object paramObject2, boolean paramBoolean)
  {
    this(paramObject1, 1200, paramRunnable, paramObject2, null, paramBoolean);
  }
  
  public InvocationEvent(Object paramObject, Runnable paramRunnable1, Runnable paramRunnable2, boolean paramBoolean)
  {
    this(paramObject, 1200, paramRunnable1, null, paramRunnable2, paramBoolean);
  }
  
  protected InvocationEvent(Object paramObject1, int paramInt, Runnable paramRunnable, Object paramObject2, boolean paramBoolean)
  {
    this(paramObject1, paramInt, paramRunnable, paramObject2, null, paramBoolean);
  }
  
  private InvocationEvent(Object paramObject1, int paramInt, Runnable paramRunnable1, Object paramObject2, Runnable paramRunnable2, boolean paramBoolean)
  {
    super(paramObject1, paramInt);
    runnable = paramRunnable1;
    notifier = paramObject2;
    listener = paramRunnable2;
    catchExceptions = paramBoolean;
    when = System.currentTimeMillis();
  }
  
  /* Error */
  public void dispatch()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 124	java/awt/event/InvocationEvent:catchExceptions	Z
    //   4: ifeq +39 -> 43
    //   7: aload_0
    //   8: getfield 129	java/awt/event/InvocationEvent:runnable	Ljava/lang/Runnable;
    //   11: invokeinterface 144 1 0
    //   16: goto +36 -> 52
    //   19: astore_1
    //   20: aload_1
    //   21: instanceof 74
    //   24: ifeq +11 -> 35
    //   27: aload_0
    //   28: aload_1
    //   29: checkcast 74	java/lang/Exception
    //   32: putfield 126	java/awt/event/InvocationEvent:exception	Ljava/lang/Exception;
    //   35: aload_0
    //   36: aload_1
    //   37: putfield 130	java/awt/event/InvocationEvent:throwable	Ljava/lang/Throwable;
    //   40: goto +12 -> 52
    //   43: aload_0
    //   44: getfield 129	java/awt/event/InvocationEvent:runnable	Ljava/lang/Runnable;
    //   47: invokeinterface 144 1 0
    //   52: aload_0
    //   53: iconst_1
    //   54: invokespecial 132	java/awt/event/InvocationEvent:finishedDispatching	(Z)V
    //   57: goto +11 -> 68
    //   60: astore_2
    //   61: aload_0
    //   62: iconst_1
    //   63: invokespecial 132	java/awt/event/InvocationEvent:finishedDispatching	(Z)V
    //   66: aload_2
    //   67: athrow
    //   68: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	69	0	this	InvocationEvent
    //   19	18	1	localThrowable	Throwable
    //   60	7	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   7	16	19	java/lang/Throwable
    //   0	52	60	finally
  }
  
  public Exception getException()
  {
    return catchExceptions ? exception : null;
  }
  
  public Throwable getThrowable()
  {
    return catchExceptions ? throwable : null;
  }
  
  public long getWhen()
  {
    return when;
  }
  
  public boolean isDispatched()
  {
    return dispatched;
  }
  
  private void finishedDispatching(boolean paramBoolean)
  {
    dispatched = paramBoolean;
    if (notifier != null) {
      synchronized (notifier)
      {
        notifier.notifyAll();
      }
    }
    if (listener != null) {
      listener.run();
    }
  }
  
  public String paramString()
  {
    String str;
    switch (id)
    {
    case 1200: 
      str = "INVOCATION_DEFAULT";
      break;
    default: 
      str = "unknown type";
    }
    return str + ",runnable=" + runnable + ",notifier=" + notifier + ",catchExceptions=" + catchExceptions + ",when=" + when;
  }
  
  static
  {
    AWTAccessor.setInvocationEventAccessor(new AWTAccessor.InvocationEventAccessor()
    {
      public void dispose(InvocationEvent paramAnonymousInvocationEvent)
      {
        paramAnonymousInvocationEvent.finishedDispatching(false);
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\event\InvocationEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */