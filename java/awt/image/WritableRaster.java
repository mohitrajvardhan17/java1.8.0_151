package java.awt.image;

import java.awt.Point;
import java.awt.Rectangle;

public class WritableRaster
  extends Raster
{
  protected WritableRaster(SampleModel paramSampleModel, Point paramPoint)
  {
    this(paramSampleModel, paramSampleModel.createDataBuffer(), new Rectangle(x, y, paramSampleModel.getWidth(), paramSampleModel.getHeight()), paramPoint, null);
  }
  
  protected WritableRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Point paramPoint)
  {
    this(paramSampleModel, paramDataBuffer, new Rectangle(x, y, paramSampleModel.getWidth(), paramSampleModel.getHeight()), paramPoint, null);
  }
  
  protected WritableRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Rectangle paramRectangle, Point paramPoint, WritableRaster paramWritableRaster)
  {
    super(paramSampleModel, paramDataBuffer, paramRectangle, paramPoint, paramWritableRaster);
  }
  
  public WritableRaster getWritableParent()
  {
    return (WritableRaster)parent;
  }
  
  public WritableRaster createWritableTranslatedChild(int paramInt1, int paramInt2)
  {
    return createWritableChild(minX, minY, width, height, paramInt1, paramInt2, null);
  }
  
  public WritableRaster createWritableChild(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int[] paramArrayOfInt)
  {
    if (paramInt1 < minX) {
      throw new RasterFormatException("parentX lies outside raster");
    }
    if (paramInt2 < minY) {
      throw new RasterFormatException("parentY lies outside raster");
    }
    if ((paramInt1 + paramInt3 < paramInt1) || (paramInt1 + paramInt3 > width + minX)) {
      throw new RasterFormatException("(parentX + width) is outside raster");
    }
    if ((paramInt2 + paramInt4 < paramInt2) || (paramInt2 + paramInt4 > height + minY)) {
      throw new RasterFormatException("(parentY + height) is outside raster");
    }
    SampleModel localSampleModel;
    if (paramArrayOfInt != null) {
      localSampleModel = sampleModel.createSubsetSampleModel(paramArrayOfInt);
    } else {
      localSampleModel = sampleModel;
    }
    int i = paramInt5 - paramInt1;
    int j = paramInt6 - paramInt2;
    return new WritableRaster(localSampleModel, getDataBuffer(), new Rectangle(paramInt5, paramInt6, paramInt3, paramInt4), new Point(sampleModelTranslateX + i, sampleModelTranslateY + j), this);
  }
  
  public void setDataElements(int paramInt1, int paramInt2, Object paramObject)
  {
    sampleModel.setDataElements(paramInt1 - sampleModelTranslateX, paramInt2 - sampleModelTranslateY, paramObject, dataBuffer);
  }
  
  public void setDataElements(int paramInt1, int paramInt2, Raster paramRaster)
  {
    int i = paramInt1 + paramRaster.getMinX();
    int j = paramInt2 + paramRaster.getMinY();
    int k = paramRaster.getWidth();
    int m = paramRaster.getHeight();
    if ((i < minX) || (j < minY) || (i + k > minX + width) || (j + m > minY + height)) {
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
    }
    int n = paramRaster.getMinX();
    int i1 = paramRaster.getMinY();
    Object localObject = null;
    for (int i2 = 0; i2 < m; i2++)
    {
      localObject = paramRaster.getDataElements(n, i1 + i2, k, 1, localObject);
      setDataElements(i, j + i2, k, 1, localObject);
    }
  }
  
  public void setDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject)
  {
    sampleModel.setDataElements(paramInt1 - sampleModelTranslateX, paramInt2 - sampleModelTranslateY, paramInt3, paramInt4, paramObject, dataBuffer);
  }
  
  public void setRect(Raster paramRaster)
  {
    setRect(0, 0, paramRaster);
  }
  
  public void setRect(int paramInt1, int paramInt2, Raster paramRaster)
  {
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
    if (n + i > minX + width) {
      i = minX + width - n;
    }
    if (i1 + j > minY + height) {
      j = minY + height - i1;
    }
    if ((i <= 0) || (j <= 0)) {
      return;
    }
    switch (paramRaster.getSampleModel().getDataType())
    {
    case 0: 
    case 1: 
    case 2: 
    case 3: 
      int[] arrayOfInt = null;
      for (int i3 = 0; i3 < j; i3++)
      {
        arrayOfInt = paramRaster.getPixels(k, m + i3, i, 1, arrayOfInt);
        setPixels(n, i1 + i3, i, 1, arrayOfInt);
      }
      break;
    case 4: 
      float[] arrayOfFloat = null;
      for (int i4 = 0; i4 < j; i4++)
      {
        arrayOfFloat = paramRaster.getPixels(k, m + i4, i, 1, arrayOfFloat);
        setPixels(n, i1 + i4, i, 1, arrayOfFloat);
      }
      break;
    case 5: 
      double[] arrayOfDouble = null;
      for (int i5 = 0; i5 < j; i5++)
      {
        arrayOfDouble = paramRaster.getPixels(k, m + i5, i, 1, arrayOfDouble);
        setPixels(n, i1 + i5, i, 1, arrayOfDouble);
      }
    }
  }
  
  public void setPixel(int paramInt1, int paramInt2, int[] paramArrayOfInt)
  {
    sampleModel.setPixel(paramInt1 - sampleModelTranslateX, paramInt2 - sampleModelTranslateY, paramArrayOfInt, dataBuffer);
  }
  
  public void setPixel(int paramInt1, int paramInt2, float[] paramArrayOfFloat)
  {
    sampleModel.setPixel(paramInt1 - sampleModelTranslateX, paramInt2 - sampleModelTranslateY, paramArrayOfFloat, dataBuffer);
  }
  
  public void setPixel(int paramInt1, int paramInt2, double[] paramArrayOfDouble)
  {
    sampleModel.setPixel(paramInt1 - sampleModelTranslateX, paramInt2 - sampleModelTranslateY, paramArrayOfDouble, dataBuffer);
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt)
  {
    sampleModel.setPixels(paramInt1 - sampleModelTranslateX, paramInt2 - sampleModelTranslateY, paramInt3, paramInt4, paramArrayOfInt, dataBuffer);
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, float[] paramArrayOfFloat)
  {
    sampleModel.setPixels(paramInt1 - sampleModelTranslateX, paramInt2 - sampleModelTranslateY, paramInt3, paramInt4, paramArrayOfFloat, dataBuffer);
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, double[] paramArrayOfDouble)
  {
    sampleModel.setPixels(paramInt1 - sampleModelTranslateX, paramInt2 - sampleModelTranslateY, paramInt3, paramInt4, paramArrayOfDouble, dataBuffer);
  }
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    sampleModel.setSample(paramInt1 - sampleModelTranslateX, paramInt2 - sampleModelTranslateY, paramInt3, paramInt4, dataBuffer);
  }
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, float paramFloat)
  {
    sampleModel.setSample(paramInt1 - sampleModelTranslateX, paramInt2 - sampleModelTranslateY, paramInt3, paramFloat, dataBuffer);
  }
  
  public void setSample(int paramInt1, int paramInt2, int paramInt3, double paramDouble)
  {
    sampleModel.setSample(paramInt1 - sampleModelTranslateX, paramInt2 - sampleModelTranslateY, paramInt3, paramDouble, dataBuffer);
  }
  
  public void setSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfInt)
  {
    sampleModel.setSamples(paramInt1 - sampleModelTranslateX, paramInt2 - sampleModelTranslateY, paramInt3, paramInt4, paramInt5, paramArrayOfInt, dataBuffer);
  }
  
  public void setSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, float[] paramArrayOfFloat)
  {
    sampleModel.setSamples(paramInt1 - sampleModelTranslateX, paramInt2 - sampleModelTranslateY, paramInt3, paramInt4, paramInt5, paramArrayOfFloat, dataBuffer);
  }
  
  public void setSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, double[] paramArrayOfDouble)
  {
    sampleModel.setSamples(paramInt1 - sampleModelTranslateX, paramInt2 - sampleModelTranslateY, paramInt3, paramInt4, paramInt5, paramArrayOfDouble, dataBuffer);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\WritableRaster.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */