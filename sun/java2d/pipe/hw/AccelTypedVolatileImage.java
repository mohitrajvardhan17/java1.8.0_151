package sun.java2d.pipe.hw;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import sun.awt.image.SunVolatileImage;

public class AccelTypedVolatileImage
  extends SunVolatileImage
{
  public AccelTypedVolatileImage(GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super(null, paramGraphicsConfiguration, paramInt1, paramInt2, null, paramInt3, null, paramInt4);
  }
  
  public Graphics2D createGraphics()
  {
    if (getForcedAccelSurfaceType() == 3) {
      throw new UnsupportedOperationException("Can't render to a non-RT Texture");
    }
    return super.createGraphics();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\pipe\hw\AccelTypedVolatileImage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */