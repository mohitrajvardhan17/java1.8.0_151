package sun.awt.image;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.SystemColor;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageProducer;
import java.awt.image.WritableRaster;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

public class OffScreenImage
  extends BufferedImage
{
  protected Component c;
  private OffScreenImageSource osis;
  private Font defaultFont;
  
  public OffScreenImage(Component paramComponent, ColorModel paramColorModel, WritableRaster paramWritableRaster, boolean paramBoolean)
  {
    super(paramColorModel, paramWritableRaster, paramBoolean, null);
    c = paramComponent;
    initSurface(paramWritableRaster.getWidth(), paramWritableRaster.getHeight());
  }
  
  public Graphics getGraphics()
  {
    return createGraphics();
  }
  
  public Graphics2D createGraphics()
  {
    if (c == null)
    {
      localObject1 = GraphicsEnvironment.getLocalGraphicsEnvironment();
      return ((GraphicsEnvironment)localObject1).createGraphics(this);
    }
    Object localObject1 = c.getBackground();
    if (localObject1 == null) {
      localObject1 = SystemColor.window;
    }
    Object localObject2 = c.getForeground();
    if (localObject2 == null) {
      localObject2 = SystemColor.windowText;
    }
    Font localFont = c.getFont();
    if (localFont == null)
    {
      if (defaultFont == null) {
        defaultFont = new Font("Dialog", 0, 12);
      }
      localFont = defaultFont;
    }
    return new SunGraphics2D(SurfaceData.getPrimarySurfaceData(this), (Color)localObject2, (Color)localObject1, localFont);
  }
  
  private void initSurface(int paramInt1, int paramInt2)
  {
    Graphics2D localGraphics2D = createGraphics();
    try
    {
      localGraphics2D.clearRect(0, 0, paramInt1, paramInt2);
    }
    finally
    {
      localGraphics2D.dispose();
    }
  }
  
  public ImageProducer getSource()
  {
    if (osis == null) {
      osis = new OffScreenImageSource(this);
    }
    return osis;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\image\OffScreenImage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */