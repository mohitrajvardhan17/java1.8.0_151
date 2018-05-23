package java.awt.image;

public abstract class SampleModel
{
  protected int width;
  protected int height;
  protected int numBands;
  protected int dataType;
  
  private static native void initIDs();
  
  public SampleModel(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    long l = paramInt2 * paramInt3;
    if ((paramInt2 <= 0) || (paramInt3 <= 0)) {
      throw new IllegalArgumentException("Width (" + paramInt2 + ") and height (" + paramInt3 + ") must be > 0");
    }
    if (l >= 2147483647L) {
      throw new IllegalArgumentException("Dimensions (width=" + paramInt2 + " height=" + paramInt3 + ") are too large");
    }
    if ((paramInt1 < 0) || ((paramInt1 > 5) && (paramInt1 != 32))) {
      throw new IllegalArgumentException("Unsupported dataType: " + paramInt1);
    }
    if (paramInt4 <= 0) {
      throw new IllegalArgumentException("Number of bands must be > 0");
    }
    dataType = paramInt1;
    width = paramInt2;
    height = paramInt3;
    numBands = paramInt4;
  }
  
  public final int getWidth()
  {
    return width;
  }
  
  public final int getHeight()
  {
    return height;
  }
  
  public final int getNumBands()
  {
    return numBands;
  }
  
  public abstract int getNumDataElements();
  
  public final int getDataType()
  {
    return dataType;
  }
  
  public int getTransferType()
  {
    return dataType;
  }
  
  public int[] getPixel(int paramInt1, int paramInt2, int[] paramArrayOfInt, DataBuffer paramDataBuffer)
  {
    int[] arrayOfInt;
    if (paramArrayOfInt != null) {
      arrayOfInt = paramArrayOfInt;
    } else {
      arrayOfInt = new int[numBands];
    }
    for (int i = 0; i < numBands; i++) {
      arrayOfInt[i] = getSample(paramInt1, paramInt2, i, paramDataBuffer);
    }
    return arrayOfInt;
  }
  
  public abstract Object getDataElements(int paramInt1, int paramInt2, Object paramObject, DataBuffer paramDataBuffer);
  
  public Object getDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject, DataBuffer paramDataBuffer)
  {
    int i = getTransferType();
    int j = getNumDataElements();
    int k = 0;
    Object localObject = null;
    int m = paramInt1 + paramInt3;
    int n = paramInt2 + paramInt4;
    if ((paramInt1 < 0) || (paramInt1 >= width) || (paramInt3 > width) || (m < 0) || (m > width) || (paramInt2 < 0) || (paramInt2 >= height) || (paramInt4 > height) || (n < 0) || (n > height)) {
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates.");
    }
    int i3;
    int i5;
    int i7;
    int i9;
    switch (i)
    {
    case 0: 
      byte[] arrayOfByte2;
      if (paramObject == null) {
        arrayOfByte2 = new byte[j * paramInt3 * paramInt4];
      } else {
        arrayOfByte2 = (byte[])paramObject;
      }
      for (int i1 = paramInt2; i1 < n; i1++) {
        for (int i2 = paramInt1; i2 < m; i2++)
        {
          localObject = getDataElements(i2, i1, localObject, paramDataBuffer);
          byte[] arrayOfByte1 = (byte[])localObject;
          for (i3 = 0; i3 < j; i3++) {
            arrayOfByte2[(k++)] = arrayOfByte1[i3];
          }
        }
      }
      paramObject = arrayOfByte2;
      break;
    case 1: 
    case 2: 
      short[] arrayOfShort1;
      if (paramObject == null) {
        arrayOfShort1 = new short[j * paramInt3 * paramInt4];
      } else {
        arrayOfShort1 = (short[])paramObject;
      }
      for (i3 = paramInt2; i3 < n; i3++) {
        for (int i4 = paramInt1; i4 < m; i4++)
        {
          localObject = getDataElements(i4, i3, localObject, paramDataBuffer);
          short[] arrayOfShort2 = (short[])localObject;
          for (i5 = 0; i5 < j; i5++) {
            arrayOfShort1[(k++)] = arrayOfShort2[i5];
          }
        }
      }
      paramObject = arrayOfShort1;
      break;
    case 3: 
      int[] arrayOfInt1;
      if (paramObject == null) {
        arrayOfInt1 = new int[j * paramInt3 * paramInt4];
      } else {
        arrayOfInt1 = (int[])paramObject;
      }
      for (i5 = paramInt2; i5 < n; i5++) {
        for (int i6 = paramInt1; i6 < m; i6++)
        {
          localObject = getDataElements(i6, i5, localObject, paramDataBuffer);
          int[] arrayOfInt2 = (int[])localObject;
          for (i7 = 0; i7 < j; i7++) {
            arrayOfInt1[(k++)] = arrayOfInt2[i7];
          }
        }
      }
      paramObject = arrayOfInt1;
      break;
    case 4: 
      float[] arrayOfFloat1;
      if (paramObject == null) {
        arrayOfFloat1 = new float[j * paramInt3 * paramInt4];
      } else {
        arrayOfFloat1 = (float[])paramObject;
      }
      for (i7 = paramInt2; i7 < n; i7++) {
        for (int i8 = paramInt1; i8 < m; i8++)
        {
          localObject = getDataElements(i8, i7, localObject, paramDataBuffer);
          float[] arrayOfFloat2 = (float[])localObject;
          for (i9 = 0; i9 < j; i9++) {
            arrayOfFloat1[(k++)] = arrayOfFloat2[i9];
          }
        }
      }
      paramObject = arrayOfFloat1;
      break;
    case 5: 
      double[] arrayOfDouble1;
      if (paramObject == null) {
        arrayOfDouble1 = new double[j * paramInt3 * paramInt4];
      } else {
        arrayOfDouble1 = (double[])paramObject;
      }
      for (i9 = paramInt2; i9 < n; i9++) {
        for (int i10 = paramInt1; i10 < m; i10++)
        {
          localObject = getDataElements(i10, i9, localObject, paramDataBuffer);
          double[] arrayOfDouble2 = (double[])localObject;
          for (int i11 = 0; i11 < j; i11++) {
            arrayOfDouble1[(k++)] = arrayOfDouble2[i11];
          }
        }
      }
      paramObject = arrayOfDouble1;
    }
    return paramObject;
  }
  
  public abstract void setDataElements(int paramInt1, int paramInt2, Object paramObject, DataBuffer paramDataBuffer);
  
  public void setDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject, DataBuffer paramDataBuffer)
  {
    int i = 0;
    Object localObject = null;
    int j = getTransferType();
    int k = getNumDataElements();
    int m = paramInt1 + paramInt3;
    int n = paramInt2 + paramInt4;
    if ((paramInt1 < 0) || (paramInt1 >= width) || (paramInt3 > width) || (m < 0) || (m > width) || (paramInt2 < 0) || (paramInt2 >= height) || (paramInt4 > height) || (n < 0) || (n > height)) {
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates.");
    }
    int i3;
    int i5;
    int i7;
    int i9;
    switch (j)
    {
    case 0: 
      byte[] arrayOfByte1 = (byte[])paramObject;
      byte[] arrayOfByte2 = new byte[k];
      for (int i1 = paramInt2; i1 < n; i1++) {
        for (int i2 = paramInt1; i2 < m; i2++)
        {
          for (i3 = 0; i3 < k; i3++) {
            arrayOfByte2[i3] = arrayOfByte1[(i++)];
          }
          setDataElements(i2, i1, arrayOfByte2, paramDataBuffer);
        }
      }
      break;
    case 1: 
    case 2: 
      short[] arrayOfShort1 = (short[])paramObject;
      short[] arrayOfShort2 = new short[k];
      for (i3 = paramInt2; i3 < n; i3++) {
        for (int i4 = paramInt1; i4 < m; i4++)
        {
          for (i5 = 0; i5 < k; i5++) {
            arrayOfShort2[i5] = arrayOfShort1[(i++)];
          }
          setDataElements(i4, i3, arrayOfShort2, paramDataBuffer);
        }
      }
      break;
    case 3: 
      int[] arrayOfInt1 = (int[])paramObject;
      int[] arrayOfInt2 = new int[k];
      for (i5 = paramInt2; i5 < n; i5++) {
        for (int i6 = paramInt1; i6 < m; i6++)
        {
          for (i7 = 0; i7 < k; i7++) {
            arrayOfInt2[i7] = arrayOfInt1[(i++)];
          }
          setDataElements(i6, i5, arrayOfInt2, paramDataBuffer);
        }
      }
      break;
    case 4: 
      float[] arrayOfFloat1 = (float[])paramObject;
      float[] arrayOfFloat2 = new float[k];
      for (i7 = paramInt2; i7 < n; i7++) {
        for (int i8 = paramInt1; i8 < m; i8++)
        {
          for (i9 = 0; i9 < k; i9++) {
            arrayOfFloat2[i9] = arrayOfFloat1[(i++)];
          }
          setDataElements(i8, i7, arrayOfFloat2, paramDataBuffer);
        }
      }
      break;
    case 5: 
      double[] arrayOfDouble1 = (double[])paramObject;
      double[] arrayOfDouble2 = new double[k];
      for (i9 = paramInt2; i9 < n; i9++) {
        for (int i10 = paramInt1; i10 < m; i10++)
        {
          for (int i11 = 0; i11 < k; i11++) {
            arrayOfDouble2[i11] = arrayOfDouble1[(i++)];
          }
          setDataElements(i10, i9, arrayOfDouble2, paramDataBuffer);
        }
      }
    }
  }
  
  public float[] getPixel(int paramInt1, int paramInt2, float[] paramArrayOfFloat, DataBuffer paramDataBuffer)
  {
    float[] arrayOfFloat;
    if (paramArrayOfFloat != null) {
      arrayOfFloat = paramArrayOfFloat;
    } else {
      arrayOfFloat = new float[numBands];
    }
    for (int i = 0; i < numBands; i++) {
      arrayOfFloat[i] = getSampleFloat(paramInt1, paramInt2, i, paramDataBuffer);
    }
    return arrayOfFloat;
  }
  
  public double[] getPixel(int paramInt1, int paramInt2, double[] paramArrayOfDouble, DataBuffer paramDataBuffer)
  {
    double[] arrayOfDouble;
    if (paramArrayOfDouble != null) {
      arrayOfDouble = paramArrayOfDouble;
    } else {
      arrayOfDouble = new double[numBands];
    }
    for (int i = 0; i < numBands; i++) {
      arrayOfDouble[i] = getSampleDouble(paramInt1, paramInt2, i, paramDataBuffer);
    }
    return arrayOfDouble;
  }
  
  public int[] getPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, DataBuffer paramDataBuffer)
  {
    int i = 0;
    int j = paramInt1 + paramInt3;
    int k = paramInt2 + paramInt4;
    if ((paramInt1 < 0) || (paramInt1 >= width) || (paramInt3 > width) || (j < 0) || (j > width) || (paramInt2 < 0) || (paramInt2 >= height) || (paramInt4 > height) || (k < 0) || (k > height)) {
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates.");
    }
    int[] arrayOfInt;
    if (paramArrayOfInt != null) {
      arrayOfInt = paramArrayOfInt;
    } else {
      arrayOfInt = new int[numBands * paramInt3 * paramInt4];
    }
    for (int m = paramInt2; m < k; m++) {
      for (int n = paramInt1; n < j; n++) {
        for (int i1 = 0; i1 < numBands; i1++) {
          arrayOfInt[(i++)] = getSample(n, m, i1, paramDataBuffer);
        }
      }
    }
    return arrayOfInt;
  }
  
  public float[] getPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, float[] paramArrayOfFloat, DataBuffer paramDataBuffer)
  {
    int i = 0;
    int j = paramInt1 + paramInt3;
    int k = paramInt2 + paramInt4;
    if ((paramInt1 < 0) || (paramInt1 >= width) || (paramInt3 > width) || (j < 0) || (j > width) || (paramInt2 < 0) || (paramInt2 >= height) || (paramInt4 > height) || (k < 0) || (k > height)) {
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates.");
    }
    float[] arrayOfFloat;
    if (paramArrayOfFloat != null) {
      arrayOfFloat = paramArrayOfFloat;
    } else {
      arrayOfFloat = new float[numBands * paramInt3 * paramInt4];
    }
    for (int m = paramInt2; m < k; m++) {
      for (int n = paramInt1; n < j; n++) {
        for (int i1 = 0; i1 < numBands; i1++) {
          arrayOfFloat[(i++)] = getSampleFloat(n, m, i1, paramDataBuffer);
        }
      }
    }
    return arrayOfFloat;
  }
  
  public double[] getPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, double[] paramArrayOfDouble, DataBuffer paramDataBuffer)
  {
    int i = 0;
    int j = paramInt1 + paramInt3;
    int k = paramInt2 + paramInt4;
    if ((paramInt1 < 0) || (paramInt1 >= width) || (paramInt3 > width) || (j < 0) || (j > width) || (paramInt2 < 0) || (paramInt2 >= height) || (paramInt4 > height) || (k < 0) || (k > height)) {
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates.");
    }
    double[] arrayOfDouble;
    if (paramArrayOfDouble != null) {
      arrayOfDouble = paramArrayOfDouble;
    } else {
      arrayOfDouble = new double[numBands * paramInt3 * paramInt4];
    }
    for (int m = paramInt2; m < k; m++) {
      for (int n = paramInt1; n < j; n++) {
        for (int i1 = 0; i1 < numBands; i1++) {
          arrayOfDouble[(i++)] = getSampleDouble(n, m, i1, paramDataBuffer);
        }
      }
    }
    return arrayOfDouble;
  }
  
  public abstract int getSample(int paramInt1, int paramInt2, int paramInt3, DataBuffer paramDataBuffer);
  
  public float getSampleFloat(int paramInt1, int paramInt2, int paramInt3, DataBuffer paramDataBuffer)
  {
    float f = getSample(paramInt1, paramInt2, paramInt3, paramDataBuffer);
    return f;
  }
  
  public double getSampleDouble(int paramInt1, int paramInt2, int paramInt3, DataBuffer paramDataBuffer)
  {
    double d = getSample(paramInt1, paramInt2, paramInt3, paramDataBuffer);
    return d;
  }
  
  public int[] getSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfInt, DataBuffer paramDataBuffer)
  {
    int i = 0;
    int j = paramInt1 + paramInt3;
    int k = paramInt2 + paramInt4;
    if ((paramInt1 < 0) || (j < paramInt1) || (j > width) || (paramInt2 < 0) || (k < paramInt2) || (k > height)) {
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates.");
    }
    int[] arrayOfInt;
    if (paramArrayOfInt != null) {
      arrayOfInt = paramArrayOfInt;
    } else {
      arrayOfInt = new int[paramInt3 * paramInt4];
    }
    for (int m = paramInt2; m < k; m++) {
      for (int n = paramInt1; n < j; n++) {
        arrayOfInt[(i++)] = getSample(n, m, paramInt5, paramDataBuffer);
      }
    }
    return arrayOfInt;
  }
  
  public float[] getSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, float[] paramArrayOfFloat, DataBuffer paramDataBuffer)
  {
    int i = 0;
    int j = paramInt1 + paramInt3;
    int k = paramInt2 + paramInt4;
    if ((paramInt1 < 0) || (j < paramInt1) || (j > width) || (paramInt2 < 0) || (k < paramInt2) || (k > height)) {
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates");
    }
    float[] arrayOfFloat;
    if (paramArrayOfFloat != null) {
      arrayOfFloat = paramArrayOfFloat;
    } else {
      arrayOfFloat = new float[paramInt3 * paramInt4];
    }
    for (int m = paramInt2; m < k; m++) {
      for (int n = paramInt1; n < j; n++) {
        arrayOfFloat[(i++)] = getSampleFloat(n, m, paramInt5, paramDataBuffer);
      }
    }
    return arrayOfFloat;
  }
  
  public double[] getSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, double[] paramArrayOfDouble, DataBuffer paramDataBuffer)
  {
    int i = 0;
    int j = paramInt1 + paramInt3;
    int k = paramInt2 + paramInt4;
    if ((paramInt1 < 0) || (j < paramInt1) || (j > width) || (paramInt2 < 0) || (k < paramInt2) || (k > height)) {
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates");
    }
    double[] arrayOfDouble;
    if (paramArrayOfDouble != null) {
      arrayOfDouble = paramArrayOfDouble;
    } else {
      arrayOfDouble = new double[paramInt3 * paramInt4];
    }
    for (int m = paramInt2; m < k; m++) {
      for (int n = paramInt1; n < j; n++) {
        arrayOfDouble[(i++)] = getSampleDouble(n, m, paramInt5, paramDataBuffer);
      }
    }
    return arrayOfDouble;
  }
  
  public void setPixel(int paramInt1, int paramInt2, int[] paramArrayOfInt, DataBuffer paramDataBuffer)
  {
    for (int i = 0; i < numBands; i++) {
      setSample(paramInt1, paramInt2, i, paramArrayOfInt[i], paramDataBuffer);
    }
  }
  
  public void setPixel(int paramInt1, int paramInt2, float[] paramArrayOfFloat, DataBuffer paramDataBuffer)
  {
    for (int i = 0; i < numBands; i++) {
      setSample(paramInt1, paramInt2, i, paramArrayOfFloat[i], paramDataBuffer);
    }
  }
  
  public void setPixel(int paramInt1, int paramInt2, double[] paramArrayOfDouble, DataBuffer paramDataBuffer)
  {
    for (int i = 0; i < numBands; i++) {
      setSample(paramInt1, paramInt2, i, paramArrayOfDouble[i], paramDataBuffer);
    }
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, DataBuffer paramDataBuffer)
  {
    int i = 0;
    int j = paramInt1 + paramInt3;
    int k = paramInt2 + paramInt4;
    if ((paramInt1 < 0) || (paramInt1 >= width) || (paramInt3 > width) || (j < 0) || (j > width) || (paramInt2 < 0) || (paramInt2 >= height) || (paramInt4 > height) || (k < 0) || (k > height)) {
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates.");
    }
    for (int m = paramInt2; m < k; m++) {
      for (int n = paramInt1; n < j; n++) {
        for (int i1 = 0; i1 < numBands; i1++) {
          setSample(n, m, i1, paramArrayOfInt[(i++)], paramDataBuffer);
        }
      }
    }
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, float[] paramArrayOfFloat, DataBuffer paramDataBuffer)
  {
    int i = 0;
    int j = paramInt1 + paramInt3;
    int k = paramInt2 + paramInt4;
    if ((paramInt1 < 0) || (paramInt1 >= width) || (paramInt3 > width) || (j < 0) || (j > width) || (paramInt2 < 0) || (paramInt2 >= height) || (paramInt4 > height) || (k < 0) || (k > height)) {
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates.");
    }
    for (int m = paramInt2; m < k; m++) {
      for (int n = paramInt1; n < j; n++) {
        for (int i1 = 0; i1 < numBands; i1++) {
          setSample(n, m, i1, paramArrayOfFloat[(i++)], paramDataBuffer);
        }
      }
    }
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, double[] paramArrayOfDouble, DataBuffer paramDataBuffer)
  {
    int i = 0;
    int j = paramInt1 + paramInt3;
    int k = paramInt2 + paramInt4;
    if ((paramInt1 < 0) || (paramInt1 >= width) || (paramInt3 > width) || (j < 0) || (j > width) || (paramInt2 < 0) || (paramInt2 >= height) || (paramInt4 > height) || (k < 0) || (k > height)) {
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates.");
    }
    for (int m = paramInt2; m < k; m++) {
      for (int n = paramInt1; n < j; n++) {
        for (int i1 = 0; i1 < numBands; i1++) {
          setSample(n, m, i1, paramArrayOfDouble[(i++)], paramDataBuffer);
        }
      }
    }
  }
  
  public abstract void setSample(int paramInt1, int paramInt2, int paramInt3, int paramInt4, DataBuffer paramDataBuffer);
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, float paramFloat, DataBuffer paramDataBuffer)
  {
    int i = (int)paramFloat;
    setSample(paramInt1, paramInt2, paramInt3, i, paramDataBuffer);
  }
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, double paramDouble, DataBuffer paramDataBuffer)
  {
    int i = (int)paramDouble;
    setSample(paramInt1, paramInt2, paramInt3, i, paramDataBuffer);
  }
  
  public void setSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfInt, DataBuffer paramDataBuffer)
  {
    int i = 0;
    int j = paramInt1 + paramInt3;
    int k = paramInt2 + paramInt4;
    if ((paramInt1 < 0) || (paramInt1 >= width) || (paramInt3 > width) || (j < 0) || (j > width) || (paramInt2 < 0) || (paramInt2 >= height) || (paramInt4 > height) || (k < 0) || (k > height)) {
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates.");
    }
    for (int m = paramInt2; m < k; m++) {
      for (int n = paramInt1; n < j; n++) {
        setSample(n, m, paramInt5, paramArrayOfInt[(i++)], paramDataBuffer);
      }
    }
  }
  
  public void setSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, float[] paramArrayOfFloat, DataBuffer paramDataBuffer)
  {
    int i = 0;
    int j = paramInt1 + paramInt3;
    int k = paramInt2 + paramInt4;
    if ((paramInt1 < 0) || (paramInt1 >= width) || (paramInt3 > width) || (j < 0) || (j > width) || (paramInt2 < 0) || (paramInt2 >= height) || (paramInt4 > height) || (k < 0) || (k > height)) {
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates.");
    }
    for (int m = paramInt2; m < k; m++) {
      for (int n = paramInt1; n < j; n++) {
        setSample(n, m, paramInt5, paramArrayOfFloat[(i++)], paramDataBuffer);
      }
    }
  }
  
  public void setSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, double[] paramArrayOfDouble, DataBuffer paramDataBuffer)
  {
    int i = 0;
    int j = paramInt1 + paramInt3;
    int k = paramInt2 + paramInt4;
    if ((paramInt1 < 0) || (paramInt1 >= width) || (paramInt3 > width) || (j < 0) || (j > width) || (paramInt2 < 0) || (paramInt2 >= height) || (paramInt4 > height) || (k < 0) || (k > height)) {
      throw new ArrayIndexOutOfBoundsException("Invalid coordinates.");
    }
    for (int m = paramInt2; m < k; m++) {
      for (int n = paramInt1; n < j; n++) {
        setSample(n, m, paramInt5, paramArrayOfDouble[(i++)], paramDataBuffer);
      }
    }
  }
  
  public abstract SampleModel createCompatibleSampleModel(int paramInt1, int paramInt2);
  
  public abstract SampleModel createSubsetSampleModel(int[] paramArrayOfInt);
  
  public abstract DataBuffer createDataBuffer();
  
  public abstract int[] getSampleSize();
  
  public abstract int getSampleSize(int paramInt);
  
  static
  {
    ColorModel.loadLibraries();
    initIDs();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\SampleModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */