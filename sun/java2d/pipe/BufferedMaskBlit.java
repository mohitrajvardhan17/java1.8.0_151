package sun.java2d.pipe;

import java.awt.AlphaComposite;
import java.awt.Composite;
import sun.java2d.SurfaceData;
import sun.java2d.loops.Blit;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.MaskBlit;
import sun.java2d.loops.SurfaceType;

public abstract class BufferedMaskBlit
  extends MaskBlit
{
  private static final int ST_INT_ARGB = 0;
  private static final int ST_INT_ARGB_PRE = 1;
  private static final int ST_INT_RGB = 2;
  private static final int ST_INT_BGR = 3;
  private final RenderQueue rq;
  private final int srcTypeVal;
  private Blit blitop;
  
  protected BufferedMaskBlit(RenderQueue paramRenderQueue, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2)
  {
    super(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    rq = paramRenderQueue;
    if (paramSurfaceType1 == SurfaceType.IntArgb) {
      srcTypeVal = 0;
    } else if (paramSurfaceType1 == SurfaceType.IntArgbPre) {
      srcTypeVal = 1;
    } else if (paramSurfaceType1 == SurfaceType.IntRgb) {
      srcTypeVal = 2;
    } else if (paramSurfaceType1 == SurfaceType.IntBgr) {
      srcTypeVal = 3;
    } else {
      throw new InternalError("unrecognized source surface type");
    }
  }
  
  public void MaskBlit(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, byte[] paramArrayOfByte, int paramInt7, int paramInt8)
  {
    if ((paramInt5 <= 0) || (paramInt6 <= 0)) {
      return;
    }
    if (paramArrayOfByte == null)
    {
      if (blitop == null) {
        blitop = Blit.getFromCache(paramSurfaceData1.getSurfaceType(), CompositeType.AnyAlpha, getDestType());
      }
      blitop.Blit(paramSurfaceData1, paramSurfaceData2, paramComposite, paramRegion, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
      return;
    }
    AlphaComposite localAlphaComposite = (AlphaComposite)paramComposite;
    if (localAlphaComposite.getRule() != 3) {
      paramComposite = AlphaComposite.SrcOver;
    }
    rq.lock();
    try
    {
      validateContext(paramSurfaceData2, paramComposite, paramRegion);
      RenderBuffer localRenderBuffer = rq.getBuffer();
      int i = 20 + paramInt5 * paramInt6 * 4;
      rq.ensureCapacity(i);
      int j = enqueueTile(localRenderBuffer.getAddress(), localRenderBuffer.position(), paramSurfaceData1, paramSurfaceData1.getNativeOps(), srcTypeVal, paramArrayOfByte, paramArrayOfByte.length, paramInt7, paramInt8, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
      localRenderBuffer.position(j);
    }
    finally
    {
      rq.unlock();
    }
  }
  
  private native int enqueueTile(long paramLong1, int paramInt1, SurfaceData paramSurfaceData, long paramLong2, int paramInt2, byte[] paramArrayOfByte, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10, int paramInt11);
  
  protected abstract void validateContext(SurfaceData paramSurfaceData, Composite paramComposite, Region paramRegion);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\pipe\BufferedMaskBlit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */