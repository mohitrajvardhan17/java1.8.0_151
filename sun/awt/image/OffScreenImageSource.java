package sun.awt.image;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

public class OffScreenImageSource
  implements ImageProducer
{
  BufferedImage image;
  int width;
  int height;
  Hashtable properties;
  private ImageConsumer theConsumer;
  
  public OffScreenImageSource(BufferedImage paramBufferedImage, Hashtable paramHashtable)
  {
    image = paramBufferedImage;
    if (paramHashtable != null) {
      properties = paramHashtable;
    } else {
      properties = new Hashtable();
    }
    width = paramBufferedImage.getWidth();
    height = paramBufferedImage.getHeight();
  }
  
  public OffScreenImageSource(BufferedImage paramBufferedImage)
  {
    this(paramBufferedImage, null);
  }
  
  public synchronized void addConsumer(ImageConsumer paramImageConsumer)
  {
    theConsumer = paramImageConsumer;
    produce();
  }
  
  public synchronized boolean isConsumer(ImageConsumer paramImageConsumer)
  {
    return paramImageConsumer == theConsumer;
  }
  
  public synchronized void removeConsumer(ImageConsumer paramImageConsumer)
  {
    if (theConsumer == paramImageConsumer) {
      theConsumer = null;
    }
  }
  
  public void startProduction(ImageConsumer paramImageConsumer)
  {
    addConsumer(paramImageConsumer);
  }
  
  public void requestTopDownLeftRightResend(ImageConsumer paramImageConsumer) {}
  
  private void sendPixels()
  {
    ColorModel localColorModel = image.getColorModel();
    WritableRaster localWritableRaster = image.getRaster();
    int i = localWritableRaster.getNumDataElements();
    int j = localWritableRaster.getDataBuffer().getDataType();
    int[] arrayOfInt = new int[width * i];
    int k = 1;
    int n;
    int i2;
    Object localObject;
    if ((localColorModel instanceof IndexColorModel))
    {
      byte[] arrayOfByte = new byte[width];
      theConsumer.setColorModel(localColorModel);
      if ((localWritableRaster instanceof ByteComponentRaster))
      {
        k = 0;
        for (n = 0; n < height; n++)
        {
          localWritableRaster.getDataElements(0, n, width, 1, arrayOfByte);
          theConsumer.setPixels(0, n, width, 1, localColorModel, arrayOfByte, 0, width);
        }
      }
      else if ((localWritableRaster instanceof BytePackedRaster))
      {
        k = 0;
        for (n = 0; n < height; n++)
        {
          localWritableRaster.getPixels(0, n, width, 1, arrayOfInt);
          for (i2 = 0; i2 < width; i2++) {
            arrayOfByte[i2] = ((byte)arrayOfInt[i2]);
          }
          theConsumer.setPixels(0, n, width, 1, localColorModel, arrayOfByte, 0, width);
        }
      }
      else if ((j == 2) || (j == 3))
      {
        k = 0;
        for (n = 0; n < height; n++)
        {
          localWritableRaster.getPixels(0, n, width, 1, arrayOfInt);
          theConsumer.setPixels(0, n, width, 1, localColorModel, arrayOfInt, 0, width);
        }
      }
    }
    else if ((localColorModel instanceof DirectColorModel))
    {
      theConsumer.setColorModel(localColorModel);
      k = 0;
      switch (j)
      {
      case 3: 
        for (int m = 0; m < height; m++)
        {
          localWritableRaster.getDataElements(0, m, width, 1, arrayOfInt);
          theConsumer.setPixels(0, m, width, 1, localColorModel, arrayOfInt, 0, width);
        }
        break;
      case 0: 
        localObject = new byte[width];
        for (n = 0; n < height; n++)
        {
          localWritableRaster.getDataElements(0, n, width, 1, localObject);
          for (i2 = 0; i2 < width; i2++) {
            localObject[i2] &= 0xFF;
          }
          theConsumer.setPixels(0, n, width, 1, localColorModel, arrayOfInt, 0, width);
        }
        break;
      case 1: 
        short[] arrayOfShort = new short[width];
        for (i2 = 0; i2 < height; i2++)
        {
          localWritableRaster.getDataElements(0, i2, width, 1, arrayOfShort);
          for (int i3 = 0; i3 < width; i3++) {
            arrayOfShort[i3] &= 0xFFFF;
          }
          theConsumer.setPixels(0, i2, width, 1, localColorModel, arrayOfInt, 0, width);
        }
        break;
      case 2: 
      default: 
        k = 1;
      }
    }
    if (k != 0)
    {
      localObject = ColorModel.getRGBdefault();
      theConsumer.setColorModel((ColorModel)localObject);
      for (int i1 = 0; i1 < height; i1++)
      {
        for (i2 = 0; i2 < width; i2++) {
          arrayOfInt[i2] = image.getRGB(i2, i1);
        }
        theConsumer.setPixels(0, i1, width, 1, (ColorModel)localObject, arrayOfInt, 0, width);
      }
    }
  }
  
  private void produce()
  {
    try
    {
      theConsumer.setDimensions(image.getWidth(), image.getHeight());
      theConsumer.setProperties(properties);
      sendPixels();
      theConsumer.imageComplete(2);
      theConsumer.imageComplete(3);
    }
    catch (NullPointerException localNullPointerException)
    {
      if (theConsumer != null) {
        theConsumer.imageComplete(1);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\image\OffScreenImageSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */