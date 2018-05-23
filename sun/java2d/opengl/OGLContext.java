package sun.java2d.opengl;

import sun.java2d.pipe.BufferedContext;
import sun.java2d.pipe.RenderBuffer;
import sun.java2d.pipe.RenderQueue;
import sun.java2d.pipe.hw.ContextCapabilities;

public class OGLContext
  extends BufferedContext
{
  private final OGLGraphicsConfig config;
  
  OGLContext(RenderQueue paramRenderQueue, OGLGraphicsConfig paramOGLGraphicsConfig)
  {
    super(paramRenderQueue);
    config = paramOGLGraphicsConfig;
  }
  
  static void setScratchSurface(OGLGraphicsConfig paramOGLGraphicsConfig)
  {
    setScratchSurface(paramOGLGraphicsConfig.getNativeConfigInfo());
  }
  
  static void setScratchSurface(long paramLong)
  {
    currentContext = null;
    OGLRenderQueue localOGLRenderQueue = OGLRenderQueue.getInstance();
    RenderBuffer localRenderBuffer = localOGLRenderQueue.getBuffer();
    localOGLRenderQueue.ensureCapacityAndAlignment(12, 4);
    localRenderBuffer.putInt(71);
    localRenderBuffer.putLong(paramLong);
  }
  
  static void invalidateCurrentContext()
  {
    if (currentContext != null)
    {
      currentContext.invalidateContext();
      currentContext = null;
    }
    OGLRenderQueue localOGLRenderQueue = OGLRenderQueue.getInstance();
    localOGLRenderQueue.ensureCapacity(4);
    localOGLRenderQueue.getBuffer().putInt(75);
    localOGLRenderQueue.flushNow();
  }
  
  public RenderQueue getRenderQueue()
  {
    return OGLRenderQueue.getInstance();
  }
  
  static final native String getOGLIdString();
  
  public void saveState()
  {
    invalidateContext();
    invalidateCurrentContext();
    setScratchSurface(config);
    rq.ensureCapacity(4);
    buf.putInt(78);
    rq.flushNow();
  }
  
  public void restoreState()
  {
    invalidateContext();
    invalidateCurrentContext();
    setScratchSurface(config);
    rq.ensureCapacity(4);
    buf.putInt(79);
    rq.flushNow();
  }
  
  static class OGLContextCaps
    extends ContextCapabilities
  {
    static final int CAPS_EXT_FBOBJECT = 12;
    static final int CAPS_STORED_ALPHA = 2;
    static final int CAPS_DOUBLEBUFFERED = 65536;
    static final int CAPS_EXT_LCD_SHADER = 131072;
    static final int CAPS_EXT_BIOP_SHADER = 262144;
    static final int CAPS_EXT_GRAD_SHADER = 524288;
    static final int CAPS_EXT_TEXRECT = 1048576;
    static final int CAPS_EXT_TEXBARRIER = 2097152;
    
    OGLContextCaps(int paramInt, String paramString)
    {
      super(paramString);
    }
    
    public String toString()
    {
      StringBuffer localStringBuffer = new StringBuffer(super.toString());
      if ((caps & 0xC) != 0) {
        localStringBuffer.append("CAPS_EXT_FBOBJECT|");
      }
      if ((caps & 0x2) != 0) {
        localStringBuffer.append("CAPS_STORED_ALPHA|");
      }
      if ((caps & 0x10000) != 0) {
        localStringBuffer.append("CAPS_DOUBLEBUFFERED|");
      }
      if ((caps & 0x20000) != 0) {
        localStringBuffer.append("CAPS_EXT_LCD_SHADER|");
      }
      if ((caps & 0x40000) != 0) {
        localStringBuffer.append("CAPS_BIOP_SHADER|");
      }
      if ((caps & 0x80000) != 0) {
        localStringBuffer.append("CAPS_EXT_GRAD_SHADER|");
      }
      if ((caps & 0x100000) != 0) {
        localStringBuffer.append("CAPS_EXT_TEXRECT|");
      }
      if ((caps & 0x200000) != 0) {
        localStringBuffer.append("CAPS_EXT_TEXBARRIER|");
      }
      return localStringBuffer.toString();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\opengl\OGLContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */