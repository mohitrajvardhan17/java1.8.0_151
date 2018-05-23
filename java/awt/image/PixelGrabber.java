package java.awt.image;

import java.awt.Image;
import java.util.Hashtable;

public class PixelGrabber
  implements ImageConsumer
{
  ImageProducer producer;
  int dstX;
  int dstY;
  int dstW;
  int dstH;
  ColorModel imageModel;
  byte[] bytePixels;
  int[] intPixels;
  int dstOff;
  int dstScan;
  private boolean grabbing;
  private int flags;
  private static final int GRABBEDBITS = 48;
  private static final int DONEBITS = 112;
  
  public PixelGrabber(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, int paramInt5, int paramInt6)
  {
    this(paramImage.getSource(), paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt, paramInt5, paramInt6);
  }
  
  public PixelGrabber(ImageProducer paramImageProducer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, int paramInt5, int paramInt6)
  {
    producer = paramImageProducer;
    dstX = paramInt1;
    dstY = paramInt2;
    dstW = paramInt3;
    dstH = paramInt4;
    dstOff = paramInt5;
    dstScan = paramInt6;
    intPixels = paramArrayOfInt;
    imageModel = ColorModel.getRGBdefault();
  }
  
  public PixelGrabber(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
  {
    producer = paramImage.getSource();
    dstX = paramInt1;
    dstY = paramInt2;
    dstW = paramInt3;
    dstH = paramInt4;
    if (paramBoolean) {
      imageModel = ColorModel.getRGBdefault();
    }
  }
  
  public synchronized void startGrabbing()
  {
    if ((flags & 0x70) != 0) {
      return;
    }
    if (!grabbing)
    {
      grabbing = true;
      flags &= 0xFF7F;
      producer.startProduction(this);
    }
  }
  
  public synchronized void abortGrabbing()
  {
    imageComplete(4);
  }
  
  public boolean grabPixels()
    throws InterruptedException
  {
    return grabPixels(0L);
  }
  
  public synchronized boolean grabPixels(long paramLong)
    throws InterruptedException
  {
    if ((flags & 0x70) != 0) {
      return (flags & 0x30) != 0;
    }
    long l1 = paramLong + System.currentTimeMillis();
    if (!grabbing)
    {
      grabbing = true;
      flags &= 0xFF7F;
      producer.startProduction(this);
    }
    while (grabbing)
    {
      long l2;
      if (paramLong == 0L)
      {
        l2 = 0L;
      }
      else
      {
        l2 = l1 - System.currentTimeMillis();
        if (l2 <= 0L) {
          break;
        }
      }
      wait(l2);
    }
    return (flags & 0x30) != 0;
  }
  
  public synchronized int getStatus()
  {
    return flags;
  }
  
  public synchronized int getWidth()
  {
    return dstW < 0 ? -1 : dstW;
  }
  
  public synchronized int getHeight()
  {
    return dstH < 0 ? -1 : dstH;
  }
  
  public synchronized Object getPixels()
  {
    return bytePixels == null ? intPixels : bytePixels;
  }
  
  public synchronized ColorModel getColorModel()
  {
    return imageModel;
  }
  
  public void setDimensions(int paramInt1, int paramInt2)
  {
    if (dstW < 0) {
      dstW = (paramInt1 - dstX);
    }
    if (dstH < 0) {
      dstH = (paramInt2 - dstY);
    }
    if ((dstW <= 0) || (dstH <= 0))
    {
      imageComplete(3);
    }
    else if ((intPixels == null) && (imageModel == ColorModel.getRGBdefault()))
    {
      intPixels = new int[dstW * dstH];
      dstScan = dstW;
      dstOff = 0;
    }
    flags |= 0x3;
  }
  
  public void setHints(int paramInt) {}
  
  public void setProperties(Hashtable<?, ?> paramHashtable) {}
  
  public void setColorModel(ColorModel paramColorModel) {}
  
  private void convertToRGB()
  {
    int i = dstW * dstH;
    int[] arrayOfInt = new int[i];
    int j;
    if (bytePixels != null) {
      for (j = 0; j < i; j++) {
        arrayOfInt[j] = imageModel.getRGB(bytePixels[j] & 0xFF);
      }
    } else if (intPixels != null) {
      for (j = 0; j < i; j++) {
        arrayOfInt[j] = imageModel.getRGB(intPixels[j]);
      }
    }
    bytePixels = null;
    intPixels = arrayOfInt;
    dstScan = dstW;
    dstOff = 0;
    imageModel = ColorModel.getRGBdefault();
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, byte[] paramArrayOfByte, int paramInt5, int paramInt6)
  {
    if (paramInt2 < dstY)
    {
      i = dstY - paramInt2;
      if (i >= paramInt4) {
        return;
      }
      paramInt5 += paramInt6 * i;
      paramInt2 += i;
      paramInt4 -= i;
    }
    if (paramInt2 + paramInt4 > dstY + dstH)
    {
      paramInt4 = dstY + dstH - paramInt2;
      if (paramInt4 <= 0) {
        return;
      }
    }
    if (paramInt1 < dstX)
    {
      i = dstX - paramInt1;
      if (i >= paramInt3) {
        return;
      }
      paramInt5 += i;
      paramInt1 += i;
      paramInt3 -= i;
    }
    if (paramInt1 + paramInt3 > dstX + dstW)
    {
      paramInt3 = dstX + dstW - paramInt1;
      if (paramInt3 <= 0) {
        return;
      }
    }
    int i = dstOff + (paramInt2 - dstY) * dstScan + (paramInt1 - dstX);
    int j;
    if (intPixels == null)
    {
      if (bytePixels == null)
      {
        bytePixels = new byte[dstW * dstH];
        dstScan = dstW;
        dstOff = 0;
        imageModel = paramColorModel;
      }
      else if (imageModel != paramColorModel)
      {
        convertToRGB();
      }
      if (bytePixels != null) {
        for (j = paramInt4; j > 0; j--)
        {
          System.arraycopy(paramArrayOfByte, paramInt5, bytePixels, i, paramInt3);
          paramInt5 += paramInt6;
          i += dstScan;
        }
      }
    }
    if (intPixels != null)
    {
      j = dstScan - paramInt3;
      int k = paramInt6 - paramInt3;
      for (int m = paramInt4; m > 0; m--)
      {
        for (int n = paramInt3; n > 0; n--) {
          intPixels[(i++)] = paramColorModel.getRGB(paramArrayOfByte[(paramInt5++)] & 0xFF);
        }
        paramInt5 += k;
        i += j;
      }
    }
    flags |= 0x8;
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, int[] paramArrayOfInt, int paramInt5, int paramInt6)
  {
    if (paramInt2 < dstY)
    {
      i = dstY - paramInt2;
      if (i >= paramInt4) {
        return;
      }
      paramInt5 += paramInt6 * i;
      paramInt2 += i;
      paramInt4 -= i;
    }
    if (paramInt2 + paramInt4 > dstY + dstH)
    {
      paramInt4 = dstY + dstH - paramInt2;
      if (paramInt4 <= 0) {
        return;
      }
    }
    if (paramInt1 < dstX)
    {
      i = dstX - paramInt1;
      if (i >= paramInt3) {
        return;
      }
      paramInt5 += i;
      paramInt1 += i;
      paramInt3 -= i;
    }
    if (paramInt1 + paramInt3 > dstX + dstW)
    {
      paramInt3 = dstX + dstW - paramInt1;
      if (paramInt3 <= 0) {
        return;
      }
    }
    if (intPixels == null) {
      if (bytePixels == null)
      {
        intPixels = new int[dstW * dstH];
        dstScan = dstW;
        dstOff = 0;
        imageModel = paramColorModel;
      }
      else
      {
        convertToRGB();
      }
    }
    int i = dstOff + (paramInt2 - dstY) * dstScan + (paramInt1 - dstX);
    int j;
    if (imageModel == paramColorModel)
    {
      for (j = paramInt4; j > 0; j--)
      {
        System.arraycopy(paramArrayOfInt, paramInt5, intPixels, i, paramInt3);
        paramInt5 += paramInt6;
        i += dstScan;
      }
    }
    else
    {
      if (imageModel != ColorModel.getRGBdefault()) {
        convertToRGB();
      }
      j = dstScan - paramInt3;
      int k = paramInt6 - paramInt3;
      for (int m = paramInt4; m > 0; m--)
      {
        for (int n = paramInt3; n > 0; n--) {
          intPixels[(i++)] = paramColorModel.getRGB(paramArrayOfInt[(paramInt5++)]);
        }
        paramInt5 += k;
        i += j;
      }
    }
    flags |= 0x8;
  }
  
  public synchronized void imageComplete(int paramInt)
  {
    grabbing = false;
    switch (paramInt)
    {
    case 1: 
    default: 
      flags |= 0xC0;
      break;
    case 4: 
      flags |= 0x80;
      break;
    case 3: 
      flags |= 0x20;
      break;
    case 2: 
      flags |= 0x10;
    }
    producer.removeConsumer(this);
    notifyAll();
  }
  
  public synchronized int status()
  {
    return flags;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\PixelGrabber.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */