package java.awt.image;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class MemoryImageSource
  implements ImageProducer
{
  int width;
  int height;
  ColorModel model;
  Object pixels;
  int pixeloffset;
  int pixelscan;
  Hashtable properties;
  Vector theConsumers = new Vector();
  boolean animating;
  boolean fullbuffers;
  
  public MemoryImageSource(int paramInt1, int paramInt2, ColorModel paramColorModel, byte[] paramArrayOfByte, int paramInt3, int paramInt4)
  {
    initialize(paramInt1, paramInt2, paramColorModel, paramArrayOfByte, paramInt3, paramInt4, null);
  }
  
  public MemoryImageSource(int paramInt1, int paramInt2, ColorModel paramColorModel, byte[] paramArrayOfByte, int paramInt3, int paramInt4, Hashtable<?, ?> paramHashtable)
  {
    initialize(paramInt1, paramInt2, paramColorModel, paramArrayOfByte, paramInt3, paramInt4, paramHashtable);
  }
  
  public MemoryImageSource(int paramInt1, int paramInt2, ColorModel paramColorModel, int[] paramArrayOfInt, int paramInt3, int paramInt4)
  {
    initialize(paramInt1, paramInt2, paramColorModel, paramArrayOfInt, paramInt3, paramInt4, null);
  }
  
  public MemoryImageSource(int paramInt1, int paramInt2, ColorModel paramColorModel, int[] paramArrayOfInt, int paramInt3, int paramInt4, Hashtable<?, ?> paramHashtable)
  {
    initialize(paramInt1, paramInt2, paramColorModel, paramArrayOfInt, paramInt3, paramInt4, paramHashtable);
  }
  
  private void initialize(int paramInt1, int paramInt2, ColorModel paramColorModel, Object paramObject, int paramInt3, int paramInt4, Hashtable paramHashtable)
  {
    width = paramInt1;
    height = paramInt2;
    model = paramColorModel;
    pixels = paramObject;
    pixeloffset = paramInt3;
    pixelscan = paramInt4;
    if (paramHashtable == null) {
      paramHashtable = new Hashtable();
    }
    properties = paramHashtable;
  }
  
  public MemoryImageSource(int paramInt1, int paramInt2, int[] paramArrayOfInt, int paramInt3, int paramInt4)
  {
    initialize(paramInt1, paramInt2, ColorModel.getRGBdefault(), paramArrayOfInt, paramInt3, paramInt4, null);
  }
  
  public MemoryImageSource(int paramInt1, int paramInt2, int[] paramArrayOfInt, int paramInt3, int paramInt4, Hashtable<?, ?> paramHashtable)
  {
    initialize(paramInt1, paramInt2, ColorModel.getRGBdefault(), paramArrayOfInt, paramInt3, paramInt4, paramHashtable);
  }
  
  public synchronized void addConsumer(ImageConsumer paramImageConsumer)
  {
    if (theConsumers.contains(paramImageConsumer)) {
      return;
    }
    theConsumers.addElement(paramImageConsumer);
    try
    {
      initConsumer(paramImageConsumer);
      sendPixels(paramImageConsumer, 0, 0, width, height);
      if (isConsumer(paramImageConsumer))
      {
        paramImageConsumer.imageComplete(animating ? 2 : 3);
        if ((!animating) && (isConsumer(paramImageConsumer)))
        {
          paramImageConsumer.imageComplete(1);
          removeConsumer(paramImageConsumer);
        }
      }
    }
    catch (Exception localException)
    {
      if (isConsumer(paramImageConsumer)) {
        paramImageConsumer.imageComplete(1);
      }
    }
  }
  
  public synchronized boolean isConsumer(ImageConsumer paramImageConsumer)
  {
    return theConsumers.contains(paramImageConsumer);
  }
  
  public synchronized void removeConsumer(ImageConsumer paramImageConsumer)
  {
    theConsumers.removeElement(paramImageConsumer);
  }
  
  public void startProduction(ImageConsumer paramImageConsumer)
  {
    addConsumer(paramImageConsumer);
  }
  
  public void requestTopDownLeftRightResend(ImageConsumer paramImageConsumer) {}
  
  public synchronized void setAnimated(boolean paramBoolean)
  {
    animating = paramBoolean;
    if (!animating)
    {
      Enumeration localEnumeration = theConsumers.elements();
      while (localEnumeration.hasMoreElements())
      {
        ImageConsumer localImageConsumer = (ImageConsumer)localEnumeration.nextElement();
        localImageConsumer.imageComplete(3);
        if (isConsumer(localImageConsumer)) {
          localImageConsumer.imageComplete(1);
        }
      }
      theConsumers.removeAllElements();
    }
  }
  
  public synchronized void setFullBufferUpdates(boolean paramBoolean)
  {
    if (fullbuffers == paramBoolean) {
      return;
    }
    fullbuffers = paramBoolean;
    if (animating)
    {
      Enumeration localEnumeration = theConsumers.elements();
      while (localEnumeration.hasMoreElements())
      {
        ImageConsumer localImageConsumer = (ImageConsumer)localEnumeration.nextElement();
        localImageConsumer.setHints(paramBoolean ? 6 : 1);
      }
    }
  }
  
  public void newPixels()
  {
    newPixels(0, 0, width, height, true);
  }
  
  public synchronized void newPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    newPixels(paramInt1, paramInt2, paramInt3, paramInt4, true);
  }
  
  public synchronized void newPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
  {
    if (animating)
    {
      if (fullbuffers)
      {
        paramInt1 = paramInt2 = 0;
        paramInt3 = width;
        paramInt4 = height;
      }
      else
      {
        if (paramInt1 < 0)
        {
          paramInt3 += paramInt1;
          paramInt1 = 0;
        }
        if (paramInt1 + paramInt3 > width) {
          paramInt3 = width - paramInt1;
        }
        if (paramInt2 < 0)
        {
          paramInt4 += paramInt2;
          paramInt2 = 0;
        }
        if (paramInt2 + paramInt4 > height) {
          paramInt4 = height - paramInt2;
        }
      }
      if (((paramInt3 <= 0) || (paramInt4 <= 0)) && (!paramBoolean)) {
        return;
      }
      Enumeration localEnumeration = theConsumers.elements();
      while (localEnumeration.hasMoreElements())
      {
        ImageConsumer localImageConsumer = (ImageConsumer)localEnumeration.nextElement();
        if ((paramInt3 > 0) && (paramInt4 > 0)) {
          sendPixels(localImageConsumer, paramInt1, paramInt2, paramInt3, paramInt4);
        }
        if ((paramBoolean) && (isConsumer(localImageConsumer))) {
          localImageConsumer.imageComplete(2);
        }
      }
    }
  }
  
  public synchronized void newPixels(byte[] paramArrayOfByte, ColorModel paramColorModel, int paramInt1, int paramInt2)
  {
    pixels = paramArrayOfByte;
    model = paramColorModel;
    pixeloffset = paramInt1;
    pixelscan = paramInt2;
    newPixels();
  }
  
  public synchronized void newPixels(int[] paramArrayOfInt, ColorModel paramColorModel, int paramInt1, int paramInt2)
  {
    pixels = paramArrayOfInt;
    model = paramColorModel;
    pixeloffset = paramInt1;
    pixelscan = paramInt2;
    newPixels();
  }
  
  private void initConsumer(ImageConsumer paramImageConsumer)
  {
    if (isConsumer(paramImageConsumer)) {
      paramImageConsumer.setDimensions(width, height);
    }
    if (isConsumer(paramImageConsumer)) {
      paramImageConsumer.setProperties(properties);
    }
    if (isConsumer(paramImageConsumer)) {
      paramImageConsumer.setColorModel(model);
    }
    if (isConsumer(paramImageConsumer)) {
      paramImageConsumer.setHints(animating ? 1 : fullbuffers ? 6 : 30);
    }
  }
  
  private void sendPixels(ImageConsumer paramImageConsumer, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = pixeloffset + pixelscan * paramInt2 + paramInt1;
    if (isConsumer(paramImageConsumer)) {
      if ((pixels instanceof byte[])) {
        paramImageConsumer.setPixels(paramInt1, paramInt2, paramInt3, paramInt4, model, (byte[])pixels, i, pixelscan);
      } else {
        paramImageConsumer.setPixels(paramInt1, paramInt2, paramInt3, paramInt4, model, (int[])pixels, i, pixelscan);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\MemoryImageSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */