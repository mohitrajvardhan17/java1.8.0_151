package sun.awt.image;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.RasterFormatException;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;

public class ShortComponentRaster
  extends SunWritableRaster
{
  protected int bandOffset;
  protected int[] dataOffsets;
  protected int scanlineStride;
  protected int pixelStride;
  protected short[] data;
  int type;
  private int maxX = minX + width;
  private int maxY = minY + height;
  
  private static native void initIDs();
  
  public ShortComponentRaster(SampleModel paramSampleModel, Point paramPoint)
  {
    this(paramSampleModel, paramSampleModel.createDataBuffer(), new Rectangle(x, y, paramSampleModel.getWidth(), paramSampleModel.getHeight()), paramPoint, null);
  }
  
  public ShortComponentRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Point paramPoint)
  {
    this(paramSampleModel, paramDataBuffer, new Rectangle(x, y, paramSampleModel.getWidth(), paramSampleModel.getHeight()), paramPoint, null);
  }
  
  public ShortComponentRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Rectangle paramRectangle, Point paramPoint, ShortComponentRaster paramShortComponentRaster)
  {
    super(paramSampleModel, paramDataBuffer, paramRectangle, paramPoint, paramShortComponentRaster);
    if (!(paramDataBuffer instanceof DataBufferUShort)) {
      throw new RasterFormatException("ShortComponentRasters must have short DataBuffers");
    }
    DataBufferUShort localDataBufferUShort = (DataBufferUShort)paramDataBuffer;
    data = stealData(localDataBufferUShort, 0);
    if (localDataBufferUShort.getNumBanks() != 1) {
      throw new RasterFormatException("DataBuffer for ShortComponentRasters must only have 1 bank.");
    }
    int i = localDataBufferUShort.getOffset();
    Object localObject;
    int j;
    int k;
    if ((paramSampleModel instanceof ComponentSampleModel))
    {
      localObject = (ComponentSampleModel)paramSampleModel;
      type = 2;
      scanlineStride = ((ComponentSampleModel)localObject).getScanlineStride();
      pixelStride = ((ComponentSampleModel)localObject).getPixelStride();
      dataOffsets = ((ComponentSampleModel)localObject).getBandOffsets();
      j = x - x;
      k = y - y;
      for (int m = 0; m < getNumDataElements(); m++) {
        dataOffsets[m] += i + j * pixelStride + k * scanlineStride;
      }
    }
    else if ((paramSampleModel instanceof SinglePixelPackedSampleModel))
    {
      localObject = (SinglePixelPackedSampleModel)paramSampleModel;
      type = 8;
      scanlineStride = ((SinglePixelPackedSampleModel)localObject).getScanlineStride();
      pixelStride = 1;
      dataOffsets = new int[1];
      dataOffsets[0] = i;
      j = x - x;
      k = y - y;
      dataOffsets[0] += j + k * scanlineStride;
    }
    else
    {
      throw new RasterFormatException("ShortComponentRasters must haveComponentSampleModel or SinglePixelPackedSampleModel");
    }
    bandOffset = dataOffsets[0];
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
  
  public short[] getDataStorage()
  {
    return data;
  }
  
  public Object getDataElements(int paramInt1, int paramInt2, Object paramObject)
  {
    if ((paramInt1 < minX) || (paramInt2 < minY) || (paramInt1 >= maxX) || (paramInt2 >= maxY)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    short[] arrayOfShort;
    if (paramObject == null) {
      arrayOfShort = new short[numDataElements];
    } else {
      arrayOfShort = (short[])paramObject;
    }
    int i = (paramInt2 - minY) * scanlineStride + (paramInt1 - minX) * pixelStride;
    for (int j = 0; j < numDataElements; j++) {
      arrayOfShort[j] = data[(dataOffsets[j] + i)];
    }
    return arrayOfShort;
  }
  
  public Object getDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject)
  {
    if ((paramInt1 < minX) || (paramInt2 < minY) || (paramInt1 + paramInt3 > maxX) || (paramInt2 + paramInt4 > maxY)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    short[] arrayOfShort;
    if (paramObject == null) {
      arrayOfShort = new short[paramInt3 * paramInt4 * numDataElements];
    } else {
      arrayOfShort = (short[])paramObject;
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
          arrayOfShort[(k++)] = data[(dataOffsets[i1] + j)];
        }
        m++;
        j += pixelStride;
      }
      n++;
      i += scanlineStride;
    }
    return arrayOfShort;
  }
  
  public short[] getShortData(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, short[] paramArrayOfShort)
  {
    if ((paramInt1 < minX) || (paramInt2 < minY) || (paramInt1 + paramInt3 > maxX) || (paramInt2 + paramInt4 > maxY)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    if (paramArrayOfShort == null) {
      paramArrayOfShort = new short[numDataElements * paramInt3 * paramInt4];
    }
    int i = (paramInt2 - minY) * scanlineStride + (paramInt1 - minX) * pixelStride + dataOffsets[paramInt5];
    int k = 0;
    int n;
    if (pixelStride == 1)
    {
      if (scanlineStride == paramInt3)
      {
        System.arraycopy(data, i, paramArrayOfShort, 0, paramInt3 * paramInt4);
      }
      else
      {
        n = 0;
        while (n < paramInt4)
        {
          System.arraycopy(data, i, paramArrayOfShort, k, paramInt3);
          k += paramInt3;
          n++;
          i += scanlineStride;
        }
      }
    }
    else
    {
      n = 0;
      while (n < paramInt4)
      {
        int j = i;
        int m = 0;
        while (m < paramInt3)
        {
          paramArrayOfShort[(k++)] = data[j];
          m++;
          j += pixelStride;
        }
        n++;
        i += scanlineStride;
      }
    }
    return paramArrayOfShort;
  }
  
  public short[] getShortData(int paramInt1, int paramInt2, int paramInt3, int paramInt4, short[] paramArrayOfShort)
  {
    if ((paramInt1 < minX) || (paramInt2 < minY) || (paramInt1 + paramInt3 > maxX) || (paramInt2 + paramInt4 > maxY)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    if (paramArrayOfShort == null) {
      paramArrayOfShort = new short[numDataElements * paramInt3 * paramInt4];
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
          paramArrayOfShort[(k++)] = data[(dataOffsets[i1] + j)];
        }
        m++;
        j += pixelStride;
      }
      n++;
      i += scanlineStride;
    }
    return paramArrayOfShort;
  }
  
  public void setDataElements(int paramInt1, int paramInt2, Object paramObject)
  {
    if ((paramInt1 < minX) || (paramInt2 < minY) || (paramInt1 >= maxX) || (paramInt2 >= maxY)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    short[] arrayOfShort = (short[])paramObject;
    int i = (paramInt2 - minY) * scanlineStride + (paramInt1 - minX) * pixelStride;
    for (int j = 0; j < numDataElements; j++) {
      data[(dataOffsets[j] + i)] = arrayOfShort[j];
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
    short[] arrayOfShort = (short[])paramObject;
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
          data[(dataOffsets[i1] + j)] = arrayOfShort[(k++)];
        }
        m++;
        j += pixelStride;
      }
      n++;
      i += scanlineStride;
    }
    markDirty();
  }
  
  public void putShortData(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, short[] paramArrayOfShort)
  {
    if ((paramInt1 < minX) || (paramInt2 < minY) || (paramInt1 + paramInt3 > maxX) || (paramInt2 + paramInt4 > maxY)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int i = (paramInt2 - minY) * scanlineStride + (paramInt1 - minX) * pixelStride + dataOffsets[paramInt5];
    int k = 0;
    int n;
    if (pixelStride == 1)
    {
      if (scanlineStride == paramInt3)
      {
        System.arraycopy(paramArrayOfShort, 0, data, i, paramInt3 * paramInt4);
      }
      else
      {
        n = 0;
        while (n < paramInt4)
        {
          System.arraycopy(paramArrayOfShort, k, data, i, paramInt3);
          k += paramInt3;
          n++;
          i += scanlineStride;
        }
      }
    }
    else
    {
      n = 0;
      while (n < paramInt4)
      {
        int j = i;
        int m = 0;
        while (m < paramInt3)
        {
          data[j] = paramArrayOfShort[(k++)];
          m++;
          j += pixelStride;
        }
        n++;
        i += scanlineStride;
      }
    }
    markDirty();
  }
  
  public void putShortData(int paramInt1, int paramInt2, int paramInt3, int paramInt4, short[] paramArrayOfShort)
  {
    if ((paramInt1 < minX) || (paramInt2 < minY) || (paramInt1 + paramInt3 > maxX) || (paramInt2 + paramInt4 > maxY)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
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
          data[(dataOffsets[i1] + j)] = paramArrayOfShort[(k++)];
        }
        m++;
        j += pixelStride;
      }
      n++;
      i += scanlineStride;
    }
    markDirty();
  }
  
  public Raster createChild(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int[] paramArrayOfInt)
  {
    WritableRaster localWritableRaster = createWritableChild(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramArrayOfInt);
    return localWritableRaster;
  }
  
  public WritableRaster createWritableChild(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int[] paramArrayOfInt)
  {
    if (paramInt1 < minX) {
      throw new RasterFormatException("x lies outside the raster");
    }
    if (paramInt2 < minY) {
      throw new RasterFormatException("y lies outside the raster");
    }
    if ((paramInt1 + paramInt3 < paramInt1) || (paramInt1 + paramInt3 > minX + width)) {
      throw new RasterFormatException("(x + width) is outside of Raster");
    }
    if ((paramInt2 + paramInt4 < paramInt2) || (paramInt2 + paramInt4 > minY + height)) {
      throw new RasterFormatException("(y + height) is outside of Raster");
    }
    SampleModel localSampleModel;
    if (paramArrayOfInt != null) {
      localSampleModel = sampleModel.createSubsetSampleModel(paramArrayOfInt);
    } else {
      localSampleModel = sampleModel;
    }
    int i = paramInt5 - paramInt1;
    int j = paramInt6 - paramInt2;
    return new ShortComponentRaster(localSampleModel, dataBuffer, new Rectangle(paramInt5, paramInt6, paramInt3, paramInt4), new Point(sampleModelTranslateX + i, sampleModelTranslateY + j), this);
  }
  
  public WritableRaster createCompatibleWritableRaster(int paramInt1, int paramInt2)
  {
    if ((paramInt1 <= 0) || (paramInt2 <= 0)) {
      throw new RasterFormatException("negative " + (paramInt1 <= 0 ? "width" : "height"));
    }
    SampleModel localSampleModel = sampleModel.createCompatibleSampleModel(paramInt1, paramInt2);
    return new ShortComponentRaster(localSampleModel, new Point(0, 0));
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
    for (int i = 0; i < dataOffsets.length; i++) {
      if (dataOffsets[i] < 0) {
        throw new RasterFormatException("Data offsets for band " + i + "(" + dataOffsets[i] + ") must be >= 0");
      }
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
    i = (height - 1) * scanlineStride;
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
    return new String("ShortComponentRaster: width = " + width + " height = " + height + " #numDataElements " + numDataElements);
  }
  
  static
  {
    NativeLibLoader.loadLibraries();
    initIDs();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\image\ShortComponentRaster.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */