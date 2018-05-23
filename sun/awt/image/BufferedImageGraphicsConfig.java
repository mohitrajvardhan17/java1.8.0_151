package sun.awt.image;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class BufferedImageGraphicsConfig
  extends GraphicsConfiguration
{
  private static final int numconfigs = 12;
  private static BufferedImageGraphicsConfig[] configs = new BufferedImageGraphicsConfig[12];
  GraphicsDevice gd;
  ColorModel model;
  Raster raster;
  int width;
  int height;
  
  public static BufferedImageGraphicsConfig getConfig(BufferedImage paramBufferedImage)
  {
    int i = paramBufferedImage.getType();
    if ((i > 0) && (i < 12))
    {
      localBufferedImageGraphicsConfig = configs[i];
      if (localBufferedImageGraphicsConfig != null) {
        return localBufferedImageGraphicsConfig;
      }
    }
    BufferedImageGraphicsConfig localBufferedImageGraphicsConfig = new BufferedImageGraphicsConfig(paramBufferedImage, null);
    if ((i > 0) && (i < 12)) {
      configs[i] = localBufferedImageGraphicsConfig;
    }
    return localBufferedImageGraphicsConfig;
  }
  
  public BufferedImageGraphicsConfig(BufferedImage paramBufferedImage, Component paramComponent)
  {
    if (paramComponent == null)
    {
      gd = new BufferedImageDevice(this);
    }
    else
    {
      Graphics2D localGraphics2D = (Graphics2D)paramComponent.getGraphics();
      gd = localGraphics2D.getDeviceConfiguration().getDevice();
    }
    model = paramBufferedImage.getColorModel();
    raster = paramBufferedImage.getRaster().createCompatibleWritableRaster(1, 1);
    width = paramBufferedImage.getWidth();
    height = paramBufferedImage.getHeight();
  }
  
  public GraphicsDevice getDevice()
  {
    return gd;
  }
  
  public BufferedImage createCompatibleImage(int paramInt1, int paramInt2)
  {
    WritableRaster localWritableRaster = raster.createCompatibleWritableRaster(paramInt1, paramInt2);
    return new BufferedImage(model, localWritableRaster, model.isAlphaPremultiplied(), null);
  }
  
  public ColorModel getColorModel()
  {
    return model;
  }
  
  public ColorModel getColorModel(int paramInt)
  {
    if (model.getTransparency() == paramInt) {
      return model;
    }
    switch (paramInt)
    {
    case 1: 
      return new DirectColorModel(24, 16711680, 65280, 255);
    case 2: 
      return new DirectColorModel(25, 16711680, 65280, 255, 16777216);
    case 3: 
      return ColorModel.getRGBdefault();
    }
    return null;
  }
  
  public AffineTransform getDefaultTransform()
  {
    return new AffineTransform();
  }
  
  public AffineTransform getNormalizingTransform()
  {
    return new AffineTransform();
  }
  
  public Rectangle getBounds()
  {
    return new Rectangle(0, 0, width, height);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\image\BufferedImageGraphicsConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */