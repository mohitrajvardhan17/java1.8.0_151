package sun.java2d.d3d;

import java.awt.GraphicsConfiguration;
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

abstract class D3DPaints
{
  private static Map<Integer, D3DPaints> impls = new HashMap(4, 1.0F);
  
  D3DPaints() {}
  
  static boolean isValid(SunGraphics2D paramSunGraphics2D)
  {
    D3DPaints localD3DPaints = (D3DPaints)impls.get(Integer.valueOf(paintState));
    return (localD3DPaints != null) && (localD3DPaints.isPaintValid(paramSunGraphics2D));
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
    extends D3DPaints
  {
    private Gradient() {}
    
    boolean isPaintValid(SunGraphics2D paramSunGraphics2D)
    {
      D3DSurfaceData localD3DSurfaceData = (D3DSurfaceData)surfaceData;
      D3DGraphicsDevice localD3DGraphicsDevice = (D3DGraphicsDevice)localD3DSurfaceData.getDeviceConfiguration().getDevice();
      return localD3DGraphicsDevice.isCapPresent(65536);
    }
  }
  
  private static class LinearGradient
    extends D3DPaints.MultiGradient
  {
    private LinearGradient() {}
    
    boolean isPaintValid(SunGraphics2D paramSunGraphics2D)
    {
      LinearGradientPaint localLinearGradientPaint = (LinearGradientPaint)paint;
      if ((localLinearGradientPaint.getFractions().length == 2) && (localLinearGradientPaint.getCycleMethod() != MultipleGradientPaint.CycleMethod.REPEAT) && (localLinearGradientPaint.getColorSpace() != MultipleGradientPaint.ColorSpaceType.LINEAR_RGB))
      {
        D3DSurfaceData localD3DSurfaceData = (D3DSurfaceData)surfaceData;
        D3DGraphicsDevice localD3DGraphicsDevice = (D3DGraphicsDevice)localD3DSurfaceData.getDeviceConfiguration().getDevice();
        if (localD3DGraphicsDevice.isCapPresent(65536)) {
          return true;
        }
      }
      return super.isPaintValid(paramSunGraphics2D);
    }
  }
  
  private static abstract class MultiGradient
    extends D3DPaints
  {
    public static final int MULTI_MAX_FRACTIONS_D3D = 8;
    
    protected MultiGradient() {}
    
    boolean isPaintValid(SunGraphics2D paramSunGraphics2D)
    {
      MultipleGradientPaint localMultipleGradientPaint = (MultipleGradientPaint)paint;
      if (localMultipleGradientPaint.getFractions().length > 8) {
        return false;
      }
      D3DSurfaceData localD3DSurfaceData = (D3DSurfaceData)surfaceData;
      D3DGraphicsDevice localD3DGraphicsDevice = (D3DGraphicsDevice)localD3DSurfaceData.getDeviceConfiguration().getDevice();
      return localD3DGraphicsDevice.isCapPresent(65536);
    }
  }
  
  private static class RadialGradient
    extends D3DPaints.MultiGradient
  {
    private RadialGradient() {}
  }
  
  private static class Texture
    extends D3DPaints
  {
    private Texture() {}
    
    public boolean isPaintValid(SunGraphics2D paramSunGraphics2D)
    {
      TexturePaint localTexturePaint = (TexturePaint)paint;
      D3DSurfaceData localD3DSurfaceData1 = (D3DSurfaceData)surfaceData;
      BufferedImage localBufferedImage = localTexturePaint.getImage();
      D3DGraphicsDevice localD3DGraphicsDevice = (D3DGraphicsDevice)localD3DSurfaceData1.getDeviceConfiguration().getDevice();
      int i = localBufferedImage.getWidth();
      int j = localBufferedImage.getHeight();
      if ((!localD3DGraphicsDevice.isCapPresent(32)) && (((i & i - 1) != 0) || ((j & j - 1) != 0))) {
        return false;
      }
      if ((!localD3DGraphicsDevice.isCapPresent(64)) && (i != j)) {
        return false;
      }
      SurfaceData localSurfaceData = localD3DSurfaceData1.getSourceSurfaceData(localBufferedImage, 0, CompositeType.SrcOver, null);
      if (!(localSurfaceData instanceof D3DSurfaceData))
      {
        localSurfaceData = localD3DSurfaceData1.getSourceSurfaceData(localBufferedImage, 0, CompositeType.SrcOver, null);
        if (!(localSurfaceData instanceof D3DSurfaceData)) {
          return false;
        }
      }
      D3DSurfaceData localD3DSurfaceData2 = (D3DSurfaceData)localSurfaceData;
      return localD3DSurfaceData2.getType() == 3;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\d3d\D3DPaints.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */