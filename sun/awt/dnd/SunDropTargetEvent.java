package sun.awt.dnd;

import java.awt.Component;
import java.awt.event.MouseEvent;

public class SunDropTargetEvent
  extends MouseEvent
{
  public static final int MOUSE_DROPPED = 502;
  private final SunDropTargetContextPeer.EventDispatcher dispatcher;
  
  public SunDropTargetEvent(Component paramComponent, int paramInt1, int paramInt2, int paramInt3, SunDropTargetContextPeer.EventDispatcher paramEventDispatcher)
  {
    super(paramComponent, paramInt1, System.currentTimeMillis(), 0, paramInt2, paramInt3, 0, 0, 0, false, 0);
    dispatcher = paramEventDispatcher;
    dispatcher.registerEvent(this);
  }
  
  /* Error */
  public void dispatch()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 76	sun/awt/dnd/SunDropTargetEvent:dispatcher	Lsun/awt/dnd/SunDropTargetContextPeer$EventDispatcher;
    //   4: aload_0
    //   5: invokevirtual 85	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:dispatchEvent	(Lsun/awt/dnd/SunDropTargetEvent;)V
    //   8: aload_0
    //   9: getfield 76	sun/awt/dnd/SunDropTargetEvent:dispatcher	Lsun/awt/dnd/SunDropTargetContextPeer$EventDispatcher;
    //   12: aload_0
    //   13: invokevirtual 87	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:unregisterEvent	(Lsun/awt/dnd/SunDropTargetEvent;)V
    //   16: goto +14 -> 30
    //   19: astore_1
    //   20: aload_0
    //   21: getfield 76	sun/awt/dnd/SunDropTargetEvent:dispatcher	Lsun/awt/dnd/SunDropTargetContextPeer$EventDispatcher;
    //   24: aload_0
    //   25: invokevirtual 87	sun/awt/dnd/SunDropTargetContextPeer$EventDispatcher:unregisterEvent	(Lsun/awt/dnd/SunDropTargetEvent;)V
    //   28: aload_1
    //   29: athrow
    //   30: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	31	0	this	SunDropTargetEvent
    //   19	10	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	8	19	finally
  }
  
  public void consume()
  {
    boolean bool = isConsumed();
    super.consume();
    if ((!bool) && (isConsumed())) {
      dispatcher.unregisterEvent(this);
    }
  }
  
  public SunDropTargetContextPeer.EventDispatcher getDispatcher()
  {
    return dispatcher;
  }
  
  public String paramString()
  {
    String str = null;
    switch (id)
    {
    case 502: 
      str = "MOUSE_DROPPED";
      break;
    default: 
      return super.paramString();
    }
    return str + ",(" + getX() + "," + getY() + ")";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\dnd\SunDropTargetEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */