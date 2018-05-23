package sun.java2d.pipe;

import java.util.HashSet;
import java.util.Set;
import sun.awt.SunToolkit;

public abstract class RenderQueue
{
  private static final int BUFFER_SIZE = 32000;
  protected RenderBuffer buf = RenderBuffer.allocate(32000);
  protected Set refSet = new HashSet();
  
  protected RenderQueue() {}
  
  public final void lock() {}
  
  public final boolean tryLock()
  {
    return SunToolkit.awtTryLock();
  }
  
  public final void unlock() {}
  
  public final void addReference(Object paramObject)
  {
    refSet.add(paramObject);
  }
  
  public final RenderBuffer getBuffer()
  {
    return buf;
  }
  
  public final void ensureCapacity(int paramInt)
  {
    if (buf.remaining() < paramInt) {
      flushNow();
    }
  }
  
  public final void ensureCapacityAndAlignment(int paramInt1, int paramInt2)
  {
    ensureCapacity(paramInt1 + 4);
    ensureAlignment(paramInt2);
  }
  
  public final void ensureAlignment(int paramInt)
  {
    int i = buf.position() + paramInt;
    if ((i & 0x7) != 0) {
      buf.putInt(90);
    }
  }
  
  public abstract void flushNow();
  
  public abstract void flushAndInvokeNow(Runnable paramRunnable);
  
  public void flushNow(int paramInt)
  {
    buf.position(paramInt);
    flushNow();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\pipe\RenderQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */