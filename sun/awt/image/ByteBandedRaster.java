package sun.awt.image;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BandedSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.RasterFormatException;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

public class ByteBandedRaster
  extends SunWritableRaster
{
  int[] dataOffsets;
  int scanlineStride;
  byte[][] data;
  private int maxX = minX + width;
  private int maxY = minY + height;
  
  public ByteBandedRaster(SampleModel paramSampleModel, Point paramPoint)
  {
    this(paramSampleModel, paramSampleModel.createDataBuffer(), new Rectangle(x, y, paramSampleModel.getWidth(), paramSampleModel.getHeight()), paramPoint, null);
  }
  
  public ByteBandedRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Point paramPoint)
  {
    this(paramSampleModel, paramDataBuffer, new Rectangle(x, y, paramSampleModel.getWidth(), paramSampleModel.getHeight()), paramPoint, null);
  }
  
  public ByteBandedRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Rectangle paramRectangle, Point paramPoint, ByteBandedRaster paramByteBandedRaster)
  {
    super(paramSampleModel, paramDataBuffer, paramRectangle, paramPoint, paramByteBandedRaster);
    if (!(paramDataBuffer instanceof DataBufferByte)) {
      throw new RasterFormatException("ByteBandedRaster must havebyte DataBuffers");
    }
    DataBufferByte localDataBufferByte = (DataBufferByte)paramDataBuffer;
    if ((paramSampleModel instanceof BandedSampleModel))
    {
      BandedSampleModel localBandedSampleModel = (BandedSampleModel)paramSampleModel;
      scanlineStride = localBandedSampleModel.getScanlineStride();
      int[] arrayOfInt1 = localBandedSampleModel.getBankIndices();
      int[] arrayOfInt2 = localBandedSampleModel.getBandOffsets();
      int[] arrayOfInt3 = localDataBufferByte.getOffsets();
      dataOffsets = new int[arrayOfInt1.length];
      data = new byte[arrayOfInt1.length][];
      int i = x - x;
      int j = y - y;
      for (int k = 0; k < arrayOfInt1.length; k++)
      {
        data[k] = stealData(localDataBufferByte, arrayOfInt1[k]);
        dataOffsets[k] = (arrayOfInt3[arrayOfInt1[k]] + i + j * scanlineStride + arrayOfInt2[k]);
      }
    }
    else
    {
      throw new RasterFormatException("ByteBandedRasters must haveBandedSampleModels");
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
    return 1;
  }
  
  public byte[][] getDataStorage()
  {
    return data;
  }
  
  public byte[] getDataStorage(int paramInt)
  {
    return data[paramInt];
  }
  
  public Object getDataElements(int paramInt1, int paramInt2, Object paramObject)
  {
    if ((paramInt1 < minX) || (paramInt2 < minY) || (paramInt1 >= maxX) || (paramInt2 >= maxY)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    byte[] arrayOfByte;
    if (paramObject == null) {
      arrayOfByte = new byte[numDataElements];
    } else {
      arrayOfByte = (byte[])paramObject;
    }
    int i = (paramInt2 - minY) * scanlineStride + (paramInt1 - minX);
    for (int j = 0; j < numDataElements; j++) {
      arrayOfByte[j] = data[j][(dataOffsets[j] + i)];
    }
    return arrayOfByte;
  }
  
  public Object getDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject)
  {
    if ((paramInt1 < minX) || (paramInt2 < minY) || (paramInt1 + paramInt3 > maxX) || (paramInt2 + paramInt4 > maxY)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    byte[] arrayOfByte1;
    if (paramObject == null) {
      arrayOfByte1 = new byte[numDataElements * paramInt3 * paramInt4];
    } else {
      arrayOfByte1 = (byte[])paramObject;
    }
    int i = (paramInt2 - minY) * scanlineStride + (paramInt1 - minX);
    for (int j = 0; j < numDataElements; j++)
    {
      int k = j;
      byte[] arrayOfByte2 = data[j];
      int m = dataOffsets[j];
      int n = i;
      int i1 = 0;
      while (i1 < paramInt4)
      {
        int i2 = m + n;
        for (int i3 = 0; i3 < paramInt3; i3++)
        {
          arrayOfByte1[k] = arrayOfByte2[(i2++)];
          k += numDataElements;
        }
        i1++;
        n += scanlineStride;
      }
    }
    return arrayOfByte1;
  }
  
  public byte[] getByteData(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, byte[] paramArrayOfByte)
  {
    if ((paramInt1 < minX) || (paramInt2 < minY) || (paramInt1 + paramInt3 > maxX) || (paramInt2 + paramInt4 > maxY)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    if (paramArrayOfByte == null) {
      paramArrayOfByte = new byte[scanlineStride * paramInt4];
    }
    int i = (paramInt2 - minY) * scanlineStride + (paramInt1 - minX) + dataOffsets[paramInt5];
    if (scanlineStride == paramInt3)
    {
      System.arraycopy(data[paramInt5], i, paramArrayOfByte, 0, paramInt3 * paramInt4);
    }
    else
    {
      int j = 0;
      int k = 0;
      while (k < paramInt4)
      {
        System.arraycopy(data[paramInt5], i, paramArrayOfByte, j, paramInt3);
        j += paramInt3;
        k++;
        i += scanlineStride;
      }
    }
    return paramArrayOfByte;
  }
  
  public byte[] getByteData(int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte)
  {
    if ((paramInt1 < minX) || (paramInt2 < minY) || (paramInt1 + paramInt3 > maxX) || (paramInt2 + paramInt4 > maxY)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    if (paramArrayOfByte == null) {
      paramArrayOfByte = new byte[numDataElements * scanlineStride * paramInt4];
    }
    int i = (paramInt2 - minY) * scanlineStride + (paramInt1 - minX);
    for (int j = 0; j < numDataElements; j++)
    {
      int k = j;
      byte[] arrayOfByte = data[j];
      int m = dataOffsets[j];
      int n = i;
      int i1 = 0;
      while (i1 < paramInt4)
      {
        int i2 = m + n;
        for (int i3 = 0; i3 < paramInt3; i3++)
        {
          paramArrayOfByte[k] = arrayOfByte[(i2++)];
          k += numDataElements;
        }
        i1++;
        n += scanlineStride;
      }
    }
    return paramArrayOfByte;
  }
  
  public void setDataElements(int paramInt1, int paramInt2, Object paramObject)
  {
    if ((paramInt1 < minX) || (paramInt2 < minY) || (paramInt1 >= maxX) || (paramInt2 >= maxY)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    byte[] arrayOfByte = (byte[])paramObject;
    int i = (paramInt2 - minY) * scanlineStride + (paramInt1 - minX);
    for (int j = 0; j < numDataElements; j++) {
      data[j][(dataOffsets[j] + i)] = arrayOfByte[j];
    }
    markDirty();
  }
  
  public void setDataElements(int paramInt1, int paramInt2, Raster paramRaster)
  {
    int i = paramRaster.getMinX() + paramInt1;
    int j = paramRaster.getMinY() + paramInt2;
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
    byte[] arrayOfByte1 = (byte[])paramObject;
    int i = (paramInt2 - minY) * scanlineStride + (paramInt1 - minX);
    for (int j = 0; j < numDataElements; j++)
    {
      int k = j;
      byte[] arrayOfByte2 = data[j];
      int m = dataOffsets[j];
      int n = i;
      int i1 = 0;
      while (i1 < paramInt4)
      {
        int i2 = m + n;
        for (int i3 = 0; i3 < paramInt3; i3++)
        {
          arrayOfByte2[(i2++)] = arrayOfByte1[k];
          k += numDataElements;
        }
        i1++;
        n += scanlineStride;
      }
    }
    markDirty();
  }
  
  public void putByteData(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, byte[] paramArrayOfByte)
  {
    if ((paramInt1 < minX) || (paramInt2 < minY) || (paramInt1 + paramInt3 > maxX) || (paramInt2 + paramInt4 > maxY)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int i = (paramInt2 - minY) * scanlineStride + (paramInt1 - minX) + dataOffsets[paramInt5];
    int j = 0;
    if (scanlineStride == paramInt3)
    {
      System.arraycopy(paramArrayOfByte, 0, data[paramInt5], i, paramInt3 * paramInt4);
    }
    else
    {
      int k = 0;
      while (k < paramInt4)
      {
        System.arraycopy(paramArrayOfByte, j, data[paramInt5], i, paramInt3);
        j += paramInt3;
        k++;
        i += scanlineStride;
      }
    }
    markDirty();
  }
  
  public void putByteData(int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte)
  {
    if ((paramInt1 < minX) || (paramInt2 < minY) || (paramInt1 + paramInt3 > maxX) || (paramInt2 + paramInt4 > maxY)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int i = (paramInt2 - minY) * scanlineStride + (paramInt1 - minX);
    for (int j = 0; j < numDataElements; j++)
    {
      int k = j;
      byte[] arrayOfByte = data[j];
      int m = dataOffsets[j];
      int n = i;
      int i1 = 0;
      while (i1 < paramInt4)
      {
        int i2 = m + n;
        for (int i3 = 0; i3 < paramInt3; i3++)
        {
          arrayOfByte[(i2++)] = paramArrayOfByte[k];
          k += numDataElements;
        }
        i1++;
        n += scanlineStride;
      }
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
    if ((paramInt1 + paramInt3 < paramInt1) || (paramInt1 + paramInt3 > width + minX)) {
      throw new RasterFormatException("(x + width) is outside raster");
    }
    if ((paramInt2 + paramInt4 < paramInt2) || (paramInt2 + paramInt4 > height + minY)) {
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
    return new ByteBandedRaster(localSampleModel, dataBuffer, new Rectangle(paramInt5, paramInt6, paramInt3, paramInt4), new Point(sampleModelTranslateX + i, sampleModelTranslateY + j), this);
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
    return new ByteBandedRaster(localSampleModel, new Point(0, 0));
  }
  
  public WritableRaster createCompatibleWritableRaster()
  {
    return createCompatibleWritableRaster(width, height);
  }
  
  private void verify()
  {
    if ((width <= 0) || (height <= 0) || (height > Integer.MAX_VALUE / width)) {
      throw new RasterFormatException("Invalid raster dimension");
    }
    if ((scanlineStride < 0) || (scanlineStride > Integer.MAX_VALUE / height)) {
      throw new RasterFormatException("Incorrect scanline stride: " + scanlineStride);
    }
    if ((minX - sampleModelTranslateX < 0L) || (minY - sampleModelTranslateY < 0L)) {
      throw new RasterFormatException("Incorrect origin/translate: (" + minX + ", " + minY + ") / (" + sampleModelTranslateX + ", " + sampleModelTranslateY + ")");
    }
    if ((height > 1) || (minY - sampleModelTranslateY > 0)) {
      for (i = 0; i < data.length; i++) {
        if (scanlineStride > data[i].length) {
          throw new RasterFormatException("Incorrect scanline stride: " + scanlineStride);
        }
      }
    }
    for (int i = 0; i < dataOffsets.length; i++) {
      if (dataOffsets[i] < 0) {
        throw new RasterFormatException("Data offsets for band " + i + "(" + dataOffsets[i] + ") must be >= 0");
      }
    }
    i = (height - 1) * scanlineStride;
    if (width - 1 > Integer.MAX_VALUE - i) {
      throw new RasterFormatException("Invalid raster dimension");
    }
    int j = i + (width - 1);
    int k = 0;
    for (int n = 0; n < numDataElements; n++)
    {
      if (dataOffsets[n] > Integer.MAX_VALUE - j) {
        throw new RasterFormatException("Invalid raster dimension");
      }
      int m = j + dataOffsets[n];
      if (m > k) {
        k = m;
      }
    }
    if (data.length == 1)
    {
      if (data[0].length <= k * numDataElements) {
        throw new RasterFormatException("Data array too small (it is " + data[0].length + " and should be > " + k * numDataElements + " )");
      }
    }
    else {
      for (n = 0; n < numDataElements; n++) {
        if (data[n].length <= k) {
          throw new RasterFormatException("Data array too small (it is " + data[n].length + " and should be > " + k + " )");
        }
      }
    }
  }
  
  public String toString()
  {
    return new String("ByteBandedRaster: width = " + width + " height = " + height + " #bands " + numDataElements + " minX = " + minX + " minY = " + minY);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\image\ByteBandedRaster.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */