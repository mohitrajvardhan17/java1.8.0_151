package sun.java2d.d3d;

import java.util.Set;
import sun.java2d.pipe.RenderBuffer;
import sun.java2d.pipe.RenderQueue;

public class D3DRenderQueue
  extends RenderQueue
{
  private static D3DRenderQueue theInstance;
  private static Thread rqThread;
  
  private D3DRenderQueue() {}
  
  public static synchronized D3DRenderQueue getInstance()
  {
    if (theInstance == null)
    {
      theInstance = new D3DRenderQueue();
      theInstance.flushAndInvokeNow(new Runnable()
      {
        public void run()
        {
          D3DRenderQueue.access$002(Thread.currentThread());
        }
      });
    }
    return theInstance;
  }
  
  /* Error */
  public static void sync()
  {
    // Byte code:
    //   0: getstatic 93	sun/java2d/d3d/D3DRenderQueue:theInstance	Lsun/java2d/d3d/D3DRenderQueue;
    //   3: ifnull +63 -> 66
    //   6: invokestatic 96	sun/java2d/ScreenUpdateManager:getInstance	()Lsun/java2d/ScreenUpdateManager;
    //   9: checkcast 52	sun/java2d/d3d/D3DScreenUpdateManager
    //   12: astore_0
    //   13: aload_0
    //   14: invokevirtual 109	sun/java2d/d3d/D3DScreenUpdateManager:runUpdateNow	()V
    //   17: getstatic 93	sun/java2d/d3d/D3DRenderQueue:theInstance	Lsun/java2d/d3d/D3DRenderQueue;
    //   20: invokevirtual 99	sun/java2d/d3d/D3DRenderQueue:lock	()V
    //   23: getstatic 93	sun/java2d/d3d/D3DRenderQueue:theInstance	Lsun/java2d/d3d/D3DRenderQueue;
    //   26: iconst_4
    //   27: invokevirtual 101	sun/java2d/d3d/D3DRenderQueue:ensureCapacity	(I)V
    //   30: getstatic 93	sun/java2d/d3d/D3DRenderQueue:theInstance	Lsun/java2d/d3d/D3DRenderQueue;
    //   33: invokevirtual 107	sun/java2d/d3d/D3DRenderQueue:getBuffer	()Lsun/java2d/pipe/RenderBuffer;
    //   36: bipush 76
    //   38: invokevirtual 113	sun/java2d/pipe/RenderBuffer:putInt	(I)Lsun/java2d/pipe/RenderBuffer;
    //   41: pop
    //   42: getstatic 93	sun/java2d/d3d/D3DRenderQueue:theInstance	Lsun/java2d/d3d/D3DRenderQueue;
    //   45: invokevirtual 98	sun/java2d/d3d/D3DRenderQueue:flushNow	()V
    //   48: getstatic 93	sun/java2d/d3d/D3DRenderQueue:theInstance	Lsun/java2d/d3d/D3DRenderQueue;
    //   51: invokevirtual 100	sun/java2d/d3d/D3DRenderQueue:unlock	()V
    //   54: goto +12 -> 66
    //   57: astore_1
    //   58: getstatic 93	sun/java2d/d3d/D3DRenderQueue:theInstance	Lsun/java2d/d3d/D3DRenderQueue;
    //   61: invokevirtual 100	sun/java2d/d3d/D3DRenderQueue:unlock	()V
    //   64: aload_1
    //   65: athrow
    //   66: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   12	2	0	localD3DScreenUpdateManager	D3DScreenUpdateManager
    //   57	8	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   23	48	57	finally
  }
  
  /* Error */
  public static void restoreDevices()
  {
    // Byte code:
    //   0: invokestatic 106	sun/java2d/d3d/D3DRenderQueue:getInstance	()Lsun/java2d/d3d/D3DRenderQueue;
    //   3: astore_0
    //   4: aload_0
    //   5: invokevirtual 99	sun/java2d/d3d/D3DRenderQueue:lock	()V
    //   8: aload_0
    //   9: iconst_4
    //   10: invokevirtual 101	sun/java2d/d3d/D3DRenderQueue:ensureCapacity	(I)V
    //   13: aload_0
    //   14: invokevirtual 107	sun/java2d/d3d/D3DRenderQueue:getBuffer	()Lsun/java2d/pipe/RenderBuffer;
    //   17: bipush 77
    //   19: invokevirtual 113	sun/java2d/pipe/RenderBuffer:putInt	(I)Lsun/java2d/pipe/RenderBuffer;
    //   22: pop
    //   23: aload_0
    //   24: invokevirtual 98	sun/java2d/d3d/D3DRenderQueue:flushNow	()V
    //   27: aload_0
    //   28: invokevirtual 100	sun/java2d/d3d/D3DRenderQueue:unlock	()V
    //   31: goto +10 -> 41
    //   34: astore_1
    //   35: aload_0
    //   36: invokevirtual 100	sun/java2d/d3d/D3DRenderQueue:unlock	()V
    //   39: aload_1
    //   40: athrow
    //   41: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   3	33	0	localD3DRenderQueue	D3DRenderQueue
    //   34	6	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   8	27	34	finally
  }
  
  public static boolean isRenderQueueThread()
  {
    return Thread.currentThread() == rqThread;
  }
  
  public static void disposeGraphicsConfig(long paramLong)
  {
    D3DRenderQueue localD3DRenderQueue = getInstance();
    localD3DRenderQueue.lock();
    try
    {
      RenderBuffer localRenderBuffer = localD3DRenderQueue.getBuffer();
      localD3DRenderQueue.ensureCapacityAndAlignment(12, 4);
      localRenderBuffer.putInt(74);
      localRenderBuffer.putLong(paramLong);
      localD3DRenderQueue.flushNow();
    }
    finally
    {
      localD3DRenderQueue.unlock();
    }
  }
  
  public void flushNow()
  {
    flushBuffer(null);
  }
  
  public void flushAndInvokeNow(Runnable paramRunnable)
  {
    flushBuffer(paramRunnable);
  }
  
  private native void flushBuffer(long paramLong, int paramInt, Runnable paramRunnable);
  
  private void flushBuffer(Runnable paramRunnable)
  {
    int i = buf.position();
    if ((i > 0) || (paramRunnable != null)) {
      flushBuffer(buf.getAddress(), i, paramRunnable);
    }
    buf.clear();
    refSet.clear();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\d3d\D3DRenderQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */