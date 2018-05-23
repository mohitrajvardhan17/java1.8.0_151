package sun.java2d.opengl;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.LookupOp;
import java.awt.image.RescaleOp;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.pipe.BufferedBufImgOps;

class OGLBufImgOps
  extends BufferedBufImgOps
{
  OGLBufImgOps() {}
  
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
    if ((!(localSurfaceData1 instanceof OGLSurfaceData)) || (interpolationType == 3) || (compositeState > 1)) {
      return false;
    }
    SurfaceData localSurfaceData2 = localSurfaceData1.getSourceSurfaceData(paramBufferedImage, 0, CompositeType.SrcOver, null);
    if (!(localSurfaceData2 instanceof OGLSurfaceData))
    {
      localSurfaceData2 = localSurfaceData1.getSourceSurfaceData(paramBufferedImage, 0, CompositeType.SrcOver, null);
      if (!(localSurfaceData2 instanceof OGLSurfaceData)) {
        return false;
      }
    }
    OGLSurfaceData localOGLSurfaceData = (OGLSurfaceData)localSurfaceData2;
    OGLGraphicsConfig localOGLGraphicsConfig = localOGLSurfaceData.getOGLGraphicsConfig();
    if ((localOGLSurfaceData.getType() != 3) || (!localOGLGraphicsConfig.isCapPresent(262144))) {
      return false;
    }
    int i = paramBufferedImage.getWidth();
    int j = paramBufferedImage.getHeight();
    OGLBlitLoops.IsoBlit(localSurfaceData2, localSurfaceData1, paramBufferedImage, paramBufferedImageOp, composite, paramSunGraphics2D.getCompClip(), transform, interpolationType, 0, 0, i, j, paramInt1, paramInt2, paramInt1 + i, paramInt2 + j, true);
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\opengl\OGLBufImgOps.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */