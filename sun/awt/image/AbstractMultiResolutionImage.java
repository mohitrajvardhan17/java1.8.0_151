package sun.awt.image;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;

public abstract class AbstractMultiResolutionImage
  extends Image
  implements MultiResolutionImage
{
  public AbstractMultiResolutionImage() {}
  
  public int getWidth(ImageObserver paramImageObserver)
  {
    return getBaseImage().getWidth(null);
  }
  
  public int getHeight(ImageObserver paramImageObserver)
  {
    return getBaseImage().getHeight(null);
  }
  
  public ImageProducer getSource()
  {
    return getBaseImage().getSource();
  }
  
  public Graphics getGraphics()
  {
    return getBaseImage().getGraphics();
  }
  
  public Object getProperty(String paramString, ImageObserver paramImageObserver)
  {
    return getBaseImage().getProperty(paramString, paramImageObserver);
  }
  
  protected abstract Image getBaseImage();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\image\AbstractMultiResolutionImage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */