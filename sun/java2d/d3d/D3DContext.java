package sun.java2d.d3d;

import sun.java2d.pipe.BufferedContext;
import sun.java2d.pipe.RenderBuffer;
import sun.java2d.pipe.RenderQueue;
import sun.java2d.pipe.hw.ContextCapabilities;

class D3DContext
  extends BufferedContext
{
  private final D3DGraphicsDevice device;
  
  D3DContext(RenderQueue paramRenderQueue, D3DGraphicsDevice paramD3DGraphicsDevice)
  {
    super(paramRenderQueue);
    device = paramD3DGraphicsDevice;
  }
  
  static void invalidateCurrentContext()
  {
    if (currentContext != null)
    {
      currentContext.invalidateContext();
      currentContext = null;
    }
    D3DRenderQueue localD3DRenderQueue = D3DRenderQueue.getInstance();
    localD3DRenderQueue.ensureCapacity(4);
    localD3DRenderQueue.getBuffer().putInt(75);
    localD3DRenderQueue.flushNow();
  }
  
  static void setScratchSurface(D3DContext paramD3DContext)
  {
    if (paramD3DContext != currentContext) {
      currentContext = null;
    }
    D3DRenderQueue localD3DRenderQueue = D3DRenderQueue.getInstance();
    RenderBuffer localRenderBuffer = localD3DRenderQueue.getBuffer();
    localD3DRenderQueue.ensureCapacity(8);
    localRenderBuffer.putInt(71);
    localRenderBuffer.putInt(paramD3DContext.getDevice().getScreen());
  }
  
  public RenderQueue getRenderQueue()
  {
    return D3DRenderQueue.getInstance();
  }
  
  public void saveState()
  {
    invalidateContext();
    invalidateCurrentContext();
    setScratchSurface(this);
    rq.ensureCapacity(4);
    buf.putInt(78);
    rq.flushNow();
  }
  
  public void restoreState()
  {
    invalidateContext();
    invalidateCurrentContext();
    setScratchSurface(this);
    rq.ensureCapacity(4);
    buf.putInt(79);
    rq.flushNow();
  }
  
  D3DGraphicsDevice getDevice()
  {
    return device;
  }
  
  static class D3DContextCaps
    extends ContextCapabilities
  {
    static final int CAPS_LCD_SHADER = 65536;
    static final int CAPS_BIOP_SHADER = 131072;
    static final int CAPS_DEVICE_OK = 262144;
    static final int CAPS_AA_SHADER = 524288;
    
    D3DContextCaps(int paramInt, String paramString)
    {
      super(paramString);
    }
    
    public String toString()
    {
      StringBuffer localStringBuffer = new StringBuffer(super.toString());
      if ((caps & 0x10000) != 0) {
        localStringBuffer.append("CAPS_LCD_SHADER|");
      }
      if ((caps & 0x20000) != 0) {
        localStringBuffer.append("CAPS_BIOP_SHADER|");
      }
      if ((caps & 0x80000) != 0) {
        localStringBuffer.append("CAPS_AA_SHADER|");
      }
      if ((caps & 0x40000) != 0) {
        localStringBuffer.append("CAPS_DEVICE_OK|");
      }
      return localStringBuffer.toString();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\d3d\D3DContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */