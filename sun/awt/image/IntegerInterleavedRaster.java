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

public class IntegerInterleavedRaster
  extends IntegerComponentRaster
{
  private int maxX = minX + width;
  private int maxY = minY + height;
  
  public IntegerInterleavedRaster(SampleModel paramSampleModel, Point paramPoint)
  {
    this(paramSampleModel, paramSampleModel.createDataBuffer(), new Rectangle(x, y, paramSampleModel.getWidth(), paramSampleModel.getHeight()), paramPoint, null);
  }
  
  public IntegerInterleavedRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Point paramPoint)
  {
    this(paramSampleModel, paramDataBuffer, new Rectangle(x, y, paramSampleModel.getWidth(), paramSampleModel.getHeight()), paramPoint, null);
  }
  
  public IntegerInterleavedRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Rectangle paramRectangle, Point paramPoint, IntegerInterleavedRaster paramIntegerInterleavedRaster)
  {
    super(paramSampleModel, paramDataBuffer, paramRectangle, paramPoint, paramIntegerInterleavedRaster);
    if (!(paramDataBuffer instanceof DataBufferInt)) {
      throw new RasterFormatException("IntegerInterleavedRasters must haveinteger DataBuffers");
    }
    DataBufferInt localDataBufferInt = (DataBufferInt)paramDataBuffer;
    data = stealData(localDataBufferInt, 0);
    if ((paramSampleModel instanceof SinglePixelPackedSampleModel))
    {
      SinglePixelPackedSampleModel localSinglePixelPackedSampleModel = (SinglePixelPackedSampleModel)paramSampleModel;
      scanlineStride = localSinglePixelPackedSampleModel.getScanlineStride();
      pixelStride = 1;
      dataOffsets = new int[1];
      dataOffsets[0] = localDataBufferInt.getOffset();
      bandOffset = dataOffsets[0];
      int i = x - x;
      int j = y - y;
      dataOffsets[0] += i + j * scanlineStride;
      numDataElems = localSinglePixelPackedSampleModel.getNumDataElements();
    }
    else
    {
      throw new RasterFormatException("IntegerInterleavedRasters must have SinglePixelPackedSampleModel");
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
      arrayOfInt = new int[1];
    } else {
      arrayOfInt = (int[])paramObject;
    }
    int i = (paramInt2 - minY) * scanlineStride + (paramInt1 - minX) + dataOffsets[0];
    arrayOfInt[0] = data[i];
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
      arrayOfInt = new int[paramInt3 * paramInt4];
    }
    int i = (paramInt2 - minY) * scanlineStride + (paramInt1 - minX) + dataOffsets[0];
    int j = 0;
    for (int k = 0; k < paramInt4; k++)
    {
      System.arraycopy(data, i, arrayOfInt, j, paramInt3);
      j += paramInt3;
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
    int i = (paramInt2 - minY) * scanlineStride + (paramInt1 - minX) + dataOffsets[0];
    data[i] = arrayOfInt[0];
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
    if ((paramRaster instanceof IntegerInterleavedRaster))
    {
      localObject = (IntegerInterleavedRaster)paramRaster;
      arrayOfInt = ((IntegerInterleavedRaster)localObject).getDataStorage();
      k = ((IntegerInterleavedRaster)localObject).getScanlineStride();
      int m = ((IntegerInterleavedRaster)localObject).getDataOffset(0);
      int n = m;
      int i1 = dataOffsets[0] + (paramInt2 - minY) * scanlineStride + (paramInt1 - minX);
      for (int i2 = 0; i2 < paramInt4; i2++)
      {
        System.arraycopy(arrayOfInt, n, data, i1, paramInt3);
        n += k;
        i1 += scanlineStride;
      }
      markDirty();
      return;
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
    int i = (paramInt2 - minY) * scanlineStride + (paramInt1 - minX) + dataOffsets[0];
    int j = 0;
    for (int k = 0; k < paramInt4; k++)
    {
      System.arraycopy(arrayOfInt, j, data, i, paramInt3);
      j += paramInt3;
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
    return new IntegerInterleavedRaster(localSampleModel, dataBuffer, new Rectangle(paramInt5, paramInt6, paramInt3, paramInt4), new Point(sampleModelTranslateX + i, sampleModelTranslateY + j), this);
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
    return new IntegerInterleavedRaster(localSampleModel, new Point(0, 0));
  }
  
  public WritableRaster createCompatibleWritableRaster()
  {
    return createCompatibleWritableRaster(width, height);
  }
  
  public String toString()
  {
    return new String("IntegerInterleavedRaster: width = " + width + " height = " + height + " #Bands = " + numBands + " xOff = " + sampleModelTranslateX + " yOff = " + sampleModelTranslateY + " dataOffset[0] " + dataOffsets[0]);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\image\IntegerInterleavedRaster.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */