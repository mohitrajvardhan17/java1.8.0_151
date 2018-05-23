package sun.awt.image;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferUShort;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RasterFormatException;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;

public class ShortInterleavedRaster
  extends ShortComponentRaster
{
  private int maxX = minX + width;
  private int maxY = minY + height;
  
  public ShortInterleavedRaster(SampleModel paramSampleModel, Point paramPoint)
  {
    this(paramSampleModel, paramSampleModel.createDataBuffer(), new Rectangle(x, y, paramSampleModel.getWidth(), paramSampleModel.getHeight()), paramPoint, null);
  }
  
  public ShortInterleavedRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Point paramPoint)
  {
    this(paramSampleModel, paramDataBuffer, new Rectangle(x, y, paramSampleModel.getWidth(), paramSampleModel.getHeight()), paramPoint, null);
  }
  
  public ShortInterleavedRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Rectangle paramRectangle, Point paramPoint, ShortInterleavedRaster paramShortInterleavedRaster)
  {
    super(paramSampleModel, paramDataBuffer, paramRectangle, paramPoint, paramShortInterleavedRaster);
    if (!(paramDataBuffer instanceof DataBufferUShort)) {
      throw new RasterFormatException("ShortInterleavedRasters must have ushort DataBuffers");
    }
    DataBufferUShort localDataBufferUShort = (DataBufferUShort)paramDataBuffer;
    data = stealData(localDataBufferUShort, 0);
    Object localObject;
    int i;
    int j;
    if (((paramSampleModel instanceof PixelInterleavedSampleModel)) || (((paramSampleModel instanceof ComponentSampleModel)) && (paramSampleModel.getNumBands() == 1)))
    {
      localObject = (ComponentSampleModel)paramSampleModel;
      scanlineStride = ((ComponentSampleModel)localObject).getScanlineStride();
      pixelStride = ((ComponentSampleModel)localObject).getPixelStride();
      dataOffsets = ((ComponentSampleModel)localObject).getBandOffsets();
      i = x - x;
      j = y - y;
      for (int k = 0; k < getNumDataElements(); k++) {
        dataOffsets[k] += i * pixelStride + j * scanlineStride;
      }
    }
    else if ((paramSampleModel instanceof SinglePixelPackedSampleModel))
    {
      localObject = (SinglePixelPackedSampleModel)paramSampleModel;
      scanlineStride = ((SinglePixelPackedSampleModel)localObject).getScanlineStride();
      pixelStride = 1;
      dataOffsets = new int[1];
      dataOffsets[0] = localDataBufferUShort.getOffset();
      i = x - x;
      j = y - y;
      dataOffsets[0] += i + j * scanlineStride;
    }
    else
    {
      throw new RasterFormatException("ShortInterleavedRasters must have PixelInterleavedSampleModel, SinglePixelPackedSampleModel or 1 band ComponentSampleModel.  Sample model is " + paramSampleModel);
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
    return new ShortInterleavedRaster(localSampleModel, dataBuffer, new Rectangle(paramInt5, paramInt6, paramInt3, paramInt4), new Point(sampleModelTranslateX + i, sampleModelTranslateY + j), this);
  }
  
  public WritableRaster createCompatibleWritableRaster(int paramInt1, int paramInt2)
  {
    if ((paramInt1 <= 0) || (paramInt2 <= 0)) {
      throw new RasterFormatException("negative " + (paramInt1 <= 0 ? "width" : "height"));
    }
    SampleModel localSampleModel = sampleModel.createCompatibleSampleModel(paramInt1, paramInt2);
    return new ShortInterleavedRaster(localSampleModel, new Point(0, 0));
  }
  
  public WritableRaster createCompatibleWritableRaster()
  {
    return createCompatibleWritableRaster(width, height);
  }
  
  public String toString()
  {
    return new String("ShortInterleavedRaster: width = " + width + " height = " + height + " #numDataElements " + numDataElements);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\image\ShortInterleavedRaster.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */