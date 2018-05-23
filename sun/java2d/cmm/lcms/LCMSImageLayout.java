package sun.java2d.cmm.lcms;

import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import sun.awt.image.ByteComponentRaster;
import sun.awt.image.IntegerComponentRaster;
import sun.awt.image.ShortComponentRaster;

class LCMSImageLayout
{
  public static final int SWAPFIRST = 16384;
  public static final int DOSWAP = 1024;
  public static final int PT_RGB_8 = CHANNELS_SH(3) | BYTES_SH(1);
  public static final int PT_GRAY_8 = CHANNELS_SH(1) | BYTES_SH(1);
  public static final int PT_GRAY_16 = CHANNELS_SH(1) | BYTES_SH(2);
  public static final int PT_RGBA_8 = EXTRA_SH(1) | CHANNELS_SH(3) | BYTES_SH(1);
  public static final int PT_ARGB_8 = EXTRA_SH(1) | CHANNELS_SH(3) | BYTES_SH(1) | 0x4000;
  public static final int PT_BGR_8 = 0x400 | CHANNELS_SH(3) | BYTES_SH(1);
  public static final int PT_ABGR_8 = 0x400 | EXTRA_SH(1) | CHANNELS_SH(3) | BYTES_SH(1);
  public static final int PT_BGRA_8 = EXTRA_SH(1) | CHANNELS_SH(3) | BYTES_SH(1) | 0x400 | 0x4000;
  public static final int DT_BYTE = 0;
  public static final int DT_SHORT = 1;
  public static final int DT_INT = 2;
  public static final int DT_DOUBLE = 3;
  boolean isIntPacked = false;
  int pixelType;
  int dataType;
  int width;
  int height;
  int nextRowOffset;
  private int nextPixelOffset;
  int offset;
  private boolean imageAtOnce = false;
  Object dataArray;
  private int dataArrayLength;
  
  public static int BYTES_SH(int paramInt)
  {
    return paramInt;
  }
  
  public static int EXTRA_SH(int paramInt)
  {
    return paramInt << 7;
  }
  
  public static int CHANNELS_SH(int paramInt)
  {
    return paramInt << 3;
  }
  
  private LCMSImageLayout(int paramInt1, int paramInt2, int paramInt3)
    throws LCMSImageLayout.ImageLayoutException
  {
    pixelType = paramInt2;
    width = paramInt1;
    height = 1;
    nextPixelOffset = paramInt3;
    nextRowOffset = safeMult(paramInt3, paramInt1);
    offset = 0;
  }
  
  private LCMSImageLayout(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws LCMSImageLayout.ImageLayoutException
  {
    pixelType = paramInt3;
    width = paramInt1;
    height = paramInt2;
    nextPixelOffset = paramInt4;
    nextRowOffset = safeMult(paramInt4, paramInt1);
    offset = 0;
  }
  
  public LCMSImageLayout(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
    throws LCMSImageLayout.ImageLayoutException
  {
    this(paramInt1, paramInt2, paramInt3);
    dataType = 0;
    dataArray = paramArrayOfByte;
    dataArrayLength = paramArrayOfByte.length;
    verify();
  }
  
  public LCMSImageLayout(short[] paramArrayOfShort, int paramInt1, int paramInt2, int paramInt3)
    throws LCMSImageLayout.ImageLayoutException
  {
    this(paramInt1, paramInt2, paramInt3);
    dataType = 1;
    dataArray = paramArrayOfShort;
    dataArrayLength = (2 * paramArrayOfShort.length);
    verify();
  }
  
  public LCMSImageLayout(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3)
    throws LCMSImageLayout.ImageLayoutException
  {
    this(paramInt1, paramInt2, paramInt3);
    dataType = 2;
    dataArray = paramArrayOfInt;
    dataArrayLength = (4 * paramArrayOfInt.length);
    verify();
  }
  
  public LCMSImageLayout(double[] paramArrayOfDouble, int paramInt1, int paramInt2, int paramInt3)
    throws LCMSImageLayout.ImageLayoutException
  {
    this(paramInt1, paramInt2, paramInt3);
    dataType = 3;
    dataArray = paramArrayOfDouble;
    dataArrayLength = (8 * paramArrayOfDouble.length);
    verify();
  }
  
  private LCMSImageLayout() {}
  
  public static LCMSImageLayout createImageLayout(BufferedImage paramBufferedImage)
    throws LCMSImageLayout.ImageLayoutException
  {
    LCMSImageLayout localLCMSImageLayout = new LCMSImageLayout();
    Object localObject;
    switch (paramBufferedImage.getType())
    {
    case 1: 
      pixelType = PT_ARGB_8;
      isIntPacked = true;
      break;
    case 2: 
      pixelType = PT_ARGB_8;
      isIntPacked = true;
      break;
    case 4: 
      pixelType = PT_ABGR_8;
      isIntPacked = true;
      break;
    case 5: 
      pixelType = PT_BGR_8;
      break;
    case 6: 
      pixelType = PT_ABGR_8;
      break;
    case 10: 
      pixelType = PT_GRAY_8;
      break;
    case 11: 
      pixelType = PT_GRAY_16;
      break;
    case 3: 
    case 7: 
    case 8: 
    case 9: 
    default: 
      localObject = paramBufferedImage.getColorModel();
      if ((localObject instanceof ComponentColorModel))
      {
        ComponentColorModel localComponentColorModel = (ComponentColorModel)localObject;
        int[] arrayOfInt1 = localComponentColorModel.getComponentSize();
        for (int m : arrayOfInt1) {
          if (m != 8) {
            return null;
          }
        }
        return createImageLayout(paramBufferedImage.getRaster());
      }
      return null;
    }
    width = paramBufferedImage.getWidth();
    height = paramBufferedImage.getHeight();
    switch (paramBufferedImage.getType())
    {
    case 1: 
    case 2: 
    case 4: 
      localObject = (IntegerComponentRaster)paramBufferedImage.getRaster();
      nextRowOffset = safeMult(4, ((IntegerComponentRaster)localObject).getScanlineStride());
      nextPixelOffset = safeMult(4, ((IntegerComponentRaster)localObject).getPixelStride());
      offset = safeMult(4, ((IntegerComponentRaster)localObject).getDataOffset(0));
      dataArray = ((IntegerComponentRaster)localObject).getDataStorage();
      dataArrayLength = (4 * ((IntegerComponentRaster)localObject).getDataStorage().length);
      dataType = 2;
      if (nextRowOffset == width * 4 * ((IntegerComponentRaster)localObject).getPixelStride()) {
        imageAtOnce = true;
      }
      break;
    case 5: 
    case 6: 
      localObject = (ByteComponentRaster)paramBufferedImage.getRaster();
      nextRowOffset = ((ByteComponentRaster)localObject).getScanlineStride();
      nextPixelOffset = ((ByteComponentRaster)localObject).getPixelStride();
      int i = paramBufferedImage.getSampleModel().getNumBands() - 1;
      offset = ((ByteComponentRaster)localObject).getDataOffset(i);
      dataArray = ((ByteComponentRaster)localObject).getDataStorage();
      dataArrayLength = ((ByteComponentRaster)localObject).getDataStorage().length;
      dataType = 0;
      if (nextRowOffset == width * ((ByteComponentRaster)localObject).getPixelStride()) {
        imageAtOnce = true;
      }
      break;
    case 10: 
      localObject = (ByteComponentRaster)paramBufferedImage.getRaster();
      nextRowOffset = ((ByteComponentRaster)localObject).getScanlineStride();
      nextPixelOffset = ((ByteComponentRaster)localObject).getPixelStride();
      dataArrayLength = ((ByteComponentRaster)localObject).getDataStorage().length;
      offset = ((ByteComponentRaster)localObject).getDataOffset(0);
      dataArray = ((ByteComponentRaster)localObject).getDataStorage();
      dataType = 0;
      if (nextRowOffset == width * ((ByteComponentRaster)localObject).getPixelStride()) {
        imageAtOnce = true;
      }
      break;
    case 11: 
      localObject = (ShortComponentRaster)paramBufferedImage.getRaster();
      nextRowOffset = safeMult(2, ((ShortComponentRaster)localObject).getScanlineStride());
      nextPixelOffset = safeMult(2, ((ShortComponentRaster)localObject).getPixelStride());
      offset = safeMult(2, ((ShortComponentRaster)localObject).getDataOffset(0));
      dataArray = ((ShortComponentRaster)localObject).getDataStorage();
      dataArrayLength = (2 * ((ShortComponentRaster)localObject).getDataStorage().length);
      dataType = 1;
      if (nextRowOffset == width * 2 * ((ShortComponentRaster)localObject).getPixelStride()) {
        imageAtOnce = true;
      }
      break;
    case 3: 
    case 7: 
    case 8: 
    case 9: 
    default: 
      return null;
    }
    localLCMSImageLayout.verify();
    return localLCMSImageLayout;
  }
  
  private void verify()
    throws LCMSImageLayout.ImageLayoutException
  {
    if ((offset < 0) || (offset >= dataArrayLength)) {
      throw new ImageLayoutException("Invalid image layout");
    }
    if (nextPixelOffset != getBytesPerPixel(pixelType)) {
      throw new ImageLayoutException("Invalid image layout");
    }
    int i = safeMult(nextRowOffset, height - 1);
    int j = safeMult(nextPixelOffset, width - 1);
    j = safeAdd(j, i);
    int k = safeAdd(offset, j);
    if ((k < 0) || (k >= dataArrayLength)) {
      throw new ImageLayoutException("Invalid image layout");
    }
  }
  
  static int safeAdd(int paramInt1, int paramInt2)
    throws LCMSImageLayout.ImageLayoutException
  {
    long l = paramInt1;
    l += paramInt2;
    if ((l < -2147483648L) || (l > 2147483647L)) {
      throw new ImageLayoutException("Invalid image layout");
    }
    return (int)l;
  }
  
  static int safeMult(int paramInt1, int paramInt2)
    throws LCMSImageLayout.ImageLayoutException
  {
    long l = paramInt1;
    l *= paramInt2;
    if ((l < -2147483648L) || (l > 2147483647L)) {
      throw new ImageLayoutException("Invalid image layout");
    }
    return (int)l;
  }
  
  public static LCMSImageLayout createImageLayout(Raster paramRaster)
  {
    LCMSImageLayout localLCMSImageLayout = new LCMSImageLayout();
    if (((paramRaster instanceof ByteComponentRaster)) && ((paramRaster.getSampleModel() instanceof ComponentSampleModel)))
    {
      ByteComponentRaster localByteComponentRaster = (ByteComponentRaster)paramRaster;
      ComponentSampleModel localComponentSampleModel = (ComponentSampleModel)paramRaster.getSampleModel();
      pixelType = (CHANNELS_SH(localByteComponentRaster.getNumBands()) | BYTES_SH(1));
      int[] arrayOfInt = localComponentSampleModel.getBandOffsets();
      BandOrder localBandOrder = BandOrder.getBandOrder(arrayOfInt);
      int i = 0;
      switch (localBandOrder)
      {
      case INVERTED: 
        pixelType |= 0x400;
        i = localComponentSampleModel.getNumBands() - 1;
        break;
      case DIRECT: 
        break;
      default: 
        return null;
      }
      nextRowOffset = localByteComponentRaster.getScanlineStride();
      nextPixelOffset = localByteComponentRaster.getPixelStride();
      offset = localByteComponentRaster.getDataOffset(i);
      dataArray = localByteComponentRaster.getDataStorage();
      dataType = 0;
      width = localByteComponentRaster.getWidth();
      height = localByteComponentRaster.getHeight();
      if (nextRowOffset == width * localByteComponentRaster.getPixelStride()) {
        imageAtOnce = true;
      }
      return localLCMSImageLayout;
    }
    return null;
  }
  
  private static int getBytesPerPixel(int paramInt)
  {
    int i = 0x7 & paramInt;
    int j = 0xF & paramInt >> 3;
    int k = 0x7 & paramInt >> 7;
    return i * (j + k);
  }
  
  private static enum BandOrder
  {
    DIRECT,  INVERTED,  ARBITRARY,  UNKNOWN;
    
    private BandOrder() {}
    
    public static BandOrder getBandOrder(int[] paramArrayOfInt)
    {
      BandOrder localBandOrder = UNKNOWN;
      int i = paramArrayOfInt.length;
      for (int j = 0; (localBandOrder != ARBITRARY) && (j < paramArrayOfInt.length); j++) {
        switch (LCMSImageLayout.1.$SwitchMap$sun$java2d$cmm$lcms$LCMSImageLayout$BandOrder[localBandOrder.ordinal()])
        {
        case 1: 
          if (paramArrayOfInt[j] == j) {
            localBandOrder = DIRECT;
          } else if (paramArrayOfInt[j] == i - 1 - j) {
            localBandOrder = INVERTED;
          } else {
            localBandOrder = ARBITRARY;
          }
          break;
        case 2: 
          if (paramArrayOfInt[j] != j) {
            localBandOrder = ARBITRARY;
          }
          break;
        case 3: 
          if (paramArrayOfInt[j] != i - 1 - j) {
            localBandOrder = ARBITRARY;
          }
          break;
        }
      }
      return localBandOrder;
    }
  }
  
  public static class ImageLayoutException
    extends Exception
  {
    public ImageLayoutException(String paramString)
    {
      super();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\cmm\lcms\LCMSImageLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */