package java.awt.image;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.ImageCapabilities;
import java.awt.Transparency;

public abstract class VolatileImage
  extends Image
  implements Transparency
{
  public static final int IMAGE_OK = 0;
  public static final int IMAGE_RESTORED = 1;
  public static final int IMAGE_INCOMPATIBLE = 2;
  protected int transparency = 3;
  
  public VolatileImage() {}
  
  public abstract BufferedImage getSnapshot();
  
  public abstract int getWidth();
  
  public abstract int getHeight();
  
  public ImageProducer getSource()
  {
    return getSnapshot().getSource();
  }
  
  public Graphics getGraphics()
  {
    return createGraphics();
  }
  
  public abstract Graphics2D createGraphics();
  
  public abstract int validate(GraphicsConfiguration paramGraphicsConfiguration);
  
  public abstract boolean contentsLost();
  
  public abstract ImageCapabilities getCapabilities();
  
  public int getTransparency()
  {
    return transparency;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\VolatileImage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */