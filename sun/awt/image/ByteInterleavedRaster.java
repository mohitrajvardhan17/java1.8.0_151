package sun.awt.image;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RasterFormatException;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;

public class ByteInterleavedRaster
  extends ByteComponentRaster
{
  boolean inOrder;
  int dbOffset;
  int dbOffsetPacked;
  boolean packed = false;
  int[] bitMasks;
  int[] bitOffsets;
  private int maxX = minX + width;
  private int maxY = minY + height;
  
  public ByteInterleavedRaster(SampleModel paramSampleModel, Point paramPoint)
  {
    this(paramSampleModel, paramSampleModel.createDataBuffer(), new Rectangle(x, y, paramSampleModel.getWidth(), paramSampleModel.getHeight()), paramPoint, null);
  }
  
  public ByteInterleavedRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Point paramPoint)
  {
    this(paramSampleModel, paramDataBuffer, new Rectangle(x, y, paramSampleModel.getWidth(), paramSampleModel.getHeight()), paramPoint, null);
  }
  
  private boolean isInterleaved(ComponentSampleModel paramComponentSampleModel)
  {
    int i = sampleModel.getNumBands();
    if (i == 1) {
      return true;
    }
    int[] arrayOfInt1 = paramComponentSampleModel.getBankIndices();
    for (int j = 0; j < i; j++) {
      if (arrayOfInt1[j] != 0) {
        return false;
      }
    }
    int[] arrayOfInt2 = paramComponentSampleModel.getBandOffsets();
    int k = arrayOfInt2[0];
    int m = k;
    for (int n = 1; n < i; n++)
    {
      int i1 = arrayOfInt2[n];
      if (i1 < k) {
        k = i1;
      }
      if (i1 > m) {
        m = i1;
      }
    }
    return m - k < paramComponentSampleModel.getPixelStride();
  }
  
  public ByteInterleavedRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Rectangle paramRectangle, Point paramPoint, ByteInterleavedRaster paramByteInterleavedRaster)
  {
    super(paramSampleModel, paramDataBuffer, paramRectangle, paramPoint, paramByteInterleavedRaster);
    if (!(paramDataBuffer instanceof DataBufferByte)) {
      throw new RasterFormatException("ByteInterleavedRasters must have byte DataBuffers");
    }
    DataBufferByte localDataBufferByte = (DataBufferByte)paramDataBuffer;
    data = stealData(localDataBufferByte, 0);
    int i = x - x;
    int j = y - y;
    Object localObject;
    if (((paramSampleModel instanceof PixelInterleavedSampleModel)) || (((paramSampleModel instanceof ComponentSampleModel)) && (isInterleaved((ComponentSampleModel)paramSampleModel))))
    {
      localObject = (ComponentSampleModel)paramSampleModel;
      scanlineStride = ((ComponentSampleModel)localObject).getScanlineStride();
      pixelStride = ((ComponentSampleModel)localObject).getPixelStride();
      dataOffsets = ((ComponentSampleModel)localObject).getBandOffsets();
      for (int m = 0; m < getNumDataElements(); m++) {
        dataOffsets[m] += i * pixelStride + j * scanlineStride;
      }
    }
    else if ((paramSampleModel instanceof SinglePixelPackedSampleModel))
    {
      localObject = (SinglePixelPackedSampleModel)paramSampleModel;
      packed = true;
      bitMasks = ((SinglePixelPackedSampleModel)localObject).getBitMasks();
      bitOffsets = ((SinglePixelPackedSampleModel)localObject).getBitOffsets();
      scanlineStride = ((SinglePixelPackedSampleModel)localObject).getScanlineStride();
      pixelStride = 1;
      dataOffsets = new int[1];
      dataOffsets[0] = localDataBufferByte.getOffset();
      dataOffsets[0] += i * pixelStride + j * scanlineStride;
    }
    else
    {
      throw new RasterFormatException("ByteInterleavedRasters must have PixelInterleavedSampleModel, SinglePixelPackedSampleModel or interleaved ComponentSampleModel.  Sample model is " + paramSampleModel);
    }
    bandOffset = dataOffsets[0];
    dbOffsetPacked = (paramDataBuffer.getOffset() - sampleModelTranslateY * scanlineStride - sampleModelTranslateX * pixelStride);
    dbOffset = (dbOffsetPacked - (i * pixelStride + j * scanlineStride));
    inOrder = false;
    if (numDataElements == pixelStride)
    {
      inOrder = true;
      for (int k = 1; k < numDataElements; k++) {
        if (dataOffsets[k] - dataOffsets[0] != k)
        {
          inOrder = false;
          break;
        }
      }
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
  
  public byte[] getDataStorage()
  {
    return data;
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
    int i = (paramInt2 - minY) * scanlineStride + (paramInt1 - minX) * pixelStride;
    for (int j = 0; j < numDataElements; j++) {
      arrayOfByte[j] = data[(dataOffsets[j] + i)];
    }
    return arrayOfByte;
  }
  
  public Object getDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject)
  {
    return getByteData(paramInt1, paramInt2, paramInt3, paramInt4, (byte[])paramObject);
  }
  
  public byte[] getByteData(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, byte[] paramArrayOfByte)
  {
    if ((paramInt1 < minX) || (paramInt2 < minY) || (paramInt1 + paramInt3 > maxX) || (paramInt2 + paramInt4 > maxY)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    if (paramArrayOfByte == null) {
      paramArrayOfByte = new byte[paramInt3 * paramInt4];
    }
    int i = (paramInt2 - minY) * scanlineStride + (paramInt1 - minX) * pixelStride + dataOffsets[paramInt5];
    int k = 0;
    int n;
    if (pixelStride == 1)
    {
      if (scanlineStride == paramInt3)
      {
        System.arraycopy(data, i, paramArrayOfByte, 0, paramInt3 * paramInt4);
      }
      else
      {
        n = 0;
        while (n < paramInt4)
        {
          System.arraycopy(data, i, paramArrayOfByte, k, paramInt3);
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
          paramArrayOfByte[(k++)] = data[j];
          m++;
          j += pixelStride;
        }
        n++;
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
      paramArrayOfByte = new byte[numDataElements * paramInt3 * paramInt4];
    }
    int i = (paramInt2 - minY) * scanlineStride + (paramInt1 - minX) * pixelStride;
    int k = 0;
    int i1;
    int n;
    if (inOrder)
    {
      i += dataOffsets[0];
      i1 = paramInt3 * pixelStride;
      if (scanlineStride == i1)
      {
        System.arraycopy(data, i, paramArrayOfByte, k, i1 * paramInt4);
      }
      else
      {
        n = 0;
        while (n < paramInt4)
        {
          System.arraycopy(data, i, paramArrayOfByte, k, i1);
          k += i1;
          n++;
          i += scanlineStride;
        }
      }
    }
    else
    {
      int j;
      int m;
      if (numDataElements == 1)
      {
        i += dataOffsets[0];
        n = 0;
        while (n < paramInt4)
        {
          j = i;
          m = 0;
          while (m < paramInt3)
          {
            paramArrayOfByte[(k++)] = data[j];
            m++;
            j += pixelStride;
          }
          n++;
          i += scanlineStride;
        }
      }
      if (numDataElements == 2)
      {
        i += dataOffsets[0];
        i1 = dataOffsets[1] - dataOffsets[0];
        n = 0;
        while (n < paramInt4)
        {
          j = i;
          m = 0;
          while (m < paramInt3)
          {
            paramArrayOfByte[(k++)] = data[j];
            paramArrayOfByte[(k++)] = data[(j + i1)];
            m++;
            j += pixelStride;
          }
          n++;
          i += scanlineStride;
        }
      }
      else
      {
        int i2;
        if (numDataElements == 3)
        {
          i += dataOffsets[0];
          i1 = dataOffsets[1] - dataOffsets[0];
          i2 = dataOffsets[2] - dataOffsets[0];
          n = 0;
          while (n < paramInt4)
          {
            j = i;
            m = 0;
            while (m < paramInt3)
            {
              paramArrayOfByte[(k++)] = data[j];
              paramArrayOfByte[(k++)] = data[(j + i1)];
              paramArrayOfByte[(k++)] = data[(j + i2)];
              m++;
              j += pixelStride;
            }
            n++;
            i += scanlineStride;
          }
        }
        else if (numDataElements == 4)
        {
          i += dataOffsets[0];
          i1 = dataOffsets[1] - dataOffsets[0];
          i2 = dataOffsets[2] - dataOffsets[0];
          int i3 = dataOffsets[3] - dataOffsets[0];
          n = 0;
          while (n < paramInt4)
          {
            j = i;
            m = 0;
            while (m < paramInt3)
            {
              paramArrayOfByte[(k++)] = data[j];
              paramArrayOfByte[(k++)] = data[(j + i1)];
              paramArrayOfByte[(k++)] = data[(j + i2)];
              paramArrayOfByte[(k++)] = data[(j + i3)];
              m++;
              j += pixelStride;
            }
            n++;
            i += scanlineStride;
          }
        }
        else
        {
          n = 0;
          while (n < paramInt4)
          {
            j = i;
            m = 0;
            while (m < paramInt3)
            {
              for (i1 = 0; i1 < numDataElements; i1++) {
                paramArrayOfByte[(k++)] = data[(dataOffsets[i1] + j)];
              }
              m++;
              j += pixelStride;
            }
            n++;
            i += scanlineStride;
          }
        }
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
    int i = (paramInt2 - minY) * scanlineStride + (paramInt1 - minX) * pixelStride;
    for (int j = 0; j < numDataElements; j++) {
      data[(dataOffsets[j] + i)] = arrayOfByte[j];
    }
    markDirty();
  }
  
  public void setDataElements(int paramInt1, int paramInt2, Raster paramRaster)
  {
    int i = paramRaster.getMinX();
    int j = paramRaster.getMinY();
    int k = paramInt1 + i;
    int m = paramInt2 + j;
    int n = paramRaster.getWidth();
    int i1 = paramRaster.getHeight();
    if ((k < minX) || (m < minY) || (k + n > maxX) || (m + i1 > maxY)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    setDataElements(k, m, i, j, n, i1, paramRaster);
  }
  
  private void setDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, Raster paramRaster)
  {
    if ((paramInt5 <= 0) || (paramInt6 <= 0)) {
      return;
    }
    int i = paramRaster.getMinX();
    int j = paramRaster.getMinY();
    Object localObject = null;
    if ((paramRaster instanceof ByteInterleavedRaster))
    {
      ByteInterleavedRaster localByteInterleavedRaster = (ByteInterleavedRaster)paramRaster;
      byte[] arrayOfByte = localByteInterleavedRaster.getDataStorage();
      if ((inOrder) && (inOrder) && (pixelStride == pixelStride))
      {
        int m = localByteInterleavedRaster.getDataOffset(0);
        int n = localByteInterleavedRaster.getScanlineStride();
        int i1 = localByteInterleavedRaster.getPixelStride();
        int i2 = m + (paramInt4 - j) * n + (paramInt3 - i) * i1;
        int i3 = dataOffsets[0] + (paramInt2 - minY) * scanlineStride + (paramInt1 - minX) * pixelStride;
        int i4 = paramInt5 * pixelStride;
        for (int i5 = 0; i5 < paramInt6; i5++)
        {
          System.arraycopy(arrayOfByte, i2, data, i3, i4);
          i2 += n;
          i3 += scanlineStride;
        }
        markDirty();
        return;
      }
    }
    for (int k = 0; k < paramInt6; k++)
    {
      localObject = paramRaster.getDataElements(i, j + k, paramInt5, 1, localObject);
      setDataElements(paramInt1, paramInt2 + k, paramInt5, 1, localObject);
    }
  }
  
  public void setDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject)
  {
    putByteData(paramInt1, paramInt2, paramInt3, paramInt4, (byte[])paramObject);
  }
  
  public void putByteData(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, byte[] paramArrayOfByte)
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
        System.arraycopy(paramArrayOfByte, 0, data, i, paramInt3 * paramInt4);
      }
      else
      {
        n = 0;
        while (n < paramInt4)
        {
          System.arraycopy(paramArrayOfByte, k, data, i, paramInt3);
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
          data[j] = paramArrayOfByte[(k++)];
          m++;
          j += pixelStride;
        }
        n++;
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
    int i = (paramInt2 - minY) * scanlineStride + (paramInt1 - minX) * pixelStride;
    int k = 0;
    int i1;
    int n;
    if (inOrder)
    {
      i += dataOffsets[0];
      i1 = paramInt3 * pixelStride;
      if (i1 == scanlineStride)
      {
        System.arraycopy(paramArrayOfByte, 0, data, i, i1 * paramInt4);
      }
      else
      {
        n = 0;
        while (n < paramInt4)
        {
          System.arraycopy(paramArrayOfByte, k, data, i, i1);
          k += i1;
          n++;
          i += scanlineStride;
        }
      }
    }
    else
    {
      int j;
      int m;
      if (numDataElements == 1)
      {
        i += dataOffsets[0];
        n = 0;
        while (n < paramInt4)
        {
          j = i;
          m = 0;
          while (m < paramInt3)
          {
            data[j] = paramArrayOfByte[(k++)];
            m++;
            j += pixelStride;
          }
          n++;
          i += scanlineStride;
        }
      }
      if (numDataElements == 2)
      {
        i += dataOffsets[0];
        i1 = dataOffsets[1] - dataOffsets[0];
        n = 0;
        while (n < paramInt4)
        {
          j = i;
          m = 0;
          while (m < paramInt3)
          {
            data[j] = paramArrayOfByte[(k++)];
            data[(j + i1)] = paramArrayOfByte[(k++)];
            m++;
            j += pixelStride;
          }
          n++;
          i += scanlineStride;
        }
      }
      else
      {
        int i2;
        if (numDataElements == 3)
        {
          i += dataOffsets[0];
          i1 = dataOffsets[1] - dataOffsets[0];
          i2 = dataOffsets[2] - dataOffsets[0];
          n = 0;
          while (n < paramInt4)
          {
            j = i;
            m = 0;
            while (m < paramInt3)
            {
              data[j] = paramArrayOfByte[(k++)];
              data[(j + i1)] = paramArrayOfByte[(k++)];
              data[(j + i2)] = paramArrayOfByte[(k++)];
              m++;
              j += pixelStride;
            }
            n++;
            i += scanlineStride;
          }
        }
        else if (numDataElements == 4)
        {
          i += dataOffsets[0];
          i1 = dataOffsets[1] - dataOffsets[0];
          i2 = dataOffsets[2] - dataOffsets[0];
          int i3 = dataOffsets[3] - dataOffsets[0];
          n = 0;
          while (n < paramInt4)
          {
            j = i;
            m = 0;
            while (m < paramInt3)
            {
              data[j] = paramArrayOfByte[(k++)];
              data[(j + i1)] = paramArrayOfByte[(k++)];
              data[(j + i2)] = paramArrayOfByte[(k++)];
              data[(j + i3)] = paramArrayOfByte[(k++)];
              m++;
              j += pixelStride;
            }
            n++;
            i += scanlineStride;
          }
        }
        else
        {
          n = 0;
          while (n < paramInt4)
          {
            j = i;
            m = 0;
            while (m < paramInt3)
            {
              for (i1 = 0; i1 < numDataElements; i1++) {
                data[(dataOffsets[i1] + j)] = paramArrayOfByte[(k++)];
              }
              m++;
              j += pixelStride;
            }
            n++;
            i += scanlineStride;
          }
        }
      }
    }
    markDirty();
  }
  
  public int getSample(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramInt1 < minX) || (paramInt2 < minY) || (paramInt1 >= maxX) || (paramInt2 >= maxY)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    if (packed)
    {
      i = paramInt2 * scanlineStride + paramInt1 + dbOffsetPacked;
      int j = data[i];
      return (j & bitMasks[paramInt3]) >>> bitOffsets[paramInt3];
    }
    int i = paramInt2 * scanlineStride + paramInt1 * pixelStride + dbOffset;
    return data[(i + dataOffsets[paramInt3])] & 0xFF;
  }
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((paramInt1 < minX) || (paramInt2 < minY) || (paramInt1 >= maxX) || (paramInt2 >= maxY)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int i;
    if (packed)
    {
      i = paramInt2 * scanlineStride + paramInt1 + dbOffsetPacked;
      int j = bitMasks[paramInt3];
      int k = data[i];
      k = (byte)(k & (j ^ 0xFFFFFFFF));
      k = (byte)(k | paramInt4 << bitOffsets[paramInt3] & j);
      data[i] = k;
    }
    else
    {
      i = paramInt2 * scanlineStride + paramInt1 * pixelStride + dbOffset;
      data[(i + dataOffsets[paramInt3])] = ((byte)paramInt4);
    }
    markDirty();
  }
  
  public int[] getSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfInt)
  {
    if ((paramInt1 < minX) || (paramInt2 < minY) || (paramInt1 + paramInt3 > maxX) || (paramInt2 + paramInt4 > maxY)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int[] arrayOfInt;
    if (paramArrayOfInt != null) {
      arrayOfInt = paramArrayOfInt;
    } else {
      arrayOfInt = new int[paramInt3 * paramInt4];
    }
    int i = paramInt2 * scanlineStride + paramInt1 * pixelStride;
    int j = 0;
    int k;
    int m;
    int n;
    if (packed)
    {
      i += dbOffsetPacked;
      k = bitMasks[paramInt5];
      m = bitOffsets[paramInt5];
      for (n = 0; n < paramInt4; n++)
      {
        int i1 = i;
        for (int i2 = 0; i2 < paramInt3; i2++)
        {
          int i3 = data[(i1++)];
          arrayOfInt[(j++)] = ((i3 & k) >>> m);
        }
        i += scanlineStride;
      }
    }
    else
    {
      i += dbOffset + dataOffsets[paramInt5];
      for (k = 0; k < paramInt4; k++)
      {
        m = i;
        for (n = 0; n < paramInt3; n++)
        {
          arrayOfInt[(j++)] = (data[m] & 0xFF);
          m += pixelStride;
        }
        i += scanlineStride;
      }
    }
    return arrayOfInt;
  }
  
  public void setSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfInt)
  {
    if ((paramInt1 < minX) || (paramInt2 < minY) || (paramInt1 + paramInt3 > maxX) || (paramInt2 + paramInt4 > maxY)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int i = paramInt2 * scanlineStride + paramInt1 * pixelStride;
    int j = 0;
    int k;
    int m;
    int n;
    if (packed)
    {
      i += dbOffsetPacked;
      k = bitMasks[paramInt5];
      for (m = 0; m < paramInt4; m++)
      {
        n = i;
        for (int i1 = 0; i1 < paramInt3; i1++)
        {
          int i2 = data[n];
          i2 = (byte)(i2 & (k ^ 0xFFFFFFFF));
          int i3 = paramArrayOfInt[(j++)];
          i2 = (byte)(i2 | i3 << bitOffsets[paramInt5] & k);
          data[(n++)] = i2;
        }
        i += scanlineStride;
      }
    }
    else
    {
      i += dbOffset + dataOffsets[paramInt5];
      for (k = 0; k < paramInt4; k++)
      {
        m = i;
        for (n = 0; n < paramInt3; n++)
        {
          data[m] = ((byte)paramArrayOfInt[(j++)]);
          m += pixelStride;
        }
        i += scanlineStride;
      }
    }
    markDirty();
  }
  
  public int[] getPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt)
  {
    if ((paramInt1 < minX) || (paramInt2 < minY) || (paramInt1 + paramInt3 > maxX) || (paramInt2 + paramInt4 > maxY)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int[] arrayOfInt;
    if (paramArrayOfInt != null) {
      arrayOfInt = paramArrayOfInt;
    } else {
      arrayOfInt = new int[paramInt3 * paramInt4 * numBands];
    }
    int i = paramInt2 * scanlineStride + paramInt1 * pixelStride;
    int j = 0;
    int k;
    int m;
    int n;
    int i1;
    if (packed)
    {
      i += dbOffsetPacked;
      for (k = 0; k < paramInt4; k++)
      {
        for (m = 0; m < paramInt3; m++)
        {
          n = data[(i + m)];
          for (i1 = 0; i1 < numBands; i1++) {
            arrayOfInt[(j++)] = ((n & bitMasks[i1]) >>> bitOffsets[i1]);
          }
        }
        i += scanlineStride;
      }
    }
    else
    {
      i += dbOffset;
      k = dataOffsets[0];
      if (numBands == 1)
      {
        for (m = 0; m < paramInt4; m++)
        {
          n = i + k;
          for (i1 = 0; i1 < paramInt3; i1++)
          {
            arrayOfInt[(j++)] = (data[n] & 0xFF);
            n += pixelStride;
          }
          i += scanlineStride;
        }
      }
      else
      {
        int i2;
        if (numBands == 2)
        {
          m = dataOffsets[1] - k;
          for (n = 0; n < paramInt4; n++)
          {
            i1 = i + k;
            for (i2 = 0; i2 < paramInt3; i2++)
            {
              arrayOfInt[(j++)] = (data[i1] & 0xFF);
              arrayOfInt[(j++)] = (data[(i1 + m)] & 0xFF);
              i1 += pixelStride;
            }
            i += scanlineStride;
          }
        }
        else
        {
          int i3;
          if (numBands == 3)
          {
            m = dataOffsets[1] - k;
            n = dataOffsets[2] - k;
            for (i1 = 0; i1 < paramInt4; i1++)
            {
              i2 = i + k;
              for (i3 = 0; i3 < paramInt3; i3++)
              {
                arrayOfInt[(j++)] = (data[i2] & 0xFF);
                arrayOfInt[(j++)] = (data[(i2 + m)] & 0xFF);
                arrayOfInt[(j++)] = (data[(i2 + n)] & 0xFF);
                i2 += pixelStride;
              }
              i += scanlineStride;
            }
          }
          else if (numBands == 4)
          {
            m = dataOffsets[1] - k;
            n = dataOffsets[2] - k;
            i1 = dataOffsets[3] - k;
            for (i2 = 0; i2 < paramInt4; i2++)
            {
              i3 = i + k;
              for (int i4 = 0; i4 < paramInt3; i4++)
              {
                arrayOfInt[(j++)] = (data[i3] & 0xFF);
                arrayOfInt[(j++)] = (data[(i3 + m)] & 0xFF);
                arrayOfInt[(j++)] = (data[(i3 + n)] & 0xFF);
                arrayOfInt[(j++)] = (data[(i3 + i1)] & 0xFF);
                i3 += pixelStride;
              }
              i += scanlineStride;
            }
          }
          else
          {
            for (m = 0; m < paramInt4; m++)
            {
              n = i;
              for (i1 = 0; i1 < paramInt3; i1++)
              {
                for (i2 = 0; i2 < numBands; i2++) {
                  arrayOfInt[(j++)] = (data[(n + dataOffsets[i2])] & 0xFF);
                }
                n += pixelStride;
              }
              i += scanlineStride;
            }
          }
        }
      }
    }
    return arrayOfInt;
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt)
  {
    if ((paramInt1 < minX) || (paramInt2 < minY) || (paramInt1 + paramInt3 > maxX) || (paramInt2 + paramInt4 > maxY)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int i = paramInt2 * scanlineStride + paramInt1 * pixelStride;
    int j = 0;
    int k;
    int m;
    int n;
    int i1;
    int i2;
    if (packed)
    {
      i += dbOffsetPacked;
      for (k = 0; k < paramInt4; k++)
      {
        for (m = 0; m < paramInt3; m++)
        {
          n = 0;
          for (i1 = 0; i1 < numBands; i1++)
          {
            i2 = paramArrayOfInt[(j++)];
            n |= i2 << bitOffsets[i1] & bitMasks[i1];
          }
          data[(i + m)] = ((byte)n);
        }
        i += scanlineStride;
      }
    }
    else
    {
      i += dbOffset;
      k = dataOffsets[0];
      if (numBands == 1)
      {
        for (m = 0; m < paramInt4; m++)
        {
          n = i + k;
          for (i1 = 0; i1 < paramInt3; i1++)
          {
            data[n] = ((byte)paramArrayOfInt[(j++)]);
            n += pixelStride;
          }
          i += scanlineStride;
        }
      }
      else if (numBands == 2)
      {
        m = dataOffsets[1] - k;
        for (n = 0; n < paramInt4; n++)
        {
          i1 = i + k;
          for (i2 = 0; i2 < paramInt3; i2++)
          {
            data[i1] = ((byte)paramArrayOfInt[(j++)]);
            data[(i1 + m)] = ((byte)paramArrayOfInt[(j++)]);
            i1 += pixelStride;
          }
          i += scanlineStride;
        }
      }
      else
      {
        int i3;
        if (numBands == 3)
        {
          m = dataOffsets[1] - k;
          n = dataOffsets[2] - k;
          for (i1 = 0; i1 < paramInt4; i1++)
          {
            i2 = i + k;
            for (i3 = 0; i3 < paramInt3; i3++)
            {
              data[i2] = ((byte)paramArrayOfInt[(j++)]);
              data[(i2 + m)] = ((byte)paramArrayOfInt[(j++)]);
              data[(i2 + n)] = ((byte)paramArrayOfInt[(j++)]);
              i2 += pixelStride;
            }
            i += scanlineStride;
          }
        }
        else if (numBands == 4)
        {
          m = dataOffsets[1] - k;
          n = dataOffsets[2] - k;
          i1 = dataOffsets[3] - k;
          for (i2 = 0; i2 < paramInt4; i2++)
          {
            i3 = i + k;
            for (int i4 = 0; i4 < paramInt3; i4++)
            {
              data[i3] = ((byte)paramArrayOfInt[(j++)]);
              data[(i3 + m)] = ((byte)paramArrayOfInt[(j++)]);
              data[(i3 + n)] = ((byte)paramArrayOfInt[(j++)]);
              data[(i3 + i1)] = ((byte)paramArrayOfInt[(j++)]);
              i3 += pixelStride;
            }
            i += scanlineStride;
          }
        }
        else
        {
          for (m = 0; m < paramInt4; m++)
          {
            n = i;
            for (i1 = 0; i1 < paramInt3; i1++)
            {
              for (i2 = 0; i2 < numBands; i2++) {
                data[(n + dataOffsets[i2])] = ((byte)paramArrayOfInt[(j++)]);
              }
              n += pixelStride;
            }
            i += scanlineStride;
          }
        }
      }
    }
    markDirty();
  }
  
  public void setRect(int paramInt1, int paramInt2, Raster paramRaster)
  {
    if (!(paramRaster instanceof ByteInterleavedRaster))
    {
      super.setRect(paramInt1, paramInt2, paramRaster);
      return;
    }
    int i = paramRaster.getWidth();
    int j = paramRaster.getHeight();
    int k = paramRaster.getMinX();
    int m = paramRaster.getMinY();
    int n = paramInt1 + k;
    int i1 = paramInt2 + m;
    int i2;
    if (n < minX)
    {
      i2 = minX - n;
      i -= i2;
      k += i2;
      n = minX;
    }
    if (i1 < minY)
    {
      i2 = minY - i1;
      j -= i2;
      m += i2;
      i1 = minY;
    }
    if (n + i > maxX) {
      i = maxX - n;
    }
    if (i1 + j > maxY) {
      j = maxY - i1;
    }
    setDataElements(n, i1, k, m, i, j, paramRaster);
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
    return new ByteInterleavedRaster(localSampleModel, dataBuffer, new Rectangle(paramInt5, paramInt6, paramInt3, paramInt4), new Point(sampleModelTranslateX + i, sampleModelTranslateY + j), this);
  }
  
  public WritableRaster createCompatibleWritableRaster(int paramInt1, int paramInt2)
  {
    if ((paramInt1 <= 0) || (paramInt2 <= 0)) {
      throw new RasterFormatException("negative " + (paramInt1 <= 0 ? "width" : "height"));
    }
    SampleModel localSampleModel = sampleModel.createCompatibleSampleModel(paramInt1, paramInt2);
    return new ByteInterleavedRaster(localSampleModel, new Point(0, 0));
  }
  
  public WritableRaster createCompatibleWritableRaster()
  {
    return createCompatibleWritableRaster(width, height);
  }
  
  public String toString()
  {
    return new String("ByteInterleavedRaster: width = " + width + " height = " + height + " #numDataElements " + numDataElements + " dataOff[0] = " + dataOffsets[0]);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\image\ByteInterleavedRaster.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */