package sun.print;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;

public class PrinterGraphicsConfig
  extends GraphicsConfiguration
{
  static ColorModel theModel;
  GraphicsDevice gd;
  int pageWidth;
  int pageHeight;
  AffineTransform deviceTransform;
  
  public PrinterGraphicsConfig(String paramString, AffineTransform paramAffineTransform, int paramInt1, int paramInt2)
  {
    pageWidth = paramInt1;
    pageHeight = paramInt2;
    deviceTransform = paramAffineTransform;
    gd = new PrinterGraphicsDevice(this, paramString);
  }
  
  public GraphicsDevice getDevice()
  {
    return gd;
  }
  
  public ColorModel getColorModel()
  {
    if (theModel == null)
    {
      BufferedImage localBufferedImage = new BufferedImage(1, 1, 5);
      theModel = localBufferedImage.getColorModel();
    }
    return theModel;
  }
  
  public ColorModel getColorModel(int paramInt)
  {
    switch (paramInt)
    {
    case 1: 
      return getColorModel();
    case 2: 
      return new DirectColorModel(25, 16711680, 65280, 255, 16777216);
    case 3: 
      return ColorModel.getRGBdefault();
    }
    return null;
  }
  
  public AffineTransform getDefaultTransform()
  {
    return new AffineTransform(deviceTransform);
  }
  
  public AffineTransform getNormalizingTransform()
  {
    return new AffineTransform();
  }
  
  public Rectangle getBounds()
  {
    return new Rectangle(0, 0, pageWidth, pageHeight);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\print\PrinterGraphicsConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */