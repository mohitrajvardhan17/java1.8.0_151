package java.awt.image;

import java.util.Arrays;

public class ComponentSampleModel
  extends SampleModel
{
  protected int[] bandOffsets;
  protected int[] bankIndices;
  protected int numBands = 1;
  protected int numBanks = 1;
  protected int scanlineStride;
  protected int pixelStride;
  
  private static native void initIDs();
  
  public ComponentSampleModel(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfInt)
  {
    super(paramInt1, paramInt2, paramInt3, paramArrayOfInt.length);
    dataType = paramInt1;
    pixelStride = paramInt4;
    scanlineStride = paramInt5;
    bandOffsets = ((int[])paramArrayOfInt.clone());
    numBands = bandOffsets.length;
    if (paramInt4 < 0) {
      throw new IllegalArgumentException("Pixel stride must be >= 0");
    }
    if (paramInt5 < 0) {
      throw new IllegalArgumentException("Scanline stride must be >= 0");
    }
    if (numBands < 1) {
      throw new IllegalArgumentException("Must have at least one band.");
    }
    if ((paramInt1 < 0) || (paramInt1 > 5)) {
      throw new IllegalArgumentException("Unsupported dataType.");
    }
    bankIndices = new int[numBands];
    for (int i = 0; i < numBands; i++) {
      bankIndices[i] = 0;
    }
    verify();
  }
  
  public ComponentSampleModel(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    super(paramInt1, paramInt2, paramInt3, paramArrayOfInt2.length);
    dataType = paramInt1;
    pixelStride = paramInt4;
    scanlineStride = paramInt5;
    bandOffsets = ((int[])paramArrayOfInt2.clone());
    bankIndices = ((int[])paramArrayOfInt1.clone());
    if (paramInt4 < 0) {
      throw new IllegalArgumentException("Pixel stride must be >= 0");
    }
    if (paramInt5 < 0) {
      throw new IllegalArgumentException("Scanline stride must be >= 0");
    }
    if ((paramInt1 < 0) || (paramInt1 > 5)) {
      throw new IllegalArgumentException("Unsupported dataType.");
    }
    int i = bankIndices[0];
    if (i < 0) {
      throw new IllegalArgumentException("Index of bank 0 is less than 0 (" + i + ")");
    }
    for (int j = 1; j < bankIndices.length; j++) {
      if (bankIndices[j] > i) {
        i = bankIndices[j];
      } else if (bankIndices[j] < 0) {
        throw new IllegalArgumentException("Index of bank " + j + " is less than 0 (" + i + ")");
      }
    }
    numBanks = (i + 1);
    numBands = bandOffsets.length;
    if (bandOffsets.length != bankIndices.length) {
      throw new IllegalArgumentException("Length of bandOffsets must equal length of bankIndices.");
    }
    verify();
  }
  
  private void verify()
  {
    int i = getBufferSize();
  }
  
  private int getBufferSize()
  {
    int i = bandOffsets[0];
    for (int j = 1; j < bandOffsets.length; j++) {
      i = Math.max(i, bandOffsets[j]);
    }
    if ((i < 0) || (i > 2147483646)) {
      throw new IllegalArgumentException("Invalid band offset");
    }
    if ((pixelStride < 0) || (pixelStride > Integer.MAX_VALUE / width)) {
      throw new IllegalArgumentException("Invalid pixel stride");
    }
    if ((scanlineStride < 0) || (scanlineStride > Integer.MAX_VALUE / height)) {
      throw new IllegalArgumentException("Invalid scanline stride");
    }
    j = i + 1;
    int k = pixelStride * (width - 1);
    if (k > Integer.MAX_VALUE - j) {
      throw new IllegalArgumentException("Invalid pixel stride");
    }
    j += k;
    k = scanlineStride * (height - 1);
    if (k > Integer.MAX_VALUE - j) {
      throw new IllegalArgumentException("Invalid scan stride");
    }
    j += k;
    return j;
  }
  
  int[] orderBands(int[] paramArrayOfInt, int paramInt)
  {
    int[] arrayOfInt1 = new int[paramArrayOfInt.length];
    int[] arrayOfInt2 = new int[paramArrayOfInt.length];
    for (int i = 0; i < arrayOfInt1.length; i++) {
      arrayOfInt1[i] = i;
    }
    for (i = 0; i < arrayOfInt2.length; i++)
    {
      int j = i;
      for (int k = i + 1; k < arrayOfInt2.length; k++) {
        if (paramArrayOfInt[arrayOfInt1[j]] > paramArrayOfInt[arrayOfInt1[k]]) {
          j = k;
        }
      }
      arrayOfInt2[arrayOfInt1[j]] = (i * paramInt);
      arrayOfInt1[j] = arrayOfInt1[i];
    }
    return arrayOfInt2;
  }
  
  public SampleModel createCompatibleSampleModel(int paramInt1, int paramInt2)
  {
    Object localObject = null;
    int i = bandOffsets[0];
    int j = bandOffsets[0];
    for (int k = 1; k < bandOffsets.length; k++)
    {
      i = Math.min(i, bandOffsets[k]);
      j = Math.max(j, bandOffsets[k]);
    }
    j -= i;
    k = bandOffsets.length;
    int m = Math.abs(pixelStride);
    int n = Math.abs(scanlineStride);
    int i1 = Math.abs(j);
    int[] arrayOfInt;
    if (m > n)
    {
      if (m > i1)
      {
        if (n > i1)
        {
          arrayOfInt = new int[bandOffsets.length];
          for (i2 = 0; i2 < k; i2++) {
            arrayOfInt[i2] = (bandOffsets[i2] - i);
          }
          n = i1 + 1;
          m = n * paramInt2;
        }
        else
        {
          arrayOfInt = orderBands(bandOffsets, n * paramInt2);
          m = k * n * paramInt2;
        }
      }
      else
      {
        m = n * paramInt2;
        arrayOfInt = orderBands(bandOffsets, m * paramInt1);
      }
    }
    else if (m > i1)
    {
      arrayOfInt = new int[bandOffsets.length];
      for (i2 = 0; i2 < k; i2++) {
        arrayOfInt[i2] = (bandOffsets[i2] - i);
      }
      m = i1 + 1;
      n = m * paramInt1;
    }
    else if (n > i1)
    {
      arrayOfInt = orderBands(bandOffsets, m * paramInt1);
      n = k * m * paramInt1;
    }
    else
    {
      n = m * paramInt1;
      arrayOfInt = orderBands(bandOffsets, n * paramInt2);
    }
    int i2 = 0;
    if (scanlineStride < 0)
    {
      i2 += n * paramInt2;
      n *= -1;
    }
    if (pixelStride < 0)
    {
      i2 += m * paramInt1;
      m *= -1;
    }
    for (int i3 = 0; i3 < k; i3++) {
      arrayOfInt[i3] += i2;
    }
    return new ComponentSampleModel(dataType, paramInt1, paramInt2, m, n, bankIndices, arrayOfInt);
  }
  
  public SampleModel createSubsetSampleModel(int[] paramArrayOfInt)
  {
    if (paramArrayOfInt.length > bankIndices.length) {
      throw new RasterFormatException("There are only " + bankIndices.length + " bands");
    }
    int[] arrayOfInt1 = new int[paramArrayOfInt.length];
    int[] arrayOfInt2 = new int[paramArrayOfInt.length];
    for (int i = 0; i < paramArrayOfInt.length; i++)
    {
      arrayOfInt1[i] = bankIndices[paramArrayOfInt[i]];
      arrayOfInt2[i] = bandOffsets[paramArrayOfInt[i]];
    }
    return new ComponentSampleModel(dataType, width, height, pixelStride, scanlineStride, arrayOfInt1, arrayOfInt2);
  }
  
  public DataBuffer createDataBuffer()
  {
    Object localObject = null;
    int i = getBufferSize();
    switch (dataType)
    {
    case 0: 
      localObject = new DataBufferByte(i, numBanks);
      break;
    case 1: 
      localObject = new DataBufferUShort(i, numBanks);
      break;
    case 2: 
      localObject = new DataBufferShort(i, numBanks);
      break;
    case 3: 
      localObject = new DataBufferInt(i, numBanks);
      break;
    case 4: 
      localObject = new DataBufferFloat(i, numBanks);
      break;
    case 5: 
      localObject = new DataBufferDouble(i, numBanks);
    }
    return (DataBuffer)localObject;
  }
  
  public int getOffset(int paramInt1, int paramInt2)
  {
    int i = paramInt2 * scanlineStride + paramInt1 * pixelStride + bandOffsets[0];
    return i;
  }
  
  public int getOffset(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = paramInt2 * scanlineStride + paramInt1 * pixelStride + bandOffsets[paramInt3];
    return i;
  }
  
  public final int[] getSampleSize()
  {
    int[] arrayOfInt = new int[numBands];
    int i = getSampleSize(0);
    for (int j = 0; j < numBands; j++) {
      arrayOfInt[j] = i;
    }
    return arrayOfInt;
  }
  
  public final int getSampleSize(int paramInt)
  {
    return DataBuffer.getDataTypeSize(dataType);
  }
  
  public final int[] getBankIndices()
  {
    return (int[])bankIndices.clone();
  }
  
  public final int[] getBandOffsets()
  {
    return (int[])bandOffsets.clone();
  }
  
  public final int getScanlineStride()
  {
    return scanlineStride;
  }
  
  public final int getPixelStride()
  {
    return pixelStride;
  }
  
  public final int getNumDataElements()
  {
    return getNumBands();
  }
  
  public Object getDataElements(int paramInt1, int paramInt2, Object paramObject, DataBuffer paramDataBuffer)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 >= width) || (paramInt2 >= height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int i = getTransferType();
    int j = getNumDataElements();
    int k = paramInt2 * scanlineStride + paramInt1 * pixelStride;
    switch (i)
    {
    case 0: 
      byte[] arrayOfByte;
      if (paramObject == null) {
        arrayOfByte = new byte[j];
      } else {
        arrayOfByte = (byte[])paramObject;
      }
      for (int m = 0; m < j; m++) {
        arrayOfByte[m] = ((byte)paramDataBuffer.getElem(bankIndices[m], k + bandOffsets[m]));
      }
      paramObject = arrayOfByte;
      break;
    case 1: 
    case 2: 
      short[] arrayOfShort;
      if (paramObject == null) {
        arrayOfShort = new short[j];
      } else {
        arrayOfShort = (short[])paramObject;
      }
      for (int n = 0; n < j; n++) {
        arrayOfShort[n] = ((short)paramDataBuffer.getElem(bankIndices[n], k + bandOffsets[n]));
      }
      paramObject = arrayOfShort;
      break;
    case 3: 
      int[] arrayOfInt;
      if (paramObject == null) {
        arrayOfInt = new int[j];
      } else {
        arrayOfInt = (int[])paramObject;
      }
      for (int i1 = 0; i1 < j; i1++) {
        arrayOfInt[i1] = paramDataBuffer.getElem(bankIndices[i1], k + bandOffsets[i1]);
      }
      paramObject = arrayOfInt;
      break;
    case 4: 
      float[] arrayOfFloat;
      if (paramObject == null) {
        arrayOfFloat = new float[j];
      } else {
        arrayOfFloat = (float[])paramObject;
      }
      for (int i2 = 0; i2 < j; i2++) {
        arrayOfFloat[i2] = paramDataBuffer.getElemFloat(bankIndices[i2], k + bandOffsets[i2]);
      }
      paramObject = arrayOfFloat;
      break;
    case 5: 
      double[] arrayOfDouble;
      if (paramObject == null) {
        arrayOfDouble = new double[j];
      } else {
        arrayOfDouble = (double[])paramObject;
      }
      for (int i3 = 0; i3 < j; i3++) {
        arrayOfDouble[i3] = paramDataBuffer.getElemDouble(bankIndices[i3], k + bandOffsets[i3]);
      }
      paramObject = arrayOfDouble;
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
    int i = paramInt2 * scanlineStride + paramInt1 * pixelStride;
    for (int j = 0; j < numBands; j++) {
      arrayOfInt[j] = paramDataBuffer.getElem(bankIndices[j], i + bandOffsets[j]);
    }
    return arrayOfInt;
  }
  
  public int[] getPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, DataBuffer paramDataBuffer)
  {
    int i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if ((paramInt1 < 0) || (paramInt1 >= width) || (paramInt3 > width) || (i < 0) || (i > width) || (paramInt2 < 0) || (paramInt2 >= height) || (paramInt2 > height) || (j < 0) || (j > height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int[] arrayOfInt;
    if (paramArrayOfInt != null) {
      arrayOfInt = paramArrayOfInt;
    } else {
      arrayOfInt = new int[paramInt3 * paramInt4 * numBands];
    }
    int k = paramInt2 * scanlineStride + paramInt1 * pixelStride;
    int m = 0;
    for (int n = 0; n < paramInt4; n++)
    {
      int i1 = k;
      for (int i2 = 0; i2 < paramInt3; i2++)
      {
        for (int i3 = 0; i3 < numBands; i3++) {
          arrayOfInt[(m++)] = paramDataBuffer.getElem(bankIndices[i3], i1 + bandOffsets[i3]);
        }
        i1 += pixelStride;
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
    int i = paramDataBuffer.getElem(bankIndices[paramInt3], paramInt2 * scanlineStride + paramInt1 * pixelStride + bandOffsets[paramInt3]);
    return i;
  }
  
  public float getSampleFloat(int paramInt1, int paramInt2, int paramInt3, DataBuffer paramDataBuffer)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 >= width) || (paramInt2 >= height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    float f = paramDataBuffer.getElemFloat(bankIndices[paramInt3], paramInt2 * scanlineStride + paramInt1 * pixelStride + bandOffsets[paramInt3]);
    return f;
  }
  
  public double getSampleDouble(int paramInt1, int paramInt2, int paramInt3, DataBuffer paramDataBuffer)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 >= width) || (paramInt2 >= height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    double d = paramDataBuffer.getElemDouble(bankIndices[paramInt3], paramInt2 * scanlineStride + paramInt1 * pixelStride + bandOffsets[paramInt3]);
    return d;
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
    int i = paramInt2 * scanlineStride + paramInt1 * pixelStride + bandOffsets[paramInt5];
    int j = 0;
    for (int k = 0; k < paramInt4; k++)
    {
      int m = i;
      for (int n = 0; n < paramInt3; n++)
      {
        arrayOfInt[(j++)] = paramDataBuffer.getElem(bankIndices[paramInt5], m);
        m += pixelStride;
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
    int j = getNumDataElements();
    int k = paramInt2 * scanlineStride + paramInt1 * pixelStride;
    switch (i)
    {
    case 0: 
      byte[] arrayOfByte = (byte[])paramObject;
      for (int m = 0; m < j; m++) {
        paramDataBuffer.setElem(bankIndices[m], k + bandOffsets[m], arrayOfByte[m] & 0xFF);
      }
      break;
    case 1: 
    case 2: 
      short[] arrayOfShort = (short[])paramObject;
      for (int n = 0; n < j; n++) {
        paramDataBuffer.setElem(bankIndices[n], k + bandOffsets[n], arrayOfShort[n] & 0xFFFF);
      }
      break;
    case 3: 
      int[] arrayOfInt = (int[])paramObject;
      for (int i1 = 0; i1 < j; i1++) {
        paramDataBuffer.setElem(bankIndices[i1], k + bandOffsets[i1], arrayOfInt[i1]);
      }
      break;
    case 4: 
      float[] arrayOfFloat = (float[])paramObject;
      for (int i2 = 0; i2 < j; i2++) {
        paramDataBuffer.setElemFloat(bankIndices[i2], k + bandOffsets[i2], arrayOfFloat[i2]);
      }
      break;
    case 5: 
      double[] arrayOfDouble = (double[])paramObject;
      for (int i3 = 0; i3 < j; i3++) {
        paramDataBuffer.setElemDouble(bankIndices[i3], k + bandOffsets[i3], arrayOfDouble[i3]);
      }
    }
  }
  
  public void setPixel(int paramInt1, int paramInt2, int[] paramArrayOfInt, DataBuffer paramDataBuffer)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 >= width) || (paramInt2 >= height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int i = paramInt2 * scanlineStride + paramInt1 * pixelStride;
    for (int j = 0; j < numBands; j++) {
      paramDataBuffer.setElem(bankIndices[j], i + bandOffsets[j], paramArrayOfInt[j]);
    }
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, DataBuffer paramDataBuffer)
  {
    int i = paramInt1 + paramInt3;
    int j = paramInt2 + paramInt4;
    if ((paramInt1 < 0) || (paramInt1 >= width) || (paramInt3 > width) || (i < 0) || (i > width) || (paramInt2 < 0) || (paramInt2 >= height) || (paramInt4 > height) || (j < 0) || (j > height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int k = paramInt2 * scanlineStride + paramInt1 * pixelStride;
    int m = 0;
    for (int n = 0; n < paramInt4; n++)
    {
      int i1 = k;
      for (int i2 = 0; i2 < paramInt3; i2++)
      {
        for (int i3 = 0; i3 < numBands; i3++) {
          paramDataBuffer.setElem(bankIndices[i3], i1 + bandOffsets[i3], paramArrayOfInt[(m++)]);
        }
        i1 += pixelStride;
      }
      k += scanlineStride;
    }
  }
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, int paramInt4, DataBuffer paramDataBuffer)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 >= width) || (paramInt2 >= height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    paramDataBuffer.setElem(bankIndices[paramInt3], paramInt2 * scanlineStride + paramInt1 * pixelStride + bandOffsets[paramInt3], paramInt4);
  }
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, float paramFloat, DataBuffer paramDataBuffer)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 >= width) || (paramInt2 >= height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    paramDataBuffer.setElemFloat(bankIndices[paramInt3], paramInt2 * scanlineStride + paramInt1 * pixelStride + bandOffsets[paramInt3], paramFloat);
  }
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, double paramDouble, DataBuffer paramDataBuffer)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 >= width) || (paramInt2 >= height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    paramDataBuffer.setElemDouble(bankIndices[paramInt3], paramInt2 * scanlineStride + paramInt1 * pixelStride + bandOffsets[paramInt3], paramDouble);
  }
  
  public void setSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfInt, DataBuffer paramDataBuffer)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt3 > width) || (paramInt2 + paramInt4 > height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int i = paramInt2 * scanlineStride + paramInt1 * pixelStride + bandOffsets[paramInt5];
    int j = 0;
    for (int k = 0; k < paramInt4; k++)
    {
      int m = i;
      for (int n = 0; n < paramInt3; n++)
      {
        paramDataBuffer.setElem(bankIndices[paramInt5], m, paramArrayOfInt[(j++)]);
        m += pixelStride;
      }
      i += scanlineStride;
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject == null) || (!(paramObject instanceof ComponentSampleModel))) {
      return false;
    }
    ComponentSampleModel localComponentSampleModel = (ComponentSampleModel)paramObject;
    return (width == width) && (height == height) && (numBands == numBands) && (dataType == dataType) && (Arrays.equals(bandOffsets, bandOffsets)) && (Arrays.equals(bankIndices, bankIndices)) && (numBands == numBands) && (numBanks == numBanks) && (scanlineStride == scanlineStride) && (pixelStride == pixelStride);
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
    for (int j = 0; j < bandOffsets.length; j++)
    {
      i ^= bandOffsets[j];
      i <<= 8;
    }
    for (j = 0; j < bankIndices.length; j++)
    {
      i ^= bankIndices[j];
      i <<= 8;
    }
    i ^= numBands;
    i <<= 8;
    i ^= numBanks;
    i <<= 8;
    i ^= scanlineStride;
    i <<= 8;
    i ^= pixelStride;
    return i;
  }
  
  static
  {
    ColorModel.loadLibraries();
    initIDs();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\ComponentSampleModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */