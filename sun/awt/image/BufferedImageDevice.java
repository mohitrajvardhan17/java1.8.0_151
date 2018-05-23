package sun.awt.image;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;

public class BufferedImageDevice
  extends GraphicsDevice
{
  GraphicsConfiguration gc;
  
  public BufferedImageDevice(BufferedImageGraphicsConfig paramBufferedImageGraphicsConfig)
  {
    gc = paramBufferedImageGraphicsConfig;
  }
  
  public int getType()
  {
    return 2;
  }
  
  public String getIDstring()
  {
    return "BufferedImage";
  }
  
  public GraphicsConfiguration[] getConfigurations()
  {
    return new GraphicsConfiguration[] { gc };
  }
  
  public GraphicsConfiguration getDefaultConfiguration()
  {
    return gc;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\image\BufferedImageDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */