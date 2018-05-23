package sun.java2d.opengl;

import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.MultipleGradientPaint.ColorSpaceType;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;

abstract class OGLPaints
{
  private static Map<Integer, OGLPaints> impls = new HashMap(4, 1.0F);
  
  OGLPaints() {}
  
  static boolean isValid(SunGraphics2D paramSunGraphics2D)
  {
    OGLPaints localOGLPaints = (OGLPaints)impls.get(Integer.valueOf(paintState));
    return (localOGLPaints != null) && (localOGLPaints.isPaintValid(paramSunGraphics2D));
  }
  
  abstract boolean isPaintValid(SunGraphics2D paramSunGraphics2D);
  
  static
  {
    impls.put(Integer.valueOf(2), new Gradient(null));
    impls.put(Integer.valueOf(3), new LinearGradient(null));
    impls.put(Integer.valueOf(4), new RadialGradient(null));
    impls.put(Integer.valueOf(5), new Texture(null));
  }
  
  private static class Gradient
    extends OGLPaints
  {
    private Gradient() {}
    
    boolean isPaintValid(SunGraphics2D paramSunGraphics2D)
    {
      return true;
    }
  }
  
  private static class LinearGradient
    extends OGLPaints.MultiGradient
  {
    private LinearGradient() {}
    
    boolean isPaintValid(SunGraphics2D paramSunGraphics2D)
    {
      LinearGradientPaint localLinearGradientPaint = (LinearGradientPaint)paint;
      if ((localLinearGradientPaint.getFractions().length == 2) && (localLinearGradientPaint.getCycleMethod() != MultipleGradientPaint.CycleMethod.REPEAT) && (localLinearGradientPaint.getColorSpace() != MultipleGradientPaint.ColorSpaceType.LINEAR_RGB)) {
        return true;
      }
      return super.isPaintValid(paramSunGraphics2D);
    }
  }
  
  private static abstract class MultiGradient
    extends OGLPaints
  {
    protected MultiGradient() {}
    
    boolean isPaintValid(SunGraphics2D paramSunGraphics2D)
    {
      MultipleGradientPaint localMultipleGradientPaint = (MultipleGradientPaint)paint;
      if (localMultipleGradientPaint.getFractions().length > 12) {
        return false;
      }
      OGLSurfaceData localOGLSurfaceData = (OGLSurfaceData)surfaceData;
      OGLGraphicsConfig localOGLGraphicsConfig = localOGLSurfaceData.getOGLGraphicsConfig();
      return localOGLGraphicsConfig.isCapPresent(524288);
    }
  }
  
  private static class RadialGradient
    extends OGLPaints.MultiGradient
  {
    private RadialGradient() {}
  }
  
  private static class Texture
    extends OGLPaints
  {
    private Texture() {}
    
    boolean isPaintValid(SunGraphics2D paramSunGraphics2D)
    {
      TexturePaint localTexturePaint = (TexturePaint)paint;
      OGLSurfaceData localOGLSurfaceData1 = (OGLSurfaceData)surfaceData;
      BufferedImage localBufferedImage = localTexturePaint.getImage();
      if (!localOGLSurfaceData1.isTexNonPow2Available())
      {
        int i = localBufferedImage.getWidth();
        int j = localBufferedImage.getHeight();
        if (((i & i - 1) != 0) || ((j & j - 1) != 0)) {
          return false;
        }
      }
      SurfaceData localSurfaceData = localOGLSurfaceData1.getSourceSurfaceData(localBufferedImage, 0, CompositeType.SrcOver, null);
      if (!(localSurfaceData instanceof OGLSurfaceData))
      {
        localSurfaceData = localOGLSurfaceData1.getSourceSurfaceData(localBufferedImage, 0, CompositeType.SrcOver, null);
        if (!(localSurfaceData instanceof OGLSurfaceData)) {
          return false;
        }
      }
      OGLSurfaceData localOGLSurfaceData2 = (OGLSurfaceData)localSurfaceData;
      return localOGLSurfaceData2.getType() == 3;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\opengl\OGLPaints.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */