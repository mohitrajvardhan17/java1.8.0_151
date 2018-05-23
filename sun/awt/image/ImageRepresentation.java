package sun.awt.image;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

public class ImageRepresentation
  extends ImageWatched
  implements ImageConsumer
{
  InputStreamImageSource src;
  ToolkitImage image;
  int tag;
  long pData;
  int width = -1;
  int height = -1;
  int hints;
  int availinfo;
  Rectangle newbits;
  BufferedImage bimage;
  WritableRaster biRaster;
  protected ColorModel cmodel;
  ColorModel srcModel = null;
  int[] srcLUT = null;
  int srcLUTtransIndex = -1;
  int numSrcLUT = 0;
  boolean forceCMhint;
  int sstride;
  boolean isDefaultBI = false;
  boolean isSameCM = false;
  static boolean s_useNative = true;
  private boolean consuming = false;
  private int numWaiters;
  
  private static native void initIDs();
  
  public ImageRepresentation(ToolkitImage paramToolkitImage, ColorModel paramColorModel, boolean paramBoolean)
  {
    image = paramToolkitImage;
    if ((image.getSource() instanceof InputStreamImageSource)) {
      src = ((InputStreamImageSource)image.getSource());
    }
    setColorModel(paramColorModel);
    forceCMhint = paramBoolean;
  }
  
  public synchronized void reconstruct(int paramInt)
  {
    if (src != null) {
      src.checkSecurity(null, false);
    }
    int i = paramInt & (availinfo ^ 0xFFFFFFFF);
    if (((availinfo & 0x40) == 0) && (i != 0))
    {
      numWaiters += 1;
      try
      {
        startProduction();
        for (i = paramInt & (availinfo ^ 0xFFFFFFFF); ((availinfo & 0x40) == 0) && (i != 0); i = paramInt & (availinfo ^ 0xFFFFFFFF)) {
          try
          {
            wait();
          }
          catch (InterruptedException localInterruptedException)
          {
            Thread.currentThread().interrupt();
            return;
          }
        }
      }
      finally
      {
        decrementWaiters();
      }
    }
  }
  
  public void setDimensions(int paramInt1, int paramInt2)
  {
    if (src != null) {
      src.checkSecurity(null, false);
    }
    image.setDimensions(paramInt1, paramInt2);
    newInfo(image, 3, 0, 0, paramInt1, paramInt2);
    if ((paramInt1 <= 0) || (paramInt2 <= 0))
    {
      imageComplete(1);
      return;
    }
    if ((width != paramInt1) || (height != paramInt2)) {
      bimage = null;
    }
    width = paramInt1;
    height = paramInt2;
    availinfo |= 0x3;
  }
  
  public int getWidth()
  {
    return width;
  }
  
  public int getHeight()
  {
    return height;
  }
  
  ColorModel getColorModel()
  {
    return cmodel;
  }
  
  BufferedImage getBufferedImage()
  {
    return bimage;
  }
  
  protected BufferedImage createImage(ColorModel paramColorModel, WritableRaster paramWritableRaster, boolean paramBoolean, Hashtable paramHashtable)
  {
    BufferedImage localBufferedImage = new BufferedImage(paramColorModel, paramWritableRaster, paramBoolean, null);
    localBufferedImage.setAccelerationPriority(image.getAccelerationPriority());
    return localBufferedImage;
  }
  
  public void setProperties(Hashtable<?, ?> paramHashtable)
  {
    if (src != null) {
      src.checkSecurity(null, false);
    }
    image.setProperties(paramHashtable);
    newInfo(image, 4, 0, 0, 0, 0);
  }
  
  public void setColorModel(ColorModel paramColorModel)
  {
    if (src != null) {
      src.checkSecurity(null, false);
    }
    srcModel = paramColorModel;
    Object localObject;
    if ((paramColorModel instanceof IndexColorModel))
    {
      if (paramColorModel.getTransparency() == 3)
      {
        cmodel = ColorModel.getRGBdefault();
        srcLUT = null;
      }
      else
      {
        localObject = (IndexColorModel)paramColorModel;
        numSrcLUT = ((IndexColorModel)localObject).getMapSize();
        srcLUT = new int[Math.max(numSrcLUT, 256)];
        ((IndexColorModel)localObject).getRGBs(srcLUT);
        srcLUTtransIndex = ((IndexColorModel)localObject).getTransparentPixel();
        cmodel = paramColorModel;
      }
    }
    else if (cmodel == null)
    {
      cmodel = paramColorModel;
      srcLUT = null;
    }
    else if ((paramColorModel instanceof DirectColorModel))
    {
      localObject = (DirectColorModel)paramColorModel;
      if ((((DirectColorModel)localObject).getRedMask() == 16711680) && (((DirectColorModel)localObject).getGreenMask() == 65280) && (((DirectColorModel)localObject).getBlueMask() == 255))
      {
        cmodel = paramColorModel;
        srcLUT = null;
      }
    }
    isSameCM = (cmodel == paramColorModel);
  }
  
  void createBufferedImage()
  {
    isDefaultBI = false;
    try
    {
      biRaster = cmodel.createCompatibleWritableRaster(width, height);
      bimage = createImage(cmodel, biRaster, cmodel.isAlphaPremultiplied(), null);
    }
    catch (Exception localException)
    {
      cmodel = ColorModel.getRGBdefault();
      biRaster = cmodel.createCompatibleWritableRaster(width, height);
      bimage = createImage(cmodel, biRaster, false, null);
    }
    int i = bimage.getType();
    if ((cmodel == ColorModel.getRGBdefault()) || (i == 1) || (i == 3))
    {
      isDefaultBI = true;
    }
    else if ((cmodel instanceof DirectColorModel))
    {
      DirectColorModel localDirectColorModel = (DirectColorModel)cmodel;
      if ((localDirectColorModel.getRedMask() == 16711680) && (localDirectColorModel.getGreenMask() == 65280) && (localDirectColorModel.getBlueMask() == 255)) {
        isDefaultBI = true;
      }
    }
  }
  
  private void convertToRGB()
  {
    int i = bimage.getWidth();
    int j = bimage.getHeight();
    int k = i * j;
    DataBufferInt localDataBufferInt = new DataBufferInt(k);
    int[] arrayOfInt = SunWritableRaster.stealData(localDataBufferInt, 0);
    int n;
    int i1;
    if (((cmodel instanceof IndexColorModel)) && ((biRaster instanceof ByteComponentRaster)) && (biRaster.getNumDataElements() == 1))
    {
      localObject = (ByteComponentRaster)biRaster;
      byte[] arrayOfByte = ((ByteComponentRaster)localObject).getDataStorage();
      n = ((ByteComponentRaster)localObject).getDataOffset(0);
      for (i1 = 0; i1 < k; i1++) {
        arrayOfInt[i1] = srcLUT[(arrayOfByte[(n + i1)] & 0xFF)];
      }
    }
    else
    {
      localObject = null;
      int m = 0;
      for (n = 0; n < j; n++) {
        for (i1 = 0; i1 < i; i1++)
        {
          localObject = biRaster.getDataElements(i1, n, localObject);
          arrayOfInt[(m++)] = cmodel.getRGB(localObject);
        }
      }
    }
    SunWritableRaster.markDirty(localDataBufferInt);
    isSameCM = false;
    cmodel = ColorModel.getRGBdefault();
    Object localObject = { 16711680, 65280, 255, -16777216 };
    biRaster = Raster.createPackedRaster(localDataBufferInt, i, j, i, (int[])localObject, null);
    bimage = createImage(cmodel, biRaster, cmodel.isAlphaPremultiplied(), null);
    srcLUT = null;
    isDefaultBI = true;
  }
  
  public void setHints(int paramInt)
  {
    if (src != null) {
      src.checkSecurity(null, false);
    }
    hints = paramInt;
  }
  
  private native boolean setICMpixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, byte[] paramArrayOfByte, int paramInt5, int paramInt6, IntegerComponentRaster paramIntegerComponentRaster);
  
  private native boolean setDiffICM(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, int paramInt5, int paramInt6, IndexColorModel paramIndexColorModel, byte[] paramArrayOfByte, int paramInt7, int paramInt8, ByteComponentRaster paramByteComponentRaster, int paramInt9);
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, byte[] paramArrayOfByte, int paramInt5, int paramInt6)
  {
    int i = paramInt5;
    Object localObject1 = null;
    if (src != null) {
      src.checkSecurity(null, false);
    }
    synchronized (this)
    {
      if (bimage == null)
      {
        if (cmodel == null) {
          cmodel = paramColorModel;
        }
        createBufferedImage();
      }
      if ((paramInt3 <= 0) || (paramInt4 <= 0)) {
        return;
      }
      int k = biRaster.getWidth();
      int m = biRaster.getHeight();
      int n = paramInt1 + paramInt3;
      int i1 = paramInt2 + paramInt4;
      if (paramInt1 < 0)
      {
        paramInt5 -= paramInt1;
        paramInt1 = 0;
      }
      else if (n < 0)
      {
        n = k;
      }
      if (paramInt2 < 0)
      {
        paramInt5 -= paramInt2 * paramInt6;
        paramInt2 = 0;
      }
      else if (i1 < 0)
      {
        i1 = m;
      }
      if (n > k) {
        n = k;
      }
      if (i1 > m) {
        i1 = m;
      }
      if ((paramInt1 >= n) || (paramInt2 >= i1)) {
        return;
      }
      paramInt3 = n - paramInt1;
      paramInt4 = i1 - paramInt2;
      if ((paramInt5 < 0) || (paramInt5 >= paramArrayOfByte.length)) {
        throw new ArrayIndexOutOfBoundsException("Data offset out of bounds.");
      }
      int i2 = paramArrayOfByte.length - paramInt5;
      if (i2 < paramInt3) {
        throw new ArrayIndexOutOfBoundsException("Data array is too short.");
      }
      int i3;
      if (paramInt6 < 0) {
        i3 = paramInt5 / -paramInt6 + 1;
      } else if (paramInt6 > 0) {
        i3 = (i2 - paramInt3) / paramInt6 + 1;
      } else {
        i3 = paramInt4;
      }
      if (paramInt4 > i3) {
        throw new ArrayIndexOutOfBoundsException("Data array is too short.");
      }
      Object localObject2;
      Object localObject3;
      boolean bool;
      int i9;
      if ((isSameCM) && (cmodel != paramColorModel) && (srcLUT != null) && ((paramColorModel instanceof IndexColorModel)) && ((biRaster instanceof ByteComponentRaster)))
      {
        localObject2 = (IndexColorModel)paramColorModel;
        localObject3 = (ByteComponentRaster)biRaster;
        int i6 = numSrcLUT;
        if (!setDiffICM(paramInt1, paramInt2, paramInt3, paramInt4, srcLUT, srcLUTtransIndex, numSrcLUT, (IndexColorModel)localObject2, paramArrayOfByte, paramInt5, paramInt6, (ByteComponentRaster)localObject3, ((ByteComponentRaster)localObject3).getDataOffset(0)))
        {
          convertToRGB();
        }
        else
        {
          ((ByteComponentRaster)localObject3).markDirty();
          if (i6 != numSrcLUT)
          {
            bool = ((IndexColorModel)localObject2).hasAlpha();
            if (srcLUTtransIndex != -1) {
              bool = true;
            }
            i9 = ((IndexColorModel)localObject2).getPixelSize();
            localObject2 = new IndexColorModel(i9, numSrcLUT, srcLUT, 0, bool, srcLUTtransIndex, i9 > 8 ? 1 : 0);
            cmodel = ((ColorModel)localObject2);
            bimage = createImage((ColorModel)localObject2, (WritableRaster)localObject3, false, null);
          }
          return;
        }
      }
      int j;
      if (isDefaultBI)
      {
        localObject3 = (IntegerComponentRaster)biRaster;
        int[] arrayOfInt;
        if ((srcLUT != null) && ((paramColorModel instanceof IndexColorModel)))
        {
          if (paramColorModel != srcModel)
          {
            ((IndexColorModel)paramColorModel).getRGBs(srcLUT);
            srcModel = paramColorModel;
          }
          if (s_useNative)
          {
            if (setICMpixels(paramInt1, paramInt2, paramInt3, paramInt4, srcLUT, paramArrayOfByte, paramInt5, paramInt6, (IntegerComponentRaster)localObject3)) {
              ((IntegerComponentRaster)localObject3).markDirty();
            } else {
              abort();
            }
          }
          else
          {
            arrayOfInt = new int[paramInt3 * paramInt4];
            bool = false;
            i9 = 0;
            while (i9 < paramInt4)
            {
              j = i;
              for (int i10 = 0; i10 < paramInt3; i10++) {
                arrayOfInt[(bool++)] = srcLUT[(paramArrayOfByte[(j++)] & 0xFF)];
              }
              i9++;
              i += paramInt6;
            }
            ((IntegerComponentRaster)localObject3).setDataElements(paramInt1, paramInt2, paramInt3, paramInt4, arrayOfInt);
          }
        }
        else
        {
          arrayOfInt = new int[paramInt3];
          int i8 = paramInt2;
          while (i8 < paramInt2 + paramInt4)
          {
            j = i;
            for (i9 = 0; i9 < paramInt3; i9++) {
              arrayOfInt[i9] = paramColorModel.getRGB(paramArrayOfByte[(j++)] & 0xFF);
            }
            ((IntegerComponentRaster)localObject3).setDataElements(paramInt1, i8, paramInt3, 1, arrayOfInt);
            i8++;
            i += paramInt6;
          }
          availinfo |= 0x8;
        }
      }
      else if ((cmodel == paramColorModel) && ((biRaster instanceof ByteComponentRaster)) && (biRaster.getNumDataElements() == 1))
      {
        localObject2 = (ByteComponentRaster)biRaster;
        if ((paramInt5 == 0) && (paramInt6 == paramInt3))
        {
          ((ByteComponentRaster)localObject2).putByteData(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfByte);
        }
        else
        {
          localObject3 = new byte[paramInt3];
          j = paramInt5;
          for (int i7 = paramInt2; i7 < paramInt2 + paramInt4; i7++)
          {
            System.arraycopy(paramArrayOfByte, j, localObject3, 0, paramInt3);
            ((ByteComponentRaster)localObject2).putByteData(paramInt1, i7, paramInt3, 1, (byte[])localObject3);
            j += paramInt6;
          }
        }
      }
      else
      {
        int i4 = paramInt2;
        while (i4 < paramInt2 + paramInt4)
        {
          j = i;
          for (int i5 = paramInt1; i5 < paramInt1 + paramInt3; i5++) {
            bimage.setRGB(i5, i4, paramColorModel.getRGB(paramArrayOfByte[(j++)] & 0xFF));
          }
          i4++;
          i += paramInt6;
        }
        availinfo |= 0x8;
      }
    }
    if ((availinfo & 0x10) == 0) {
      newInfo(image, 8, paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, int[] paramArrayOfInt, int paramInt5, int paramInt6)
  {
    int i = paramInt5;
    if (src != null) {
      src.checkSecurity(null, false);
    }
    synchronized (this)
    {
      if (bimage == null)
      {
        if (cmodel == null) {
          cmodel = paramColorModel;
        }
        createBufferedImage();
      }
      int[] arrayOfInt1 = new int[paramInt3];
      if ((cmodel instanceof IndexColorModel)) {
        convertToRGB();
      }
      Object localObject1;
      int k;
      if ((paramColorModel == cmodel) && ((biRaster instanceof IntegerComponentRaster)))
      {
        localObject1 = (IntegerComponentRaster)biRaster;
        if ((paramInt5 == 0) && (paramInt6 == paramInt3))
        {
          ((IntegerComponentRaster)localObject1).setDataElements(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt);
        }
        else
        {
          k = paramInt2;
          while (k < paramInt2 + paramInt4)
          {
            System.arraycopy(paramArrayOfInt, i, arrayOfInt1, 0, paramInt3);
            ((IntegerComponentRaster)localObject1).setDataElements(paramInt1, k, paramInt3, 1, arrayOfInt1);
            k++;
            i += paramInt6;
          }
        }
      }
      else
      {
        if ((paramColorModel.getTransparency() != 1) && (cmodel.getTransparency() == 1)) {
          convertToRGB();
        }
        int j;
        if (isDefaultBI)
        {
          localObject1 = (IntegerComponentRaster)biRaster;
          int[] arrayOfInt2 = ((IntegerComponentRaster)localObject1).getDataStorage();
          int i1;
          if (cmodel.equals(paramColorModel))
          {
            i1 = ((IntegerComponentRaster)localObject1).getScanlineStride();
            int i2 = paramInt2 * i1 + paramInt1;
            k = 0;
            while (k < paramInt4)
            {
              System.arraycopy(paramArrayOfInt, i, arrayOfInt2, i2, paramInt3);
              i2 += i1;
              k++;
              i += paramInt6;
            }
            ((IntegerComponentRaster)localObject1).markDirty();
          }
          else
          {
            k = paramInt2;
            while (k < paramInt2 + paramInt4)
            {
              j = i;
              for (i1 = 0; i1 < paramInt3; i1++) {
                arrayOfInt1[i1] = paramColorModel.getRGB(paramArrayOfInt[(j++)]);
              }
              ((IntegerComponentRaster)localObject1).setDataElements(paramInt1, k, paramInt3, 1, arrayOfInt1);
              k++;
              i += paramInt6;
            }
          }
          availinfo |= 0x8;
        }
        else
        {
          localObject1 = null;
          k = paramInt2;
          while (k < paramInt2 + paramInt4)
          {
            j = i;
            for (int n = paramInt1; n < paramInt1 + paramInt3; n++)
            {
              int m = paramColorModel.getRGB(paramArrayOfInt[(j++)]);
              localObject1 = cmodel.getDataElements(m, localObject1);
              biRaster.setDataElements(n, k, localObject1);
            }
            k++;
            i += paramInt6;
          }
          availinfo |= 0x8;
        }
      }
    }
    if ((availinfo & 0x10) == 0) {
      newInfo(image, 8, paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  public BufferedImage getOpaqueRGBImage()
  {
    if (bimage.getType() == 2)
    {
      int i = bimage.getWidth();
      int j = bimage.getHeight();
      int k = i * j;
      DataBufferInt localDataBufferInt = (DataBufferInt)biRaster.getDataBuffer();
      int[] arrayOfInt1 = SunWritableRaster.stealData(localDataBufferInt, 0);
      for (int m = 0; m < k; m++) {
        if (arrayOfInt1[m] >>> 24 != 255) {
          return bimage;
        }
      }
      DirectColorModel localDirectColorModel = new DirectColorModel(24, 16711680, 65280, 255);
      int[] arrayOfInt2 = { 16711680, 65280, 255 };
      WritableRaster localWritableRaster = Raster.createPackedRaster(localDataBufferInt, i, j, i, arrayOfInt2, null);
      try
      {
        BufferedImage localBufferedImage = createImage(localDirectColorModel, localWritableRaster, false, null);
        return localBufferedImage;
      }
      catch (Exception localException)
      {
        return bimage;
      }
    }
    return bimage;
  }
  
  public void imageComplete(int paramInt)
  {
    if (src != null) {
      src.checkSecurity(null, false);
    }
    int i;
    int j;
    switch (paramInt)
    {
    case 4: 
    default: 
      i = 1;
      j = 128;
      break;
    case 1: 
      image.addInfo(64);
      i = 1;
      j = 64;
      dispose();
      break;
    case 3: 
      i = 1;
      j = 32;
      break;
    case 2: 
      i = 0;
      j = 16;
    }
    synchronized (this)
    {
      if (i != 0)
      {
        image.getSource().removeConsumer(this);
        consuming = false;
        newbits = null;
        if (bimage != null) {
          bimage = getOpaqueRGBImage();
        }
      }
      availinfo |= j;
      notifyAll();
    }
    newInfo(image, j, 0, 0, width, height);
    image.infoDone(paramInt);
  }
  
  void startProduction()
  {
    if (!consuming)
    {
      consuming = true;
      image.getSource().startProduction(this);
    }
  }
  
  private synchronized void checkConsumption()
  {
    if ((isWatcherListEmpty()) && (numWaiters == 0) && ((availinfo & 0x20) == 0)) {
      dispose();
    }
  }
  
  public synchronized void notifyWatcherListEmpty()
  {
    checkConsumption();
  }
  
  private synchronized void decrementWaiters()
  {
    numWaiters -= 1;
    checkConsumption();
  }
  
  public boolean prepare(ImageObserver paramImageObserver)
  {
    if (src != null) {
      src.checkSecurity(null, false);
    }
    if ((availinfo & 0x40) != 0)
    {
      if (paramImageObserver != null) {
        paramImageObserver.imageUpdate(image, 192, -1, -1, -1, -1);
      }
      return false;
    }
    boolean bool = (availinfo & 0x20) != 0;
    if (!bool)
    {
      addWatcher(paramImageObserver);
      startProduction();
      bool = (availinfo & 0x20) != 0;
    }
    return bool;
  }
  
  public int check(ImageObserver paramImageObserver)
  {
    if (src != null) {
      src.checkSecurity(null, false);
    }
    if ((availinfo & 0x60) == 0) {
      addWatcher(paramImageObserver);
    }
    return availinfo;
  }
  
  public boolean drawToBufImage(Graphics paramGraphics, ToolkitImage paramToolkitImage, int paramInt1, int paramInt2, Color paramColor, ImageObserver paramImageObserver)
  {
    if (src != null) {
      src.checkSecurity(null, false);
    }
    if ((availinfo & 0x40) != 0)
    {
      if (paramImageObserver != null) {
        paramImageObserver.imageUpdate(image, 192, -1, -1, -1, -1);
      }
      return false;
    }
    boolean bool = (availinfo & 0x20) != 0;
    int i = (availinfo & 0x80) != 0 ? 1 : 0;
    if ((!bool) && (i == 0))
    {
      addWatcher(paramImageObserver);
      startProduction();
      bool = (availinfo & 0x20) != 0;
    }
    if ((bool) || (0 != (availinfo & 0x10))) {
      paramGraphics.drawImage(bimage, paramInt1, paramInt2, paramColor, null);
    }
    return bool;
  }
  
  public boolean drawToBufImage(Graphics paramGraphics, ToolkitImage paramToolkitImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor, ImageObserver paramImageObserver)
  {
    if (src != null) {
      src.checkSecurity(null, false);
    }
    if ((availinfo & 0x40) != 0)
    {
      if (paramImageObserver != null) {
        paramImageObserver.imageUpdate(image, 192, -1, -1, -1, -1);
      }
      return false;
    }
    boolean bool = (availinfo & 0x20) != 0;
    int i = (availinfo & 0x80) != 0 ? 1 : 0;
    if ((!bool) && (i == 0))
    {
      addWatcher(paramImageObserver);
      startProduction();
      bool = (availinfo & 0x20) != 0;
    }
    if ((bool) || (0 != (availinfo & 0x10))) {
      paramGraphics.drawImage(bimage, paramInt1, paramInt2, paramInt3, paramInt4, paramColor, null);
    }
    return bool;
  }
  
  public boolean drawToBufImage(Graphics paramGraphics, ToolkitImage paramToolkitImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, Color paramColor, ImageObserver paramImageObserver)
  {
    if (src != null) {
      src.checkSecurity(null, false);
    }
    if ((availinfo & 0x40) != 0)
    {
      if (paramImageObserver != null) {
        paramImageObserver.imageUpdate(image, 192, -1, -1, -1, -1);
      }
      return false;
    }
    boolean bool = (availinfo & 0x20) != 0;
    int i = (availinfo & 0x80) != 0 ? 1 : 0;
    if ((!bool) && (i == 0))
    {
      addWatcher(paramImageObserver);
      startProduction();
      bool = (availinfo & 0x20) != 0;
    }
    if ((bool) || (0 != (availinfo & 0x10))) {
      paramGraphics.drawImage(bimage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramColor, null);
    }
    return bool;
  }
  
  public boolean drawToBufImage(Graphics paramGraphics, ToolkitImage paramToolkitImage, AffineTransform paramAffineTransform, ImageObserver paramImageObserver)
  {
    Graphics2D localGraphics2D = (Graphics2D)paramGraphics;
    if (src != null) {
      src.checkSecurity(null, false);
    }
    if ((availinfo & 0x40) != 0)
    {
      if (paramImageObserver != null) {
        paramImageObserver.imageUpdate(image, 192, -1, -1, -1, -1);
      }
      return false;
    }
    boolean bool = (availinfo & 0x20) != 0;
    int i = (availinfo & 0x80) != 0 ? 1 : 0;
    if ((!bool) && (i == 0))
    {
      addWatcher(paramImageObserver);
      startProduction();
      bool = (availinfo & 0x20) != 0;
    }
    if ((bool) || (0 != (availinfo & 0x10))) {
      localGraphics2D.drawImage(bimage, paramAffineTransform, null);
    }
    return bool;
  }
  
  synchronized void abort()
  {
    image.getSource().removeConsumer(this);
    consuming = false;
    newbits = null;
    bimage = null;
    biRaster = null;
    cmodel = null;
    srcLUT = null;
    isDefaultBI = false;
    isSameCM = false;
    newInfo(image, 128, -1, -1, -1, -1);
    availinfo &= 0xFFFFFF87;
  }
  
  synchronized void dispose()
  {
    image.getSource().removeConsumer(this);
    consuming = false;
    newbits = null;
    availinfo &= 0xFFFFFFC7;
  }
  
  public void setAccelerationPriority(float paramFloat)
  {
    if (bimage != null) {
      bimage.setAccelerationPriority(paramFloat);
    }
  }
  
  static
  {
    NativeLibLoader.loadLibraries();
    initIDs();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\image\ImageRepresentation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */