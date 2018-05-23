package java.awt.image;

import java.util.Arrays;

public class SinglePixelPackedSampleModel
  extends SampleModel
{
  private int[] bitMasks;
  private int[] bitOffsets;
  private int[] bitSizes;
  private int maxBitSize;
  private int scanlineStride;
  
  private static native void initIDs();
  
  public SinglePixelPackedSampleModel(int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt)
  {
    this(paramInt1, paramInt2, paramInt3, paramInt2, paramArrayOfInt);
    if ((paramInt1 != 0) && (paramInt1 != 1) && (paramInt1 != 3)) {
      throw new IllegalArgumentException("Unsupported data type " + paramInt1);
    }
  }
  
  public SinglePixelPackedSampleModel(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt)
  {
    super(paramInt1, paramInt2, paramInt3, paramArrayOfInt.length);
    if ((paramInt1 != 0) && (paramInt1 != 1) && (paramInt1 != 3)) {
      throw new IllegalArgumentException("Unsupported data type " + paramInt1);
    }
    dataType = paramInt1;
    bitMasks = ((int[])paramArrayOfInt.clone());
    scanlineStride = paramInt4;
    bitOffsets = new int[numBands];
    bitSizes = new int[numBands];
    int i = (int)((1L << DataBuffer.getDataTypeSize(paramInt1)) - 1L);
    maxBitSize = 0;
    for (int j = 0; j < numBands; j++)
    {
      int k = 0;
      int m = 0;
      bitMasks[j] &= i;
      int n = bitMasks[j];
      if (n != 0)
      {
        while ((n & 0x1) == 0)
        {
          n >>>= 1;
          k++;
        }
        while ((n & 0x1) == 1)
        {
          n >>>= 1;
          m++;
        }
        if (n != 0) {
          throw new IllegalArgumentException("Mask " + paramArrayOfInt[j] + " must be contiguous");
        }
      }
      bitOffsets[j] = k;
      bitSizes[j] = m;
      if (m > maxBitSize) {
        maxBitSize = m;
      }
    }
  }
  
  public int getNumDataElements()
  {
    return 1;
  }
  
  private long getBufferSize()
  {
    long l = scanlineStride * (height - 1) + width;
    return l;
  }
  
  public SampleModel createCompatibleSampleModel(int paramInt1, int paramInt2)
  {
    SinglePixelPackedSampleModel localSinglePixelPackedSampleModel = new SinglePixelPackedSampleModel(dataType, paramInt1, paramInt2, bitMasks);
    return localSinglePixelPackedSampleModel;
  }
  
  public DataBuffer createDataBuffer()
  {
    Object localObject = null;
    int i = (int)getBufferSize();
    switch (dataType)
    {
    case 0: 
      localObject = new DataBufferByte(i);
      break;
    case 1: 
      localObject = new DataBufferUShort(i);
      break;
    case 3: 
      localObject = new DataBufferInt(i);
    }
    return (DataBuffer)localObject;
  }
  
  public int[] getSampleSize()
  {
    return (int[])bitSizes.clone();
  }
  
  public int getSampleSize(int paramInt)
  {
    return bitSizes[paramInt];
  }
  
  public int getOffset(int paramInt1, int paramInt2)
  {
    int i = paramInt2 * scanlineStride + paramInt1;
    return i;
  }
  
  public int[] getBitOffsets()
  {
    return (int[])bitOffsets.clone();
  }
  
  public int[] getBitMasks()
  {
    return (int[])bitMasks.clone();
  }
  
  public int getScanlineStride()
  {
    return scanlineStride;
  }
  
  public SampleModel createSubsetSampleModel(int[] paramArrayOfInt)
  {
    if (paramArrayOfInt.length > numBands) {
      throw new RasterFormatException("There are only " + numBands + " bands");
    }
    int[] arrayOfInt = new int[paramArrayOfInt.length];
    for (int i = 0; i < paramArrayOfInt.length; i++) {
      arrayOfInt[i] = bitMasks[paramArrayOfInt[i]];
    }
    return new SinglePixelPackedSampleModel(dataType, width, height, scanlineStride, arrayOfInt);
  }
  
  public Object getDataElements(int paramInt1, int paramInt2, Object paramObject, DataBuffer paramDataBuffer)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 >= width) || (paramInt2 >= height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int i = getTransferType();
    switch (i)
    {
    case 0: 
      byte[] arrayOfByte;
      if (paramObject == null) {
        arrayOfByte = new byte[1];
      } else {
        arrayOfByte = (byte[])paramObject;
      }
      arrayOfByte[0] = ((byte)paramDataBuffer.getElem(paramInt2 * scanlineStride + paramInt1));
      paramObject = arrayOfByte;
      break;
    case 1: 
      short[] arrayOfShort;
      if (paramObject == null) {
        arrayOfShort = new short[1];
      } else {
        arrayOfShort = (short[])paramObject;
      }
      arrayOfShort[0] = ((short)paramDataBuffer.getElem(paramInt2 * scanlineStride + paramInt1));
      paramObject = arrayOfShort;
      break;
    case 3: 
      int[] arrayOfInt;
      if (paramObject == null) {
        arrayOfInt = new int[1];
      } else {
        arrayOfInt = (int[])paramObject;
      }
      arrayOfInt[0] = paramDataBuffer.getElem(paramInt2 * scanlineStride + paramInt1);
      paramObject = arrayOfInt;
    }
    return paramObject;
  }
  
  public int[] getPixel(int paramInt1, int paramInt2, int[] paramArrayOfInt, DataBuffer paramDataBuffer)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 >= width) || (paramInt2 >= height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int[] arrayOfInt;
    if (paramArrayOfInt == null) {
      arrayOfInt = new int[numBands];
    } else {
      arrayOfInt = paramArrayOfInt;
    }
    int i = paramDataBuffer.getElem(paramInt2 * scanlineStride + paramInt1);
    for (int j = 0; j < numBands; j++) {
      arrayOfInt[j] = ((i & bitMasks[j]) >>> bitOffsets[j]);
    }
    return arrayOfInt;
  }
  
  public int[] getPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, DataBuffer paramDataBuffer)
  {
    int i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if ((paramInt1 < 0) || (paramInt1 >= width) || (paramInt3 > width) || (i < 0) || (i > width) || (paramInt2 < 0) || (paramInt2 >= height) || (paramInt4 > height) || (j < 0) || (j > height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int[] arrayOfInt;
    if (paramArrayOfInt != null) {
      arrayOfInt = paramArrayOfInt;
    } else {
      arrayOfInt = new int[paramInt3 * paramInt4 * numBands];
    }
    int k = paramInt2 * scanlineStride + paramInt1;
    int m = 0;
    for (int n = 0; n < paramInt4; n++)
    {
      for (int i1 = 0; i1 < paramInt3; i1++)
      {
        int i2 = paramDataBuffer.getElem(k + i1);
        for (int i3 = 0; i3 < numBands; i3++) {
          arrayOfInt[(m++)] = ((i2 & bitMasks[i3]) >>> bitOffsets[i3]);
        }
      }
      k += scanlineStride;
    }
    return arrayOfInt;
  }
  
  public int getSample(int paramInt1, int paramInt2, int paramInt3, DataBuffer paramDataBuffer)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 >= width) || (paramInt2 >= height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int i = paramDataBuffer.getElem(paramInt2 * scanlineStride + paramInt1);
    return (i & bitMasks[paramInt3]) >>> bitOffsets[paramInt3];
  }
  
  public int[] getSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfInt, DataBuffer paramDataBuffer)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt3 > width) || (paramInt2 + paramInt4 > height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int[] arrayOfInt;
    if (paramArrayOfInt != null) {
      arrayOfInt = paramArrayOfInt;
    } else {
      arrayOfInt = new int[paramInt3 * paramInt4];
    }
    int i = paramInt2 * scanlineStride + paramInt1;
    int j = 0;
    for (int k = 0; k < paramInt4; k++)
    {
      for (int m = 0; m < paramInt3; m++)
      {
        int n = paramDataBuffer.getElem(i + m);
        arrayOfInt[(j++)] = ((n & bitMasks[paramInt5]) >>> bitOffsets[paramInt5]);
      }
      i += scanlineStride;
    }
    return arrayOfInt;
  }
  
  public void setDataElements(int paramInt1, int paramInt2, Object paramObject, DataBuffer paramDataBuffer)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 >= width) || (paramInt2 >= height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int i = getTransferType();
    switch (i)
    {
    case 0: 
      byte[] arrayOfByte = (byte[])paramObject;
      paramDataBuffer.setElem(paramInt2 * scanlineStride + paramInt1, arrayOfByte[0] & 0xFF);
      break;
    case 1: 
      short[] arrayOfShort = (short[])paramObject;
      paramDataBuffer.setElem(paramInt2 * scanlineStride + paramInt1, arrayOfShort[0] & 0xFFFF);
      break;
    case 3: 
      int[] arrayOfInt = (int[])paramObject;
      paramDataBuffer.setElem(paramInt2 * scanlineStride + paramInt1, arrayOfInt[0]);
    }
  }
  
  public void setPixel(int paramInt1, int paramInt2, int[] paramArrayOfInt, DataBuffer paramDataBuffer)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 >= width) || (paramInt2 >= height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int i = paramInt2 * scanlineStride + paramInt1;
    int j = paramDataBuffer.getElem(i);
    for (int k = 0; k < numBands; k++)
    {
      j &= (bitMasks[k] ^ 0xFFFFFFFF);
      j |= paramArrayOfInt[k] << bitOffsets[k] & bitMasks[k];
    }
    paramDataBuffer.setElem(i, j);
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, DataBuffer paramDataBuffer)
  {
    int i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if ((paramInt1 < 0) || (paramInt1 >= width) || (paramInt3 > width) || (i < 0) || (i > width) || (paramInt2 < 0) || (paramInt2 >= height) || (paramInt4 > height) || (j < 0) || (j > height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int k = paramInt2 * scanlineStride + paramInt1;
    int m = 0;
    for (int n = 0; n < paramInt4; n++)
    {
      for (int i1 = 0; i1 < paramInt3; i1++)
      {
        int i2 = paramDataBuffer.getElem(k + i1);
        for (int i3 = 0; i3 < numBands; i3++)
        {
          i2 &= (bitMasks[i3] ^ 0xFFFFFFFF);
          int i4 = paramArrayOfInt[(m++)];
          i2 |= i4 << bitOffsets[i3] & bitMasks[i3];
        }
        paramDataBuffer.setElem(k + i1, i2);
      }
      k += scanlineStride;
    }
  }
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, int paramInt4, DataBuffer paramDataBuffer)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 >= width) || (paramInt2 >= height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int i = paramDataBuffer.getElem(paramInt2 * scanlineStride + paramInt1);
    i &= (bitMasks[paramInt3] ^ 0xFFFFFFFF);
    i |= paramInt4 << bitOffsets[paramInt3] & bitMasks[paramInt3];
    paramDataBuffer.setElem(paramInt2 * scanlineStride + paramInt1, i);
  }
  
  public void setSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfInt, DataBuffer paramDataBuffer)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt3 > width) || (paramInt2 + paramInt4 > height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int i = paramInt2 * scanlineStride + paramInt1;
    int j = 0;
    for (int k = 0; k < paramInt4; k++)
    {
      for (int m = 0; m < paramInt3; m++)
      {
        int n = paramDataBuffer.getElem(i + m);
        n &= (bitMasks[paramInt5] ^ 0xFFFFFFFF);
        int i1 = paramArrayOfInt[(j++)];
        n |= i1 << bitOffsets[paramInt5] & bitMasks[paramInt5];
        paramDataBuffer.setElem(i + m, n);
      }
      i += scanlineStride;
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject == null) || (!(paramObject instanceof SinglePixelPackedSampleModel))) {
      return false;
    }
    SinglePixelPackedSampleModel localSinglePixelPackedSampleModel = (SinglePixelPackedSampleModel)paramObject;
    return (width == width) && (height == height) && (numBands == numBands) && (dataType == dataType) && (Arrays.equals(bitMasks, bitMasks)) && (Arrays.equals(bitOffsets, bitOffsets)) && (Arrays.equals(bitSizes, bitSizes)) && (maxBitSize == maxBitSize) && (scanlineStride == scanlineStride);
  }
  
  public int hashCode()
  {
    int i = 0;
    i = width;
    i <<= 8;
    i ^= height;
    i <<= 8;
    i ^= numBands;
    i <<= 8;
    i ^= dataType;
    i <<= 8;
    for (int j = 0; j < bitMasks.length; j++)
    {
      i ^= bitMasks[j];
      i <<= 8;
    }
    for (j = 0; j < bitOffsets.length; j++)
    {
      i ^= bitOffsets[j];
      i <<= 8;
    }
    for (j = 0; j < bitSizes.length; j++)
    {
      i ^= bitSizes[j];
      i <<= 8;
    }
    i ^= maxBitSize;
    i <<= 8;
    i ^= scanlineStride;
    return i;
  }
  
  static
  {
    ColorModel.loadLibraries();
    initIDs();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\SinglePixelPackedSampleModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */