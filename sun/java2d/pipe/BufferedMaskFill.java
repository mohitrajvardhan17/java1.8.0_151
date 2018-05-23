package sun.java2d.pipe;

import java.awt.AlphaComposite;
import java.awt.Composite;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.MaskFill;
import sun.java2d.loops.SurfaceType;

public abstract class BufferedMaskFill
  extends MaskFill
{
  protected final RenderQueue rq;
  
  protected BufferedMaskFill(RenderQueue paramRenderQueue, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    super(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    rq = paramRenderQueue;
  }
  
  public void MaskFill(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, Composite paramComposite, final int paramInt1, final int paramInt2, final int paramInt3, final int paramInt4, final byte[] paramArrayOfByte, final int paramInt5, final int paramInt6)
  {
    AlphaComposite localAlphaComposite = (AlphaComposite)paramComposite;
    if (localAlphaComposite.getRule() != 3) {
      paramComposite = AlphaComposite.SrcOver;
    }
    rq.lock();
    try
    {
      validateContext(paramSunGraphics2D, paramComposite, 2);
      int i;
      if (paramArrayOfByte != null) {
        i = paramArrayOfByte.length + 3 & 0xFFFFFFFC;
      } else {
        i = 0;
      }
      int j = 32 + i;
      RenderBuffer localRenderBuffer = rq.getBuffer();
      if (j <= localRenderBuffer.capacity())
      {
        if (j > localRenderBuffer.remaining()) {
          rq.flushNow();
        }
        localRenderBuffer.putInt(32);
        localRenderBuffer.putInt(paramInt1).putInt(paramInt2).putInt(paramInt3).putInt(paramInt4);
        localRenderBuffer.putInt(paramInt5);
        localRenderBuffer.putInt(paramInt6);
        localRenderBuffer.putInt(i);
        if (paramArrayOfByte != null)
        {
          int k = i - paramArrayOfByte.length;
          localRenderBuffer.put(paramArrayOfByte);
          if (k != 0) {
            localRenderBuffer.position(localRenderBuffer.position() + k);
          }
        }
      }
      else
      {
        rq.flushAndInvokeNow(new Runnable()
        {
          public void run()
          {
            maskFill(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramArrayOfByte.length, paramArrayOfByte);
          }
        });
      }
    }
    finally
    {
      rq.unlock();
    }
  }
  
  protected abstract void maskFill(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, byte[] paramArrayOfByte);
  
  protected abstract void validateContext(SunGraphics2D paramSunGraphics2D, Composite paramComposite, int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\pipe\BufferedMaskFill.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */