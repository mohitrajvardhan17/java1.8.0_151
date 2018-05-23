package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.VolatileImage;
import java.awt.image.WritableRaster;
import sun.awt.image.SunVolatileImage;

public abstract class GraphicsConfiguration
{
  private static BufferCapabilities defaultBufferCaps;
  private static ImageCapabilities defaultImageCaps;
  
  protected GraphicsConfiguration() {}
  
  public abstract GraphicsDevice getDevice();
  
  public BufferedImage createCompatibleImage(int paramInt1, int paramInt2)
  {
    ColorModel localColorModel = getColorModel();
    WritableRaster localWritableRaster = localColorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
    return new BufferedImage(localColorModel, localWritableRaster, localColorModel.isAlphaPremultiplied(), null);
  }
  
  public BufferedImage createCompatibleImage(int paramInt1, int paramInt2, int paramInt3)
  {
    if (getColorModel().getTransparency() == paramInt3) {
      return createCompatibleImage(paramInt1, paramInt2);
    }
    ColorModel localColorModel = getColorModel(paramInt3);
    if (localColorModel == null) {
      throw new IllegalArgumentException("Unknown transparency: " + paramInt3);
    }
    WritableRaster localWritableRaster = localColorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
    return new BufferedImage(localColorModel, localWritableRaster, localColorModel.isAlphaPremultiplied(), null);
  }
  
  public VolatileImage createCompatibleVolatileImage(int paramInt1, int paramInt2)
  {
    VolatileImage localVolatileImage = null;
    try
    {
      localVolatileImage = createCompatibleVolatileImage(paramInt1, paramInt2, null, 1);
    }
    catch (AWTException localAWTException)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
    return localVolatileImage;
  }
  
  public VolatileImage createCompatibleVolatileImage(int paramInt1, int paramInt2, int paramInt3)
  {
    VolatileImage localVolatileImage = null;
    try
    {
      localVolatileImage = createCompatibleVolatileImage(paramInt1, paramInt2, null, paramInt3);
    }
    catch (AWTException localAWTException)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
    return localVolatileImage;
  }
  
  public VolatileImage createCompatibleVolatileImage(int paramInt1, int paramInt2, ImageCapabilities paramImageCapabilities)
    throws AWTException
  {
    return createCompatibleVolatileImage(paramInt1, paramInt2, paramImageCapabilities, 1);
  }
  
  public VolatileImage createCompatibleVolatileImage(int paramInt1, int paramInt2, ImageCapabilities paramImageCapabilities, int paramInt3)
    throws AWTException
  {
    SunVolatileImage localSunVolatileImage = new SunVolatileImage(this, paramInt1, paramInt2, paramInt3, paramImageCapabilities);
    if ((paramImageCapabilities != null) && (paramImageCapabilities.isAccelerated()) && (!localSunVolatileImage.getCapabilities().isAccelerated())) {
      throw new AWTException("Supplied image capabilities could not be met by this graphics configuration.");
    }
    return localSunVolatileImage;
  }
  
  public abstract ColorModel getColorModel();
  
  public abstract ColorModel getColorModel(int paramInt);
  
  public abstract AffineTransform getDefaultTransform();
  
  public abstract AffineTransform getNormalizingTransform();
  
  public abstract Rectangle getBounds();
  
  public BufferCapabilities getBufferCapabilities()
  {
    if (defaultBufferCaps == null) {
      defaultBufferCaps = new DefaultBufferCapabilities(getImageCapabilities());
    }
    return defaultBufferCaps;
  }
  
  public ImageCapabilities getImageCapabilities()
  {
    if (defaultImageCaps == null) {
      defaultImageCaps = new ImageCapabilities(false);
    }
    return defaultImageCaps;
  }
  
  public boolean isTranslucencyCapable()
  {
    return false;
  }
  
  private static class DefaultBufferCapabilities
    extends BufferCapabilities
  {
    public DefaultBufferCapabilities(ImageCapabilities paramImageCapabilities)
    {
      super(paramImageCapabilities, null);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\GraphicsConfiguration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */