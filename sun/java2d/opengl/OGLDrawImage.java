package sun.java2d.opengl;

import java.awt.Color;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.SurfaceType;
import sun.java2d.loops.TransformBlit;
import sun.java2d.pipe.DrawImage;

public class OGLDrawImage
  extends DrawImage
{
  public OGLDrawImage() {}
  
  protected void renderImageXform(SunGraphics2D paramSunGraphics2D, Image paramImage, AffineTransform paramAffineTransform, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, Color paramColor)
  {
    if (paramInt1 != 3)
    {
      SurfaceData localSurfaceData1 = surfaceData;
      SurfaceData localSurfaceData2 = localSurfaceData1.getSourceSurfaceData(paramImage, 4, imageComp, paramColor);
      if ((localSurfaceData2 != null) && (!isBgOperation(localSurfaceData2, paramColor)) && ((localSurfaceData2.getSurfaceType() == OGLSurfaceData.OpenGLTexture) || (localSurfaceData2.getSurfaceType() == OGLSurfaceData.OpenGLSurfaceRTT) || (paramInt1 == 1)))
      {
        SurfaceType localSurfaceType1 = localSurfaceData2.getSurfaceType();
        SurfaceType localSurfaceType2 = localSurfaceData1.getSurfaceType();
        TransformBlit localTransformBlit = TransformBlit.getFromCache(localSurfaceType1, imageComp, localSurfaceType2);
        if (localTransformBlit != null)
        {
          localTransformBlit.Transform(localSurfaceData2, localSurfaceData1, composite, paramSunGraphics2D.getCompClip(), paramAffineTransform, paramInt1, paramInt2, paramInt3, 0, 0, paramInt4 - paramInt2, paramInt5 - paramInt3);
          return;
        }
      }
    }
    super.renderImageXform(paramSunGraphics2D, paramImage, paramAffineTransform, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramColor);
  }
  
  public void transformImage(SunGraphics2D paramSunGraphics2D, BufferedImage paramBufferedImage, BufferedImageOp paramBufferedImageOp, int paramInt1, int paramInt2)
  {
    if (paramBufferedImageOp != null)
    {
      if ((paramBufferedImageOp instanceof AffineTransformOp))
      {
        AffineTransformOp localAffineTransformOp = (AffineTransformOp)paramBufferedImageOp;
        transformImage(paramSunGraphics2D, paramBufferedImage, paramInt1, paramInt2, localAffineTransformOp.getTransform(), localAffineTransformOp.getInterpolationType());
        return;
      }
      if (OGLBufImgOps.renderImageWithOp(paramSunGraphics2D, paramBufferedImage, paramBufferedImageOp, paramInt1, paramInt2)) {
        return;
      }
      paramBufferedImage = paramBufferedImageOp.filter(paramBufferedImage, null);
    }
    copyImage(paramSunGraphics2D, paramBufferedImage, paramInt1, paramInt2, null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\opengl\OGLDrawImage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */