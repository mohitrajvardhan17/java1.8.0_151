package sun.awt.image;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.util.Hashtable;

public class ToolkitImage
  extends Image
{
  ImageProducer source;
  InputStreamImageSource src;
  ImageRepresentation imagerep;
  private int width = -1;
  private int height = -1;
  private Hashtable properties;
  private int availinfo;
  
  protected ToolkitImage() {}
  
  public ToolkitImage(ImageProducer paramImageProducer)
  {
    source = paramImageProducer;
    if ((paramImageProducer instanceof InputStreamImageSource)) {
      src = ((InputStreamImageSource)paramImageProducer);
    }
  }
  
  public ImageProducer getSource()
  {
    if (src != null) {
      src.checkSecurity(null, false);
    }
    return source;
  }
  
  public int getWidth()
  {
    if (src != null) {
      src.checkSecurity(null, false);
    }
    if ((availinfo & 0x1) == 0) {
      reconstruct(1);
    }
    return width;
  }
  
  public synchronized int getWidth(ImageObserver paramImageObserver)
  {
    if (src != null) {
      src.checkSecurity(null, false);
    }
    if ((availinfo & 0x1) == 0)
    {
      addWatcher(paramImageObserver, true);
      if ((availinfo & 0x1) == 0) {
        return -1;
      }
    }
    return width;
  }
  
  public int getHeight()
  {
    if (src != null) {
      src.checkSecurity(null, false);
    }
    if ((availinfo & 0x2) == 0) {
      reconstruct(2);
    }
    return height;
  }
  
  public synchronized int getHeight(ImageObserver paramImageObserver)
  {
    if (src != null) {
      src.checkSecurity(null, false);
    }
    if ((availinfo & 0x2) == 0)
    {
      addWatcher(paramImageObserver, true);
      if ((availinfo & 0x2) == 0) {
        return -1;
      }
    }
    return height;
  }
  
  public Object getProperty(String paramString, ImageObserver paramImageObserver)
  {
    if (paramString == null) {
      throw new NullPointerException("null property name is not allowed");
    }
    if (src != null) {
      src.checkSecurity(null, false);
    }
    if (properties == null)
    {
      addWatcher(paramImageObserver, true);
      if (properties == null) {
        return null;
      }
    }
    Object localObject = properties.get(paramString);
    if (localObject == null) {
      localObject = Image.UndefinedProperty;
    }
    return localObject;
  }
  
  public boolean hasError()
  {
    if (src != null) {
      src.checkSecurity(null, false);
    }
    return (availinfo & 0x40) != 0;
  }
  
  public int check(ImageObserver paramImageObserver)
  {
    if (src != null) {
      src.checkSecurity(null, false);
    }
    if (((availinfo & 0x40) == 0) && (((availinfo ^ 0xFFFFFFFF) & 0x7) != 0)) {
      addWatcher(paramImageObserver, false);
    }
    return availinfo;
  }
  
  public void preload(ImageObserver paramImageObserver)
  {
    if (src != null) {
      src.checkSecurity(null, false);
    }
    if ((availinfo & 0x20) == 0) {
      addWatcher(paramImageObserver, true);
    }
  }
  
  private synchronized void addWatcher(ImageObserver paramImageObserver, boolean paramBoolean)
  {
    if ((availinfo & 0x40) != 0)
    {
      if (paramImageObserver != null) {
        paramImageObserver.imageUpdate(this, 192, -1, -1, -1, -1);
      }
      return;
    }
    ImageRepresentation localImageRepresentation = getImageRep();
    localImageRepresentation.addWatcher(paramImageObserver);
    if (paramBoolean) {
      localImageRepresentation.startProduction();
    }
  }
  
  private synchronized void reconstruct(int paramInt)
  {
    if ((paramInt & (availinfo ^ 0xFFFFFFFF)) != 0)
    {
      if ((availinfo & 0x40) != 0) {
        return;
      }
      ImageRepresentation localImageRepresentation = getImageRep();
      localImageRepresentation.startProduction();
      while ((paramInt & (availinfo ^ 0xFFFFFFFF)) != 0)
      {
        try
        {
          wait();
        }
        catch (InterruptedException localInterruptedException)
        {
          Thread.currentThread().interrupt();
          return;
        }
        if ((availinfo & 0x40) != 0) {}
      }
    }
  }
  
  synchronized void addInfo(int paramInt)
  {
    availinfo |= paramInt;
    notifyAll();
  }
  
  void setDimensions(int paramInt1, int paramInt2)
  {
    width = paramInt1;
    height = paramInt2;
    addInfo(3);
  }
  
  void setProperties(Hashtable paramHashtable)
  {
    if (paramHashtable == null) {
      paramHashtable = new Hashtable();
    }
    properties = paramHashtable;
    addInfo(4);
  }
  
  synchronized void infoDone(int paramInt)
  {
    if ((paramInt == 1) || (((availinfo ^ 0xFFFFFFFF) & 0x3) != 0)) {
      addInfo(64);
    } else if ((availinfo & 0x4) == 0) {
      setProperties(null);
    }
  }
  
  public void flush()
  {
    if (src != null) {
      src.checkSecurity(null, false);
    }
    ImageRepresentation localImageRepresentation;
    synchronized (this)
    {
      availinfo &= 0xFFFFFFBF;
      localImageRepresentation = imagerep;
      imagerep = null;
    }
    if (localImageRepresentation != null) {
      localImageRepresentation.abort();
    }
    if (src != null) {
      src.flush();
    }
  }
  
  protected ImageRepresentation makeImageRep()
  {
    return new ImageRepresentation(this, ColorModel.getRGBdefault(), false);
  }
  
  public synchronized ImageRepresentation getImageRep()
  {
    if (src != null) {
      src.checkSecurity(null, false);
    }
    if (imagerep == null) {
      imagerep = makeImageRep();
    }
    return imagerep;
  }
  
  public Graphics getGraphics()
  {
    throw new UnsupportedOperationException("getGraphics() not valid for images created with createImage(producer)");
  }
  
  public ColorModel getColorModel()
  {
    ImageRepresentation localImageRepresentation = getImageRep();
    return localImageRepresentation.getColorModel();
  }
  
  public BufferedImage getBufferedImage()
  {
    ImageRepresentation localImageRepresentation = getImageRep();
    return localImageRepresentation.getBufferedImage();
  }
  
  public void setAccelerationPriority(float paramFloat)
  {
    super.setAccelerationPriority(paramFloat);
    ImageRepresentation localImageRepresentation = getImageRep();
    localImageRepresentation.setAccelerationPriority(accelerationPriority);
  }
  
  static {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\image\ToolkitImage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */