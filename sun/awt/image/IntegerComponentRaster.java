package sun.awt.image;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.RasterFormatException;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;

public class IntegerComponentRaster
  extends SunWritableRaster
{
  static final int TYPE_CUSTOM = 0;
  static final int TYPE_BYTE_SAMPLES = 1;
  static final int TYPE_USHORT_SAMPLES = 2;
  static final int TYPE_INT_SAMPLES = 3;
  static final int TYPE_BYTE_BANDED_SAMPLES = 4;
  static final int TYPE_USHORT_BANDED_SAMPLES = 5;
  static final int TYPE_INT_BANDED_SAMPLES = 6;
  static final int TYPE_BYTE_PACKED_SAMPLES = 7;
  static final int TYPE_USHORT_PACKED_SAMPLES = 8;
  static final int TYPE_INT_PACKED_SAMPLES = 9;
  static final int TYPE_INT_8BIT_SAMPLES = 10;
  static final int TYPE_BYTE_BINARY_SAMPLES = 11;
  protected int bandOffset;
  protected int[] dataOffsets;
  protected int scanlineStride;
  protected int pixelStride;
  protected int[] data;
  protected int numDataElems;
  int type;
  private int maxX = minX + width;
  private int maxY = minY + height;
  
  private static native void initIDs();
  
  public IntegerComponentRaster(SampleModel paramSampleModel, Point paramPoint)
  {
    this(paramSampleModel, paramSampleModel.createDataBuffer(), new Rectangle(x, y, paramSampleModel.getWidth(), paramSampleModel.getHeight()), paramPoint, null);
  }
  
  public IntegerComponentRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Point paramPoint)
  {
    this(paramSampleModel, paramDataBuffer, new Rectangle(x, y, paramSampleModel.getWidth(), paramSampleModel.getHeight()), paramPoint, null);
  }
  
  public IntegerComponentRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Rectangle paramRectangle, Point paramPoint, IntegerComponentRaster paramIntegerComponentRaster)
  {
    super(paramSampleModel, paramDataBuffer, paramRectangle, paramPoint, paramIntegerComponentRaster);
    if (!(paramDataBuffer instanceof DataBufferInt)) {
      throw new RasterFormatException("IntegerComponentRasters must haveinteger DataBuffers");
    }
    DataBufferInt localDataBufferInt = (DataBufferInt)paramDataBuffer;
    if (localDataBufferInt.getNumBanks() != 1) {
      throw new RasterFormatException("DataBuffer for IntegerComponentRasters must only have 1 bank.");
    }
    data = stealData(localDataBufferInt, 0);
    if ((paramSampleModel instanceof SinglePixelPackedSampleModel))
    {
      SinglePixelPackedSampleModel localSinglePixelPackedSampleModel = (SinglePixelPackedSampleModel)paramSampleModel;
      int[] arrayOfInt = localSinglePixelPackedSampleModel.getBitOffsets();
      int i = 0;
      for (int j = 1; j < arrayOfInt.length; j++) {
        if (arrayOfInt[j] % 8 != 0) {
          i = 1;
        }
      }
      type = (i != 0 ? 9 : 10);
      scanlineStride = localSinglePixelPackedSampleModel.getScanlineStride();
      pixelStride = 1;
      dataOffsets = new int[1];
      dataOffsets[0] = localDataBufferInt.getOffset();
      bandOffset = dataOffsets[0];
      j = x - x;
      int k = y - y;
      dataOffsets[0] += j + k * scanlineStride;
      numDataElems = localSinglePixelPackedSampleModel.getNumDataElements();
    }
    else
    {
      throw new RasterFormatException("IntegerComponentRasters must have SinglePixelPackedSampleModel");
    }
    verify();
  }
  
  public int[] getDataOffsets()
  {
    return (int[])dataOffsets.clone();
  }
  
  public int getDataOffset(int paramInt)
  {
    return dataOffsets[paramInt];
  }
  
  public int getScanlineStride()
  {
    return scanlineStride;
  }
  
  public int getPixelStride()
  {
    return pixelStride;
  }
  
  public int[] getDataStorage()
  {
    return data;
  }
  
  public Object getDataElements(int paramInt1, int paramInt2, Object paramObject)
  {
    if ((paramInt1 < minX) || (paramInt2 < minY) || (paramInt1 >= maxX) || (paramInt2 >= maxY)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int[] arrayOfInt;
    if (paramObject == null) {
      arrayOfInt = new int[numDataElements];
    } else {
      arrayOfInt = (int[])paramObject;
    }
    int i = (paramInt2 - minY) * scanlineStride + (paramInt1 - minX) * pixelStride;
    for (int j = 0; j < numDataElements; j++) {
      arrayOfInt[j] = data[(dataOffsets[j] + i)];
    }
    return arrayOfInt;
  }
  
  public Object getDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject)
  {
    if ((paramInt1 < minX) || (paramInt2 < minY) || (paramInt1 + paramInt3 > maxX) || (paramInt2 + paramInt4 > maxY)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int[] arrayOfInt;
    if ((paramObject instanceof int[])) {
      arrayOfInt = (int[])paramObject;
    } else {
      arrayOfInt = new int[numDataElements * paramInt3 * paramInt4];
    }
    int i = (paramInt2 - minY) * scanlineStride + (paramInt1 - minX) * pixelStride;
    int k = 0;
    int n = 0;
    while (n < paramInt4)
    {
      int j = i;
      int m = 0;
      while (m < paramInt3)
      {
        for (int i1 = 0; i1 < numDataElements; i1++) {
          arrayOfInt[(k++)] = data[(dataOffsets[i1] + j)];
        }
        m++;
        j += pixelStride;
      }
      n++;
      i += scanlineStride;
    }
    return arrayOfInt;
  }
  
  public void setDataElements(int paramInt1, int paramInt2, Object paramObject)
  {
    if ((paramInt1 < minX) || (paramInt2 < minY) || (paramInt1 >= maxX) || (paramInt2 >= maxY)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int[] arrayOfInt = (int[])paramObject;
    int i = (paramInt2 - minY) * scanlineStride + (paramInt1 - minX) * pixelStride;
    for (int j = 0; j < numDataElements; j++) {
      data[(dataOffsets[j] + i)] = arrayOfInt[j];
    }
    markDirty();
  }
  
  public void setDataElements(int paramInt1, int paramInt2, Raster paramRaster)
  {
    int i = paramInt1 + paramRaster.getMinX();
    int j = paramInt2 + paramRaster.getMinY();
    int k = paramRaster.getWidth();
    int m = paramRaster.getHeight();
    if ((i < minX) || (j < minY) || (i + k > maxX) || (j + m > maxY)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    setDataElements(i, j, k, m, paramRaster);
  }
  
  private void setDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Raster paramRaster)
  {
    if ((paramInt3 <= 0) || (paramInt4 <= 0)) {
      return;
    }
    int i = paramRaster.getMinX();
    int j = paramRaster.getMinY();
    int[] arrayOfInt = null;
    if (((paramRaster instanceof IntegerComponentRaster)) && (pixelStride == 1) && (numDataElements == 1))
    {
      localObject = (IntegerComponentRaster)paramRaster;
      if (((IntegerComponentRaster)localObject).getNumDataElements() != 1) {
        throw new ArrayIndexOutOfBoundsException("Number of bands does not match");
      }
      arrayOfInt = ((IntegerComponentRaster)localObject).getDataStorage();
      k = ((IntegerComponentRaster)localObject).getScanlineStride();
      int m = ((IntegerComponentRaster)localObject).getDataOffset(0);
      int n = m;
      int i1 = dataOffsets[0] + (paramInt2 - minY) * scanlineStride + (paramInt1 - minX);
      if (((IntegerComponentRaster)localObject).getPixelStride() == pixelStride)
      {
        paramInt3 *= pixelStride;
        for (int i2 = 0; i2 < paramInt4; i2++)
        {
          System.arraycopy(arrayOfInt, n, data, i1, paramInt3);
          n += k;
          i1 += scanlineStride;
        }
        markDirty();
        return;
      }
    }
    Object localObject = null;
    for (int k = 0; k < paramInt4; k++)
    {
      localObject = paramRaster.getDataElements(i, j + k, paramInt3, 1, localObject);
      setDataElements(paramInt1, paramInt2 + k, paramInt3, 1, localObject);
    }
  }
  
  public void setDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject)
  {
    if ((paramInt1 < minX) || (paramInt2 < minY) || (paramInt1 + paramInt3 > maxX) || (paramInt2 + paramInt4 > maxY)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int[] arrayOfInt = (int[])paramObject;
    int i = (paramInt2 - minY) * scanlineStride + (paramInt1 - minX) * pixelStride;
    int k = 0;
    int n = 0;
    while (n < paramInt4)
    {
      int j = i;
      int m = 0;
      while (m < paramInt3)
      {
        for (int i1 = 0; i1 < numDataElements; i1++) {
          data[(dataOffsets[i1] + j)] = arrayOfInt[(k++)];
        }
        m++;
        j += pixelStride;
      }
      n++;
      i += scanlineStride;
    }
    markDirty();
  }
  
  public WritableRaster createWritableChild(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int[] paramArrayOfInt)
  {
    if (paramInt1 < minX) {
      throw new RasterFormatException("x lies outside raster");
    }
    if (paramInt2 < minY) {
      throw new RasterFormatException("y lies outside raster");
    }
    if ((paramInt1 + paramInt3 < paramInt1) || (paramInt1 + paramInt3 > minX + width)) {
      throw new RasterFormatException("(x + width) is outside raster");
    }
    if ((paramInt2 + paramInt4 < paramInt2) || (paramInt2 + paramInt4 > minY + height)) {
      throw new RasterFormatException("(y + height) is outside raster");
    }
    SampleModel localSampleModel;
    if (paramArrayOfInt != null) {
      localSampleModel = sampleModel.createSubsetSampleModel(paramArrayOfInt);
    } else {
      localSampleModel = sampleModel;
    }
    int i = paramInt5 - paramInt1;
    int j = paramInt6 - paramInt2;
    return new IntegerComponentRaster(localSampleModel, dataBuffer, new Rectangle(paramInt5, paramInt6, paramInt3, paramInt4), new Point(sampleModelTranslateX + i, sampleModelTranslateY + j), this);
  }
  
  public Raster createChild(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int[] paramArrayOfInt)
  {
    return createWritableChild(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramArrayOfInt);
  }
  
  public WritableRaster createCompatibleWritableRaster(int paramInt1, int paramInt2)
  {
    if ((paramInt1 <= 0) || (paramInt2 <= 0)) {
      throw new RasterFormatException("negative " + (paramInt1 <= 0 ? "width" : "height"));
    }
    SampleModel localSampleModel = sampleModel.createCompatibleSampleModel(paramInt1, paramInt2);
    return new IntegerComponentRaster(localSampleModel, new Point(0, 0));
  }
  
  public WritableRaster createCompatibleWritableRaster()
  {
    return createCompatibleWritableRaster(width, height);
  }
  
  protected final void verify()
  {
    if ((width <= 0) || (height <= 0) || (height > Integer.MAX_VALUE / width)) {
      throw new RasterFormatException("Invalid raster dimension");
    }
    if (dataOffsets[0] < 0) {
      throw new RasterFormatException("Data offset (" + dataOffsets[0] + ") must be >= 0");
    }
    if ((minX - sampleModelTranslateX < 0L) || (minY - sampleModelTranslateY < 0L)) {
      throw new RasterFormatException("Incorrect origin/translate: (" + minX + ", " + minY + ") / (" + sampleModelTranslateX + ", " + sampleModelTranslateY + ")");
    }
    if ((scanlineStride < 0) || (scanlineStride > Integer.MAX_VALUE / height)) {
      throw new RasterFormatException("Incorrect scanline stride: " + scanlineStride);
    }
    if (((height > 1) || (minY - sampleModelTranslateY > 0)) && (scanlineStride > data.length)) {
      throw new RasterFormatException("Incorrect scanline stride: " + scanlineStride);
    }
    int i = (height - 1) * scanlineStride;
    if ((pixelStride < 0) || (pixelStride > Integer.MAX_VALUE / width) || (pixelStride > data.length)) {
      throw new RasterFormatException("Incorrect pixel stride: " + pixelStride);
    }
    int j = (width - 1) * pixelStride;
    if (j > Integer.MAX_VALUE - i) {
      throw new RasterFormatException("Incorrect raster attributes");
    }
    j += i;
    int m = 0;
    for (int n = 0; n < numDataElements; n++)
    {
      if (dataOffsets[n] > Integer.MAX_VALUE - j) {
        throw new RasterFormatException("Incorrect band offset: " + dataOffsets[n]);
      }
      int k = j + dataOffsets[n];
      if (k > m) {
        m = k;
      }
    }
    if (data.length <= m) {
      throw new RasterFormatException("Data array too small (should be > " + m + " )");
    }
  }
  
  public String toString()
  {
    return new String("IntegerComponentRaster: width = " + width + " height = " + height + " #Bands = " + numBands + " #DataElements " + numDataElements + " xOff = " + sampleModelTranslateX + " yOff = " + sampleModelTranslateY + " dataOffset[0] " + dataOffsets[0]);
  }
  
  static
  {
    NativeLibLoader.loadLibraries();
    initIDs();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\image\IntegerComponentRaster.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */