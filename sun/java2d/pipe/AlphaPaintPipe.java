package sun.java2d.pipe;

import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.lang.ref.WeakReference;
import sun.awt.image.BufImgSurfaceData;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.Blit;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.MaskBlit;

public class AlphaPaintPipe
  implements CompositePipe
{
  static WeakReference cachedLastRaster;
  static WeakReference cachedLastColorModel;
  static WeakReference cachedLastData;
  private static final int TILE_SIZE = 32;
  
  public AlphaPaintPipe() {}
  
  public Object startSequence(SunGraphics2D paramSunGraphics2D, Shape paramShape, Rectangle paramRectangle, int[] paramArrayOfInt)
  {
    PaintContext localPaintContext = paint.createContext(paramSunGraphics2D.getDeviceColorModel(), paramRectangle, paramShape.getBounds2D(), paramSunGraphics2D.cloneTransform(), paramSunGraphics2D.getRenderingHints());
    return new TileContext(paramSunGraphics2D, localPaintContext);
  }
  
  public boolean needTile(Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return true;
  }
  
  public void renderPathTile(Object paramObject, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    TileContext localTileContext = (TileContext)paramObject;
    PaintContext localPaintContext = paintCtxt;
    SunGraphics2D localSunGraphics2D = sunG2D;
    SurfaceData localSurfaceData1 = dstData;
    SurfaceData localSurfaceData2 = null;
    Object localObject1 = null;
    if ((lastData != null) && (lastRaster != null))
    {
      localSurfaceData2 = (SurfaceData)lastData.get();
      localObject1 = (Raster)lastRaster.get();
      if ((localSurfaceData2 == null) || (localObject1 == null))
      {
        localSurfaceData2 = null;
        localObject1 = null;
      }
    }
    ColorModel localColorModel = paintModel;
    for (int i = 0; i < paramInt6; i += 32)
    {
      int j = paramInt4 + i;
      int k = Math.min(paramInt6 - i, 32);
      for (int m = 0; m < paramInt5; m += 32)
      {
        int n = paramInt3 + m;
        int i1 = Math.min(paramInt5 - m, 32);
        Raster localRaster = localPaintContext.getRaster(n, j, i1, k);
        if ((localRaster.getMinX() != 0) || (localRaster.getMinY() != 0)) {
          localRaster = localRaster.createTranslatedChild(0, 0);
        }
        Object localObject2;
        if (localObject1 != localRaster)
        {
          localObject1 = localRaster;
          lastRaster = new WeakReference(localObject1);
          localObject2 = new BufferedImage(localColorModel, (WritableRaster)localRaster, localColorModel.isAlphaPremultiplied(), null);
          localSurfaceData2 = BufImgSurfaceData.createData((BufferedImage)localObject2);
          lastData = new WeakReference(localSurfaceData2);
          lastMask = null;
          lastBlit = null;
        }
        if (paramArrayOfByte == null)
        {
          if (lastBlit == null)
          {
            localObject2 = imageComp;
            if ((CompositeType.SrcOverNoEa.equals(localObject2)) && (localColorModel.getTransparency() == 1)) {
              localObject2 = CompositeType.SrcNoEa;
            }
            lastBlit = Blit.getFromCache(localSurfaceData2.getSurfaceType(), (CompositeType)localObject2, localSurfaceData1.getSurfaceType());
          }
          lastBlit.Blit(localSurfaceData2, localSurfaceData1, composite, null, 0, 0, n, j, i1, k);
        }
        else
        {
          if (lastMask == null)
          {
            localObject2 = imageComp;
            if ((CompositeType.SrcOverNoEa.equals(localObject2)) && (localColorModel.getTransparency() == 1)) {
              localObject2 = CompositeType.SrcNoEa;
            }
            lastMask = MaskBlit.getFromCache(localSurfaceData2.getSurfaceType(), (CompositeType)localObject2, localSurfaceData1.getSurfaceType());
          }
          int i2 = paramInt1 + i * paramInt2 + m;
          lastMask.MaskBlit(localSurfaceData2, localSurfaceData1, composite, null, 0, 0, n, j, i1, k, paramArrayOfByte, i2, paramInt2);
        }
      }
    }
  }
  
  public void skipTile(Object paramObject, int paramInt1, int paramInt2) {}
  
  public void endSequence(Object paramObject)
  {
    TileContext localTileContext = (TileContext)paramObject;
    if (paintCtxt != null) {
      paintCtxt.dispose();
    }
    synchronized (AlphaPaintPipe.class)
    {
      if (lastData != null)
      {
        cachedLastRaster = lastRaster;
        if ((cachedLastColorModel == null) || (cachedLastColorModel.get() != paintModel)) {
          cachedLastColorModel = new WeakReference(paintModel);
        }
        cachedLastData = lastData;
      }
    }
  }
  
  static class TileContext
  {
    SunGraphics2D sunG2D;
    PaintContext paintCtxt;
    ColorModel paintModel;
    WeakReference lastRaster;
    WeakReference lastData;
    MaskBlit lastMask;
    Blit lastBlit;
    SurfaceData dstData;
    
    public TileContext(SunGraphics2D paramSunGraphics2D, PaintContext paramPaintContext)
    {
      sunG2D = paramSunGraphics2D;
      paintCtxt = paramPaintContext;
      paintModel = paramPaintContext.getColorModel();
      dstData = paramSunGraphics2D.getSurfaceData();
      synchronized (AlphaPaintPipe.class)
      {
        if ((AlphaPaintPipe.cachedLastColorModel != null) && (AlphaPaintPipe.cachedLastColorModel.get() == paintModel))
        {
          lastRaster = AlphaPaintPipe.cachedLastRaster;
          lastData = AlphaPaintPipe.cachedLastData;
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\pipe\AlphaPaintPipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */