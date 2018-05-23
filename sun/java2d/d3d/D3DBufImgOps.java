package sun.java2d.d3d;

import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.LookupOp;
import java.awt.image.RescaleOp;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.pipe.BufferedBufImgOps;

class D3DBufImgOps
  extends BufferedBufImgOps
{
  D3DBufImgOps() {}
  
  static boolean renderImageWithOp(SunGraphics2D paramSunGraphics2D, BufferedImage paramBufferedImage, BufferedImageOp paramBufferedImageOp, int paramInt1, int paramInt2)
  {
    if ((paramBufferedImageOp instanceof ConvolveOp))
    {
      if (!isConvolveOpValid((ConvolveOp)paramBufferedImageOp)) {
        return false;
      }
    }
    else if ((paramBufferedImageOp instanceof RescaleOp))
    {
      if (!isRescaleOpValid((RescaleOp)paramBufferedImageOp, paramBufferedImage)) {
        return false;
      }
    }
    else if ((paramBufferedImageOp instanceof LookupOp))
    {
      if (!isLookupOpValid((LookupOp)paramBufferedImageOp, paramBufferedImage)) {
        return false;
      }
    }
    else {
      return false;
    }
    SurfaceData localSurfaceData1 = surfaceData;
    if ((!(localSurfaceData1 instanceof D3DSurfaceData)) || (interpolationType == 3) || (compositeState > 1)) {
      return false;
    }
    SurfaceData localSurfaceData2 = localSurfaceData1.getSourceSurfaceData(paramBufferedImage, 0, CompositeType.SrcOver, null);
    if (!(localSurfaceData2 instanceof D3DSurfaceData))
    {
      localSurfaceData2 = localSurfaceData1.getSourceSurfaceData(paramBufferedImage, 0, CompositeType.SrcOver, null);
      if (!(localSurfaceData2 instanceof D3DSurfaceData)) {
        return false;
      }
    }
    D3DSurfaceData localD3DSurfaceData = (D3DSurfaceData)localSurfaceData2;
    D3DGraphicsDevice localD3DGraphicsDevice = (D3DGraphicsDevice)localD3DSurfaceData.getDeviceConfiguration().getDevice();
    if ((localD3DSurfaceData.getType() != 3) || (!localD3DGraphicsDevice.isCapPresent(65536))) {
      return false;
    }
    int i = paramBufferedImage.getWidth();
    int j = paramBufferedImage.getHeight();
    D3DBlitLoops.IsoBlit(localSurfaceData2, localSurfaceData1, paramBufferedImage, paramBufferedImageOp, composite, paramSunGraphics2D.getCompClip(), transform, interpolationType, 0, 0, i, j, paramInt1, paramInt2, paramInt1 + i, paramInt2 + j, true);
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\d3d\D3DBufImgOps.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */