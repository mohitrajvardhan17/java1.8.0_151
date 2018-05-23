package sun.java2d.pipe;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import sun.awt.image.BufImgSurfaceData;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.Blit;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.MaskBlit;

public class GeneralCompositePipe
  implements CompositePipe
{
  public GeneralCompositePipe() {}
  
  public Object startSequence(SunGraphics2D paramSunGraphics2D, Shape paramShape, Rectangle paramRectangle, int[] paramArrayOfInt)
  {
    RenderingHints localRenderingHints = paramSunGraphics2D.getRenderingHints();
    ColorModel localColorModel = paramSunGraphics2D.getDeviceColorModel();
    PaintContext localPaintContext = paint.createContext(localColorModel, paramRectangle, paramShape.getBounds2D(), paramSunGraphics2D.cloneTransform(), localRenderingHints);
    CompositeContext localCompositeContext = composite.createContext(localPaintContext.getColorModel(), localColorModel, localRenderingHints);
    return new TileContext(paramSunGraphics2D, localPaintContext, localCompositeContext, localColorModel);
  }
  
  public boolean needTile(Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return true;
  }
  
  public void renderPathTile(Object paramObject, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    TileContext localTileContext = (TileContext)paramObject;
    PaintContext localPaintContext = paintCtxt;
    CompositeContext localCompositeContext = compCtxt;
    SunGraphics2D localSunGraphics2D = sunG2D;
    Raster localRaster1 = localPaintContext.getRaster(paramInt3, paramInt4, paramInt5, paramInt6);
    ColorModel localColorModel1 = localPaintContext.getColorModel();
    SurfaceData localSurfaceData1 = localSunGraphics2D.getSurfaceData();
    Raster localRaster2 = localSurfaceData1.getRaster(paramInt3, paramInt4, paramInt5, paramInt6);
    WritableRaster localWritableRaster;
    Object localObject1;
    if (((localRaster2 instanceof WritableRaster)) && (paramArrayOfByte == null))
    {
      localWritableRaster = (WritableRaster)localRaster2;
      localWritableRaster = localWritableRaster.createWritableChild(paramInt3, paramInt4, paramInt5, paramInt6, 0, 0, null);
      localObject1 = localWritableRaster;
    }
    else
    {
      localObject1 = localRaster2.createChild(paramInt3, paramInt4, paramInt5, paramInt6, 0, 0, null);
      localWritableRaster = ((Raster)localObject1).createCompatibleWritableRaster();
    }
    localCompositeContext.compose(localRaster1, (Raster)localObject1, localWritableRaster);
    if ((localRaster2 != localWritableRaster) && (localWritableRaster.getParent() != localRaster2)) {
      if (((localRaster2 instanceof WritableRaster)) && (paramArrayOfByte == null))
      {
        ((WritableRaster)localRaster2).setDataElements(paramInt3, paramInt4, localWritableRaster);
      }
      else
      {
        ColorModel localColorModel2 = localSunGraphics2D.getDeviceColorModel();
        BufferedImage localBufferedImage = new BufferedImage(localColorModel2, localWritableRaster, localColorModel2.isAlphaPremultiplied(), null);
        SurfaceData localSurfaceData2 = BufImgSurfaceData.createData(localBufferedImage);
        Object localObject2;
        if (paramArrayOfByte == null)
        {
          localObject2 = Blit.getFromCache(localSurfaceData2.getSurfaceType(), CompositeType.SrcNoEa, localSurfaceData1.getSurfaceType());
          ((Blit)localObject2).Blit(localSurfaceData2, localSurfaceData1, AlphaComposite.Src, null, 0, 0, paramInt3, paramInt4, paramInt5, paramInt6);
        }
        else
        {
          localObject2 = MaskBlit.getFromCache(localSurfaceData2.getSurfaceType(), CompositeType.SrcNoEa, localSurfaceData1.getSurfaceType());
          ((MaskBlit)localObject2).MaskBlit(localSurfaceData2, localSurfaceData1, AlphaComposite.Src, null, 0, 0, paramInt3, paramInt4, paramInt5, paramInt6, paramArrayOfByte, paramInt1, paramInt2);
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
    if (compCtxt != null) {
      compCtxt.dispose();
    }
  }
  
  class TileContext
  {
    SunGraphics2D sunG2D;
    PaintContext paintCtxt;
    CompositeContext compCtxt;
    ColorModel compModel;
    Object pipeState;
    
    public TileContext(SunGraphics2D paramSunGraphics2D, PaintContext paramPaintContext, CompositeContext paramCompositeContext, ColorModel paramColorModel)
    {
      sunG2D = paramSunGraphics2D;
      paintCtxt = paramPaintContext;
      compCtxt = paramCompositeContext;
      compModel = paramColorModel;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\pipe\GeneralCompositePipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */