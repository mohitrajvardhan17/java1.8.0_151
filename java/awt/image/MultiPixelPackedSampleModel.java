package java.awt.image;

public class MultiPixelPackedSampleModel
  extends SampleModel
{
  int pixelBitStride;
  int bitMask;
  int pixelsPerDataElement;
  int dataElementSize;
  int dataBitOffset;
  int scanlineStride;
  
  public MultiPixelPackedSampleModel(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this(paramInt1, paramInt2, paramInt3, paramInt4, (paramInt2 * paramInt4 + DataBuffer.getDataTypeSize(paramInt1) - 1) / DataBuffer.getDataTypeSize(paramInt1), 0);
    if ((paramInt1 != 0) && (paramInt1 != 1) && (paramInt1 != 3)) {
      throw new IllegalArgumentException("Unsupported data type " + paramInt1);
    }
  }
  
  public MultiPixelPackedSampleModel(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    super(paramInt1, paramInt2, paramInt3, 1);
    if ((paramInt1 != 0) && (paramInt1 != 1) && (paramInt1 != 3)) {
      throw new IllegalArgumentException("Unsupported data type " + paramInt1);
    }
    dataType = paramInt1;
    pixelBitStride = paramInt4;
    scanlineStride = paramInt5;
    dataBitOffset = paramInt6;
    dataElementSize = DataBuffer.getDataTypeSize(paramInt1);
    pixelsPerDataElement = (dataElementSize / paramInt4);
    if (pixelsPerDataElement * paramInt4 != dataElementSize) {
      throw new RasterFormatException("MultiPixelPackedSampleModel does not allow pixels to span data element boundaries");
    }
    bitMask = ((1 << paramInt4) - 1);
  }
  
  public SampleModel createCompatibleSampleModel(int paramInt1, int paramInt2)
  {
    MultiPixelPackedSampleModel localMultiPixelPackedSampleModel = new MultiPixelPackedSampleModel(dataType, paramInt1, paramInt2, pixelBitStride);
    return localMultiPixelPackedSampleModel;
  }
  
  public DataBuffer createDataBuffer()
  {
    Object localObject = null;
    int i = scanlineStride * height;
    switch (dataType)
    {
    case 0: 
      localObject = new DataBufferByte(i + (dataBitOffset + 7) / 8);
      break;
    case 1: 
      localObject = new DataBufferUShort(i + (dataBitOffset + 15) / 16);
      break;
    case 3: 
      localObject = new DataBufferInt(i + (dataBitOffset + 31) / 32);
    }
    return (DataBuffer)localObject;
  }
  
  public int getNumDataElements()
  {
    return 1;
  }
  
  public int[] getSampleSize()
  {
    int[] arrayOfInt = { pixelBitStride };
    return arrayOfInt;
  }
  
  public int getSampleSize(int paramInt)
  {
    return pixelBitStride;
  }
  
  public int getOffset(int paramInt1, int paramInt2)
  {
    int i = paramInt2 * scanlineStride;
    i += (paramInt1 * pixelBitStride + dataBitOffset) / dataElementSize;
    return i;
  }
  
  public int getBitOffset(int paramInt)
  {
    return (paramInt * pixelBitStride + dataBitOffset) % dataElementSize;
  }
  
  public int getScanlineStride()
  {
    return scanlineStride;
  }
  
  public int getPixelBitStride()
  {
    return pixelBitStride;
  }
  
  public int getDataBitOffset()
  {
    return dataBitOffset;
  }
  
  public int getTransferType()
  {
    if (pixelBitStride > 16) {
      return 3;
    }
    if (pixelBitStride > 8) {
      return 1;
    }
    return 0;
  }
  
  public SampleModel createSubsetSampleModel(int[] paramArrayOfInt)
  {
    if ((paramArrayOfInt != null) && (paramArrayOfInt.length != 1)) {
      throw new RasterFormatException("MultiPixelPackedSampleModel has only one band.");
    }
    SampleModel localSampleModel = createCompatibleSampleModel(width, height);
    return localSampleModel;
  }
  
  public int getSample(int paramInt1, int paramInt2, int paramInt3, DataBuffer paramDataBuffer)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 >= width) || (paramInt2 >= height) || (paramInt3 != 0)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int i = dataBitOffset + paramInt1 * pixelBitStride;
    int j = paramDataBuffer.getElem(paramInt2 * scanlineStride + i / dataElementSize);
    int k = dataElementSize - (i & dataElementSize - 1) - pixelBitStride;
    return j >> k & bitMask;
  }
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, int paramInt4, DataBuffer paramDataBuffer)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 >= width) || (paramInt2 >= height) || (paramInt3 != 0)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int i = dataBitOffset + paramInt1 * pixelBitStride;
    int j = paramInt2 * scanlineStride + i / dataElementSize;
    int k = dataElementSize - (i & dataElementSize - 1) - pixelBitStride;
    int m = paramDataBuffer.getElem(j);
    m &= (bitMask << k ^ 0xFFFFFFFF);
    m |= (paramInt4 & bitMask) << k;
    paramDataBuffer.setElem(j, m);
  }
  
  public Object getDataElements(int paramInt1, int paramInt2, Object paramObject, DataBuffer paramDataBuffer)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 >= width) || (paramInt2 >= height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int i = getTransferType();
    int j = dataBitOffset + paramInt1 * pixelBitStride;
    int k = dataElementSize - (j & dataElementSize - 1) - pixelBitStride;
    int m = 0;
    switch (i)
    {
    case 0: 
      byte[] arrayOfByte;
      if (paramObject == null) {
        arrayOfByte = new byte[1];
      } else {
        arrayOfByte = (byte[])paramObject;
      }
      m = paramDataBuffer.getElem(paramInt2 * scanlineStride + j / dataElementSize);
      arrayOfByte[0] = ((byte)(m >> k & bitMask));
      paramObject = arrayOfByte;
      break;
    case 1: 
      short[] arrayOfShort;
      if (paramObject == null) {
        arrayOfShort = new short[1];
      } else {
        arrayOfShort = (short[])paramObject;
      }
      m = paramDataBuffer.getElem(paramInt2 * scanlineStride + j / dataElementSize);
      arrayOfShort[0] = ((short)(m >> k & bitMask));
      paramObject = arrayOfShort;
      break;
    case 3: 
      int[] arrayOfInt;
      if (paramObject == null) {
        arrayOfInt = new int[1];
      } else {
        arrayOfInt = (int[])paramObject;
      }
      m = paramDataBuffer.getElem(paramInt2 * scanlineStride + j / dataElementSize);
      arrayOfInt[0] = (m >> k & bitMask);
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
    if (paramArrayOfInt != null) {
      arrayOfInt = paramArrayOfInt;
    } else {
      arrayOfInt = new int[numBands];
    }
    int i = dataBitOffset + paramInt1 * pixelBitStride;
    int j = paramDataBuffer.getElem(paramInt2 * scanlineStride + i / dataElementSize);
    int k = dataElementSize - (i & dataElementSize - 1) - pixelBitStride;
    arrayOfInt[0] = (j >> k & bitMask);
    return arrayOfInt;
  }
  
  public void setDataElements(int paramInt1, int paramInt2, Object paramObject, DataBuffer paramDataBuffer)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 >= width) || (paramInt2 >= height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int i = getTransferType();
    int j = dataBitOffset + paramInt1 * pixelBitStride;
    int k = paramInt2 * scanlineStride + j / dataElementSize;
    int m = dataElementSize - (j & dataElementSize - 1) - pixelBitStride;
    int n = paramDataBuffer.getElem(k);
    n &= (bitMask << m ^ 0xFFFFFFFF);
    switch (i)
    {
    case 0: 
      byte[] arrayOfByte = (byte[])paramObject;
      n |= (arrayOfByte[0] & 0xFF & bitMask) << m;
      paramDataBuffer.setElem(k, n);
      break;
    case 1: 
      short[] arrayOfShort = (short[])paramObject;
      n |= (arrayOfShort[0] & 0xFFFF & bitMask) << m;
      paramDataBuffer.setElem(k, n);
      break;
    case 3: 
      int[] arrayOfInt = (int[])paramObject;
      n |= (arrayOfInt[0] & bitMask) << m;
      paramDataBuffer.setElem(k, n);
    }
  }
  
  public void setPixel(int paramInt1, int paramInt2, int[] paramArrayOfInt, DataBuffer paramDataBuffer)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 >= width) || (paramInt2 >= height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int i = dataBitOffset + paramInt1 * pixelBitStride;
    int j = paramInt2 * scanlineStride + i / dataElementSize;
    int k = dataElementSize - (i & dataElementSize - 1) - pixelBitStride;
    int m = paramDataBuffer.getElem(j);
    m &= (bitMask << k ^ 0xFFFFFFFF);
    m |= (paramArrayOfInt[0] & bitMask) << k;
    paramDataBuffer.setElem(j, m);
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject == null) || (!(paramObject instanceof MultiPixelPackedSampleModel))) {
      return false;
    }
    MultiPixelPackedSampleModel localMultiPixelPackedSampleModel = (MultiPixelPackedSampleModel)paramObject;
    return (width == width) && (height == height) && (numBands == numBands) && (dataType == dataType) && (pixelBitStride == pixelBitStride) && (bitMask == bitMask) && (pixelsPerDataElement == pixelsPerDataElement) && (dataElementSize == dataElementSize) && (dataBitOffset == dataBitOffset) && (scanlineStride == scanlineStride);
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
    i ^= pixelBitStride;
    i <<= 8;
    i ^= bitMask;
    i <<= 8;
    i ^= pixelsPerDataElement;
    i <<= 8;
    i ^= dataElementSize;
    i <<= 8;
    i ^= dataBitOffset;
    i <<= 8;
    i ^= scanlineStride;
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\MultiPixelPackedSampleModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */