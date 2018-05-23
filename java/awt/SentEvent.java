package java.awt;

import sun.awt.AppContext;
import sun.awt.SunToolkit;

class SentEvent
  extends AWTEvent
  implements ActiveEvent
{
  private static final long serialVersionUID = -383615247028828931L;
  static final int ID = 1007;
  boolean dispatched;
  private AWTEvent nested;
  private AppContext toNotify;
  
  SentEvent()
  {
    this(null);
  }
  
  SentEvent(AWTEvent paramAWTEvent)
  {
    this(paramAWTEvent, null);
  }
  
  SentEvent(AWTEvent paramAWTEvent, AppContext paramAppContext)
  {
    super(paramAWTEvent != null ? paramAWTEvent.getSource() : Toolkit.getDefaultToolkit(), 1007);
    nested = paramAWTEvent;
    toNotify = paramAppContext;
  }
  
  /* Error */
  public void dispatch()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 66	java/awt/SentEvent:nested	Ljava/awt/AWTEvent;
    //   4: ifnull +13 -> 17
    //   7: invokestatic 74	java/awt/Toolkit:getEventQueue	()Ljava/awt/EventQueue;
    //   10: aload_0
    //   11: getfield 66	java/awt/SentEvent:nested	Ljava/awt/AWTEvent;
    //   14: invokevirtual 70	java/awt/EventQueue:dispatchEvent	(Ljava/awt/AWTEvent;)V
    //   17: aload_0
    //   18: iconst_1
    //   19: putfield 65	java/awt/SentEvent:dispatched	Z
    //   22: aload_0
    //   23: getfield 67	java/awt/SentEvent:toNotify	Lsun/awt/AppContext;
    //   26: ifnull +17 -> 43
    //   29: aload_0
    //   30: getfield 67	java/awt/SentEvent:toNotify	Lsun/awt/AppContext;
    //   33: new 38	java/awt/SentEvent
    //   36: dup
    //   37: invokespecial 71	java/awt/SentEvent:<init>	()V
    //   40: invokestatic 77	sun/awt/SunToolkit:postEvent	(Lsun/awt/AppContext;Ljava/awt/AWTEvent;)V
    //   43: aload_0
    //   44: dup
    //   45: astore_1
    //   46: monitorenter
    //   47: aload_0
    //   48: invokevirtual 76	java/lang/Object:notifyAll	()V
    //   51: aload_1
    //   52: monitorexit
    //   53: goto +8 -> 61
    //   56: astore_2
    //   57: aload_1
    //   58: monitorexit
    //   59: aload_2
    //   60: athrow
    //   61: goto +55 -> 116
    //   64: astore_3
    //   65: aload_0
    //   66: iconst_1
    //   67: putfield 65	java/awt/SentEvent:dispatched	Z
    //   70: aload_0
    //   71: getfield 67	java/awt/SentEvent:toNotify	Lsun/awt/AppContext;
    //   74: ifnull +17 -> 91
    //   77: aload_0
    //   78: getfield 67	java/awt/SentEvent:toNotify	Lsun/awt/AppContext;
    //   81: new 38	java/awt/SentEvent
    //   84: dup
    //   85: invokespecial 71	java/awt/SentEvent:<init>	()V
    //   88: invokestatic 77	sun/awt/SunToolkit:postEvent	(Lsun/awt/AppContext;Ljava/awt/AWTEvent;)V
    //   91: aload_0
    //   92: dup
    //   93: astore 4
    //   95: monitorenter
    //   96: aload_0
    //   97: invokevirtual 76	java/lang/Object:notifyAll	()V
    //   100: aload 4
    //   102: monitorexit
    //   103: goto +11 -> 114
    //   106: astore 5
    //   108: aload 4
    //   110: monitorexit
    //   111: aload 5
    //   113: athrow
    //   114: aload_3
    //   115: athrow
    //   116: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	117	0	this	SentEvent
    //   56	4	2	localObject1	Object
    //   64	51	3	localObject2	Object
    //   106	6	5	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   47	53	56	finally
    //   56	59	56	finally
    //   0	17	64	finally
    //   96	103	106	finally
    //   106	111	106	finally
  }
  
  final void dispose()
  {
    dispatched = true;
    if (toNotify != null) {
      SunToolkit.postEvent(toNotify, new SentEvent());
    }
    synchronized (this)
    {
      notifyAll();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\SentEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */