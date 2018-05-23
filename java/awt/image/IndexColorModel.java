package java.awt.image;

import java.awt.color.ColorSpace;
import java.math.BigInteger;
import java.util.Arrays;
import sun.awt.image.BufImgSurfaceData.ICMColorData;

public class IndexColorModel
  extends ColorModel
{
  private int[] rgb;
  private int map_size;
  private int pixel_mask;
  private int transparent_index = -1;
  private boolean allgrayopaque;
  private BigInteger validBits;
  private BufImgSurfaceData.ICMColorData colorData = null;
  private static int[] opaqueBits = { 8, 8, 8 };
  private static int[] alphaBits = { 8, 8, 8, 8 };
  private static final int CACHESIZE = 40;
  private int[] lookupcache = new int[40];
  
  private static native void initIDs();
  
  public IndexColorModel(int paramInt1, int paramInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3)
  {
    super(paramInt1, opaqueBits, ColorSpace.getInstance(1000), false, false, 1, ColorModel.getDefaultTransferType(paramInt1));
    if ((paramInt1 < 1) || (paramInt1 > 16)) {
      throw new IllegalArgumentException("Number of bits must be between 1 and 16.");
    }
    setRGBs(paramInt2, paramArrayOfByte1, paramArrayOfByte2, paramArrayOfByte3, null);
    calculatePixelMask();
  }
  
  public IndexColorModel(int paramInt1, int paramInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt3)
  {
    super(paramInt1, opaqueBits, ColorSpace.getInstance(1000), false, false, 1, ColorModel.getDefaultTransferType(paramInt1));
    if ((paramInt1 < 1) || (paramInt1 > 16)) {
      throw new IllegalArgumentException("Number of bits must be between 1 and 16.");
    }
    setRGBs(paramInt2, paramArrayOfByte1, paramArrayOfByte2, paramArrayOfByte3, null);
    setTransparentPixel(paramInt3);
    calculatePixelMask();
  }
  
  public IndexColorModel(int paramInt1, int paramInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, byte[] paramArrayOfByte4)
  {
    super(paramInt1, alphaBits, ColorSpace.getInstance(1000), true, false, 3, ColorModel.getDefaultTransferType(paramInt1));
    if ((paramInt1 < 1) || (paramInt1 > 16)) {
      throw new IllegalArgumentException("Number of bits must be between 1 and 16.");
    }
    setRGBs(paramInt2, paramArrayOfByte1, paramArrayOfByte2, paramArrayOfByte3, paramArrayOfByte4);
    calculatePixelMask();
  }
  
  public IndexColorModel(int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3, boolean paramBoolean)
  {
    this(paramInt1, paramInt2, paramArrayOfByte, paramInt3, paramBoolean, -1);
    if ((paramInt1 < 1) || (paramInt1 > 16)) {
      throw new IllegalArgumentException("Number of bits must be between 1 and 16.");
    }
  }
  
  public IndexColorModel(int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3, boolean paramBoolean, int paramInt4)
  {
    super(paramInt1, opaqueBits, ColorSpace.getInstance(1000), false, false, 1, ColorModel.getDefaultTransferType(paramInt1));
    if ((paramInt1 < 1) || (paramInt1 > 16)) {
      throw new IllegalArgumentException("Number of bits must be between 1 and 16.");
    }
    if (paramInt2 < 1) {
      throw new IllegalArgumentException("Map size (" + paramInt2 + ") must be >= 1");
    }
    map_size = paramInt2;
    rgb = new int[calcRealMapSize(paramInt1, paramInt2)];
    int i = paramInt3;
    int j = 255;
    boolean bool = true;
    int k = 1;
    for (int m = 0; m < paramInt2; m++)
    {
      int n = paramArrayOfByte[(i++)] & 0xFF;
      int i1 = paramArrayOfByte[(i++)] & 0xFF;
      int i2 = paramArrayOfByte[(i++)] & 0xFF;
      bool = (bool) && (n == i1) && (i1 == i2);
      if (paramBoolean)
      {
        j = paramArrayOfByte[(i++)] & 0xFF;
        if (j != 255)
        {
          if (j == 0)
          {
            if (k == 1) {
              k = 2;
            }
            if (transparent_index < 0) {
              transparent_index = m;
            }
          }
          else
          {
            k = 3;
          }
          bool = false;
        }
      }
      rgb[m] = (j << 24 | n << 16 | i1 << 8 | i2);
    }
    allgrayopaque = bool;
    setTransparency(k);
    setTransparentPixel(paramInt4);
    calculatePixelMask();
  }
  
  public IndexColorModel(int paramInt1, int paramInt2, int[] paramArrayOfInt, int paramInt3, boolean paramBoolean, int paramInt4, int paramInt5)
  {
    super(paramInt1, opaqueBits, ColorSpace.getInstance(1000), false, false, 1, paramInt5);
    if ((paramInt1 < 1) || (paramInt1 > 16)) {
      throw new IllegalArgumentException("Number of bits must be between 1 and 16.");
    }
    if (paramInt2 < 1) {
      throw new IllegalArgumentException("Map size (" + paramInt2 + ") must be >= 1");
    }
    if ((paramInt5 != 0) && (paramInt5 != 1)) {
      throw new IllegalArgumentException("transferType must be eitherDataBuffer.TYPE_BYTE or DataBuffer.TYPE_USHORT");
    }
    setRGBs(paramInt2, paramArrayOfInt, paramInt3, paramBoolean);
    setTransparentPixel(paramInt4);
    calculatePixelMask();
  }
  
  public IndexColorModel(int paramInt1, int paramInt2, int[] paramArrayOfInt, int paramInt3, int paramInt4, BigInteger paramBigInteger)
  {
    super(paramInt1, alphaBits, ColorSpace.getInstance(1000), true, false, 3, paramInt4);
    if ((paramInt1 < 1) || (paramInt1 > 16)) {
      throw new IllegalArgumentException("Number of bits must be between 1 and 16.");
    }
    if (paramInt2 < 1) {
      throw new IllegalArgumentException("Map size (" + paramInt2 + ") must be >= 1");
    }
    if ((paramInt4 != 0) && (paramInt4 != 1)) {
      throw new IllegalArgumentException("transferType must be eitherDataBuffer.TYPE_BYTE or DataBuffer.TYPE_USHORT");
    }
    if (paramBigInteger != null) {
      for (int i = 0; i < paramInt2; i++) {
        if (!paramBigInteger.testBit(i))
        {
          validBits = paramBigInteger;
          break;
        }
      }
    }
    setRGBs(paramInt2, paramArrayOfInt, paramInt3, true);
    calculatePixelMask();
  }
  
  private void setRGBs(int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, byte[] paramArrayOfByte4)
  {
    if (paramInt < 1) {
      throw new IllegalArgumentException("Map size (" + paramInt + ") must be >= 1");
    }
    map_size = paramInt;
    rgb = new int[calcRealMapSize(pixel_bits, paramInt)];
    int i = 255;
    int j = 1;
    boolean bool = true;
    for (int k = 0; k < paramInt; k++)
    {
      int m = paramArrayOfByte1[k] & 0xFF;
      int n = paramArrayOfByte2[k] & 0xFF;
      int i1 = paramArrayOfByte3[k] & 0xFF;
      bool = (bool) && (m == n) && (n == i1);
      if (paramArrayOfByte4 != null)
      {
        i = paramArrayOfByte4[k] & 0xFF;
        if (i != 255)
        {
          if (i == 0)
          {
            if (j == 1) {
              j = 2;
            }
            if (transparent_index < 0) {
              transparent_index = k;
            }
          }
          else
          {
            j = 3;
          }
          bool = false;
        }
      }
      rgb[k] = (i << 24 | m << 16 | n << 8 | i1);
    }
    allgrayopaque = bool;
    setTransparency(j);
  }
  
  private void setRGBs(int paramInt1, int[] paramArrayOfInt, int paramInt2, boolean paramBoolean)
  {
    map_size = paramInt1;
    rgb = new int[calcRealMapSize(pixel_bits, paramInt1)];
    int i = paramInt2;
    int j = 1;
    boolean bool = true;
    BigInteger localBigInteger = validBits;
    int k = 0;
    while (k < paramInt1)
    {
      if ((localBigInteger == null) || (localBigInteger.testBit(k)))
      {
        int m = paramArrayOfInt[i];
        int n = m >> 16 & 0xFF;
        int i1 = m >> 8 & 0xFF;
        int i2 = m & 0xFF;
        bool = (bool) && (n == i1) && (i1 == i2);
        if (paramBoolean)
        {
          int i3 = m >>> 24;
          if (i3 != 255)
          {
            if (i3 == 0)
            {
              if (j == 1) {
                j = 2;
              }
              if (transparent_index < 0) {
                transparent_index = k;
              }
            }
            else
            {
              j = 3;
            }
            bool = false;
          }
        }
        else
        {
          m |= 0xFF000000;
        }
        rgb[k] = m;
      }
      k++;
      i++;
    }
    allgrayopaque = bool;
    setTransparency(j);
  }
  
  private int calcRealMapSize(int paramInt1, int paramInt2)
  {
    int i = Math.max(1 << paramInt1, paramInt2);
    return Math.max(i, 256);
  }
  
  private BigInteger getAllValid()
  {
    int i = (map_size + 7) / 8;
    byte[] arrayOfByte = new byte[i];
    Arrays.fill(arrayOfByte, (byte)-1);
    arrayOfByte[0] = ((byte)(255 >>> i * 8 - map_size));
    return new BigInteger(1, arrayOfByte);
  }
  
  public int getTransparency()
  {
    return transparency;
  }
  
  public int[] getComponentSize()
  {
    if (nBits == null)
    {
      if (supportsAlpha)
      {
        nBits = new int[4];
        nBits[3] = 8;
      }
      else
      {
        nBits = new int[3];
      }
      nBits[0] = (nBits[1] = nBits[2] = 8);
    }
    return (int[])nBits.clone();
  }
  
  public final int getMapSize()
  {
    return map_size;
  }
  
  public final int getTransparentPixel()
  {
    return transparent_index;
  }
  
  public final void getReds(byte[] paramArrayOfByte)
  {
    for (int i = 0; i < map_size; i++) {
      paramArrayOfByte[i] = ((byte)(rgb[i] >> 16));
    }
  }
  
  public final void getGreens(byte[] paramArrayOfByte)
  {
    for (int i = 0; i < map_size; i++) {
      paramArrayOfByte[i] = ((byte)(rgb[i] >> 8));
    }
  }
  
  public final void getBlues(byte[] paramArrayOfByte)
  {
    for (int i = 0; i < map_size; i++) {
      paramArrayOfByte[i] = ((byte)rgb[i]);
    }
  }
  
  public final void getAlphas(byte[] paramArrayOfByte)
  {
    for (int i = 0; i < map_size; i++) {
      paramArrayOfByte[i] = ((byte)(rgb[i] >> 24));
    }
  }
  
  public final void getRGBs(int[] paramArrayOfInt)
  {
    System.arraycopy(rgb, 0, paramArrayOfInt, 0, map_size);
  }
  
  private void setTransparentPixel(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < map_size))
    {
      rgb[paramInt] &= 0xFFFFFF;
      transparent_index = paramInt;
      allgrayopaque = false;
      if (transparency == 1) {
        setTransparency(2);
      }
    }
  }
  
  private void setTransparency(int paramInt)
  {
    if (transparency != paramInt)
    {
      transparency = paramInt;
      if (paramInt == 1)
      {
        supportsAlpha = false;
        numComponents = 3;
        nBits = opaqueBits;
      }
      else
      {
        supportsAlpha = true;
        numComponents = 4;
        nBits = alphaBits;
      }
    }
  }
  
  private final void calculatePixelMask()
  {
    int i = pixel_bits;
    if (i == 3) {
      i = 4;
    } else if ((i > 4) && (i < 8)) {
      i = 8;
    }
    pixel_mask = ((1 << i) - 1);
  }
  
  public final int getRed(int paramInt)
  {
    return rgb[(paramInt & pixel_mask)] >> 16 & 0xFF;
  }
  
  public final int getGreen(int paramInt)
  {
    return rgb[(paramInt & pixel_mask)] >> 8 & 0xFF;
  }
  
  public final int getBlue(int paramInt)
  {
    return rgb[(paramInt & pixel_mask)] & 0xFF;
  }
  
  public final int getAlpha(int paramInt)
  {
    return rgb[(paramInt & pixel_mask)] >> 24 & 0xFF;
  }
  
  public final int getRGB(int paramInt)
  {
    return rgb[(paramInt & pixel_mask)];
  }
  
  public synchronized Object getDataElements(int paramInt, Object paramObject)
  {
    int i = paramInt >> 16 & 0xFF;
    int j = paramInt >> 8 & 0xFF;
    int k = paramInt & 0xFF;
    int m = paramInt >>> 24;
    int n = 0;
    for (int i1 = 38; (i1 >= 0) && ((n = lookupcache[i1]) != 0); i1 -= 2) {
      if (paramInt == lookupcache[(i1 + 1)]) {
        return installpixel(paramObject, n ^ 0xFFFFFFFF);
      }
    }
    int i3;
    int i4;
    if (allgrayopaque)
    {
      i1 = 256;
      i3 = (i * 77 + j * 150 + k * 29 + 128) / 256;
      for (i4 = 0; i4 < map_size; i4++) {
        if (rgb[i4] != 0)
        {
          int i2 = (rgb[i4] & 0xFF) - i3;
          if (i2 < 0) {
            i2 = -i2;
          }
          if (i2 < i1)
          {
            n = i4;
            if (i2 == 0) {
              break;
            }
            i1 = i2;
          }
        }
      }
    }
    else
    {
      int[] arrayOfInt;
      int i5;
      int i6;
      if (transparency == 1)
      {
        i1 = Integer.MAX_VALUE;
        arrayOfInt = rgb;
        for (i4 = 0; i4 < map_size; i4++)
        {
          i3 = arrayOfInt[i4];
          if ((i3 == paramInt) && (i3 != 0))
          {
            n = i4;
            i1 = 0;
            break;
          }
        }
        if (i1 != 0) {
          for (i4 = 0; i4 < map_size; i4++)
          {
            i3 = arrayOfInt[i4];
            if (i3 != 0)
            {
              i5 = (i3 >> 16 & 0xFF) - i;
              i6 = i5 * i5;
              if (i6 < i1)
              {
                i5 = (i3 >> 8 & 0xFF) - j;
                i6 += i5 * i5;
                if (i6 < i1)
                {
                  i5 = (i3 & 0xFF) - k;
                  i6 += i5 * i5;
                  if (i6 < i1)
                  {
                    n = i4;
                    i1 = i6;
                  }
                }
              }
            }
          }
        }
      }
      else if ((m == 0) && (transparent_index >= 0))
      {
        n = transparent_index;
      }
      else
      {
        i1 = Integer.MAX_VALUE;
        arrayOfInt = rgb;
        for (i3 = 0; i3 < map_size; i3++)
        {
          i4 = arrayOfInt[i3];
          if (i4 == paramInt)
          {
            if ((validBits == null) || (validBits.testBit(i3)))
            {
              n = i3;
              break;
            }
          }
          else
          {
            i5 = (i4 >> 16 & 0xFF) - i;
            i6 = i5 * i5;
            if (i6 < i1)
            {
              i5 = (i4 >> 8 & 0xFF) - j;
              i6 += i5 * i5;
              if (i6 < i1)
              {
                i5 = (i4 & 0xFF) - k;
                i6 += i5 * i5;
                if (i6 < i1)
                {
                  i5 = (i4 >>> 24) - m;
                  i6 += i5 * i5;
                  if ((i6 < i1) && ((validBits == null) || (validBits.testBit(i3))))
                  {
                    n = i3;
                    i1 = i6;
                  }
                }
              }
            }
          }
        }
      }
    }
    System.arraycopy(lookupcache, 2, lookupcache, 0, 38);
    lookupcache[39] = paramInt;
    lookupcache[38] = (n ^ 0xFFFFFFFF);
    return installpixel(paramObject, n);
  }
  
  private Object installpixel(Object paramObject, int paramInt)
  {
    switch (transferType)
    {
    case 3: 
      int[] arrayOfInt;
      if (paramObject == null) {
        paramObject = arrayOfInt = new int[1];
      } else {
        arrayOfInt = (int[])paramObject;
      }
      arrayOfInt[0] = paramInt;
      break;
    case 0: 
      byte[] arrayOfByte;
      if (paramObject == null) {
        paramObject = arrayOfByte = new byte[1];
      } else {
        arrayOfByte = (byte[])paramObject;
      }
      arrayOfByte[0] = ((byte)paramInt);
      break;
    case 1: 
      short[] arrayOfShort;
      if (paramObject == null) {
        paramObject = arrayOfShort = new short[1];
      } else {
        arrayOfShort = (short[])paramObject;
      }
      arrayOfShort[0] = ((short)paramInt);
      break;
    case 2: 
    default: 
      throw new UnsupportedOperationException("This method has not been implemented for transferType " + transferType);
    }
    return paramObject;
  }
  
  public int[] getComponents(int paramInt1, int[] paramArrayOfInt, int paramInt2)
  {
    if (paramArrayOfInt == null) {
      paramArrayOfInt = new int[paramInt2 + numComponents];
    }
    paramArrayOfInt[(paramInt2 + 0)] = getRed(paramInt1);
    paramArrayOfInt[(paramInt2 + 1)] = getGreen(paramInt1);
    paramArrayOfInt[(paramInt2 + 2)] = getBlue(paramInt1);
    if ((supportsAlpha) && (paramArrayOfInt.length - paramInt2 > 3)) {
      paramArrayOfInt[(paramInt2 + 3)] = getAlpha(paramInt1);
    }
    return paramArrayOfInt;
  }
  
  public int[] getComponents(Object paramObject, int[] paramArrayOfInt, int paramInt)
  {
    int i;
    switch (transferType)
    {
    case 0: 
      byte[] arrayOfByte = (byte[])paramObject;
      i = arrayOfByte[0] & 0xFF;
      break;
    case 1: 
      short[] arrayOfShort = (short[])paramObject;
      i = arrayOfShort[0] & 0xFFFF;
      break;
    case 3: 
      int[] arrayOfInt = (int[])paramObject;
      i = arrayOfInt[0];
      break;
    case 2: 
    default: 
      throw new UnsupportedOperationException("This method has not been implemented for transferType " + transferType);
    }
    return getComponents(i, paramArrayOfInt, paramInt);
  }
  
  public int getDataElement(int[] paramArrayOfInt, int paramInt)
  {
    int i = paramArrayOfInt[(paramInt + 0)] << 16 | paramArrayOfInt[(paramInt + 1)] << 8 | paramArrayOfInt[(paramInt + 2)];
    if (supportsAlpha) {
      i |= paramArrayOfInt[(paramInt + 3)] << 24;
    } else {
      i |= 0xFF000000;
    }
    Object localObject = getDataElements(i, null);
    int j;
    switch (transferType)
    {
    case 0: 
      byte[] arrayOfByte = (byte[])localObject;
      j = arrayOfByte[0] & 0xFF;
      break;
    case 1: 
      short[] arrayOfShort = (short[])localObject;
      j = arrayOfShort[0];
      break;
    case 3: 
      int[] arrayOfInt = (int[])localObject;
      j = arrayOfInt[0];
      break;
    case 2: 
    default: 
      throw new UnsupportedOperationException("This method has not been implemented for transferType " + transferType);
    }
    return j;
  }
  
  public Object getDataElements(int[] paramArrayOfInt, int paramInt, Object paramObject)
  {
    int i = paramArrayOfInt[(paramInt + 0)] << 16 | paramArrayOfInt[(paramInt + 1)] << 8 | paramArrayOfInt[(paramInt + 2)];
    if (supportsAlpha) {
      i |= paramArrayOfInt[(paramInt + 3)] << 24;
    } else {
      i &= 0xFF000000;
    }
    return getDataElements(i, paramObject);
  }
  
  public WritableRaster createCompatibleWritableRaster(int paramInt1, int paramInt2)
  {
    WritableRaster localWritableRaster;
    if ((pixel_bits == 1) || (pixel_bits == 2) || (pixel_bits == 4)) {
      localWritableRaster = Raster.createPackedRaster(0, paramInt1, paramInt2, 1, pixel_bits, null);
    } else if (pixel_bits <= 8) {
      localWritableRaster = Raster.createInterleavedRaster(0, paramInt1, paramInt2, 1, null);
    } else if (pixel_bits <= 16) {
      localWritableRaster = Raster.createInterleavedRaster(1, paramInt1, paramInt2, 1, null);
    } else {
      throw new UnsupportedOperationException("This method is not supported  for pixel bits > 16.");
    }
    return localWritableRaster;
  }
  
  public boolean isCompatibleRaster(Raster paramRaster)
  {
    int i = paramRaster.getSampleModel().getSampleSize(0);
    return (paramRaster.getTransferType() == transferType) && (paramRaster.getNumBands() == 1) && (1 << i >= map_size);
  }
  
  public SampleModel createCompatibleSampleModel(int paramInt1, int paramInt2)
  {
    int[] arrayOfInt = new int[1];
    arrayOfInt[0] = 0;
    if ((pixel_bits == 1) || (pixel_bits == 2) || (pixel_bits == 4)) {
      return new MultiPixelPackedSampleModel(transferType, paramInt1, paramInt2, pixel_bits);
    }
    return new ComponentSampleModel(transferType, paramInt1, paramInt2, 1, paramInt1, arrayOfInt);
  }
  
  public boolean isCompatibleSampleModel(SampleModel paramSampleModel)
  {
    if ((!(paramSampleModel instanceof ComponentSampleModel)) && (!(paramSampleModel instanceof MultiPixelPackedSampleModel))) {
      return false;
    }
    if (paramSampleModel.getTransferType() != transferType) {
      return false;
    }
    return paramSampleModel.getNumBands() == 1;
  }
  
  public BufferedImage convertToIntDiscrete(Raster paramRaster, boolean paramBoolean)
  {
    if (!isCompatibleRaster(paramRaster)) {
      throw new IllegalArgumentException("This raster is not compatiblewith this IndexColorModel.");
    }
    Object localObject1;
    if ((paramBoolean) || (transparency == 3)) {
      localObject1 = ColorModel.getRGBdefault();
    } else if (transparency == 2) {
      localObject1 = new DirectColorModel(25, 16711680, 65280, 255, 16777216);
    } else {
      localObject1 = new DirectColorModel(24, 16711680, 65280, 255);
    }
    int i = paramRaster.getWidth();
    int j = paramRaster.getHeight();
    WritableRaster localWritableRaster = ((ColorModel)localObject1).createCompatibleWritableRaster(i, j);
    Object localObject2 = null;
    int[] arrayOfInt = null;
    int k = paramRaster.getMinX();
    int m = paramRaster.getMinY();
    int n = 0;
    while (n < j)
    {
      localObject2 = paramRaster.getDataElements(k, m, i, 1, localObject2);
      if ((localObject2 instanceof int[])) {
        arrayOfInt = (int[])localObject2;
      } else {
        arrayOfInt = DataBuffer.toIntArray(localObject2);
      }
      for (int i1 = 0; i1 < i; i1++) {
        arrayOfInt[i1] = rgb[(arrayOfInt[i1] & pixel_mask)];
      }
      localWritableRaster.setDataElements(0, n, i, 1, arrayOfInt);
      n++;
      m++;
    }
    return new BufferedImage((ColorModel)localObject1, localWritableRaster, false, null);
  }
  
  public boolean isValid(int paramInt)
  {
    return (paramInt >= 0) && (paramInt < map_size) && ((validBits == null) || (validBits.testBit(paramInt)));
  }
  
  public boolean isValid()
  {
    return validBits == null;
  }
  
  public BigInteger getValidPixels()
  {
    if (validBits == null) {
      return getAllValid();
    }
    return validBits;
  }
  
  public void finalize() {}
  
  public String toString()
  {
    return new String("IndexColorModel: #pixelBits = " + pixel_bits + " numComponents = " + numComponents + " color space = " + colorSpace + " transparency = " + transparency + " transIndex   = " + transparent_index + " has alpha = " + supportsAlpha + " isAlphaPre = " + isAlphaPremultiplied);
  }
  
  static
  {
    ColorModel.loadLibraries();
    initIDs();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\IndexColorModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */