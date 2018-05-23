package java.awt.image;

public class BufferedImageFilter
  extends ImageFilter
  implements Cloneable
{
  BufferedImageOp bufferedImageOp;
  ColorModel model;
  int width;
  int height;
  byte[] bytePixels;
  int[] intPixels;
  
  public BufferedImageFilter(BufferedImageOp paramBufferedImageOp)
  {
    if (paramBufferedImageOp == null) {
      throw new NullPointerException("Operation cannot be null");
    }
    bufferedImageOp = paramBufferedImageOp;
  }
  
  public BufferedImageOp getBufferedImageOp()
  {
    return bufferedImageOp;
  }
  
  public void setDimensions(int paramInt1, int paramInt2)
  {
    if ((paramInt1 <= 0) || (paramInt2 <= 0))
    {
      imageComplete(3);
      return;
    }
    width = paramInt1;
    height = paramInt2;
  }
  
  public void setColorModel(ColorModel paramColorModel)
  {
    model = paramColorModel;
  }
  
  private void convertToRGB()
  {
    int i = width * height;
    int[] arrayOfInt = new int[i];
    int j;
    if (bytePixels != null) {
      for (j = 0; j < i; j++) {
        arrayOfInt[j] = model.getRGB(bytePixels[j] & 0xFF);
      }
    } else if (intPixels != null) {
      for (j = 0; j < i; j++) {
        arrayOfInt[j] = model.getRGB(intPixels[j]);
      }
    }
    bytePixels = null;
    intPixels = arrayOfInt;
    model = ColorModel.getRGBdefault();
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, byte[] paramArrayOfByte, int paramInt5, int paramInt6)
  {
    if ((paramInt3 < 0) || (paramInt4 < 0)) {
      throw new IllegalArgumentException("Width (" + paramInt3 + ") and height (" + paramInt4 + ") must be > 0");
    }
    if ((paramInt3 == 0) || (paramInt4 == 0)) {
      return;
    }
    if (paramInt2 < 0)
    {
      i = -paramInt2;
      if (i >= paramInt4) {
        return;
      }
      paramInt5 += paramInt6 * i;
      paramInt2 += i;
      paramInt4 -= i;
    }
    if (paramInt2 + paramInt4 > height)
    {
      paramInt4 = height - paramInt2;
      if (paramInt4 <= 0) {
        return;
      }
    }
    if (paramInt1 < 0)
    {
      i = -paramInt1;
      if (i >= paramInt3) {
        return;
      }
      paramInt5 += i;
      paramInt1 += i;
      paramInt3 -= i;
    }
    if (paramInt1 + paramInt3 > width)
    {
      paramInt3 = width - paramInt1;
      if (paramInt3 <= 0) {
        return;
      }
    }
    int i = paramInt2 * width + paramInt1;
    int j;
    if (intPixels == null)
    {
      if (bytePixels == null)
      {
        bytePixels = new byte[width * height];
        model = paramColorModel;
      }
      else if (model != paramColorModel)
      {
        convertToRGB();
      }
      if (bytePixels != null) {
        for (j = paramInt4; j > 0; j--)
        {
          System.arraycopy(paramArrayOfByte, paramInt5, bytePixels, i, paramInt3);
          paramInt5 += paramInt6;
          i += width;
        }
      }
    }
    if (intPixels != null)
    {
      j = width - paramInt3;
      int k = paramInt6 - paramInt3;
      for (int m = paramInt4; m > 0; m--)
      {
        for (int n = paramInt3; n > 0; n--) {
          intPixels[(i++)] = paramColorModel.getRGB(paramArrayOfByte[(paramInt5++)] & 0xFF);
        }
        paramInt5 += k;
        i += j;
      }
    }
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, int[] paramArrayOfInt, int paramInt5, int paramInt6)
  {
    if ((paramInt3 < 0) || (paramInt4 < 0)) {
      throw new IllegalArgumentException("Width (" + paramInt3 + ") and height (" + paramInt4 + ") must be > 0");
    }
    if ((paramInt3 == 0) || (paramInt4 == 0)) {
      return;
    }
    if (paramInt2 < 0)
    {
      i = -paramInt2;
      if (i >= paramInt4) {
        return;
      }
      paramInt5 += paramInt6 * i;
      paramInt2 += i;
      paramInt4 -= i;
    }
    if (paramInt2 + paramInt4 > height)
    {
      paramInt4 = height - paramInt2;
      if (paramInt4 <= 0) {
        return;
      }
    }
    if (paramInt1 < 0)
    {
      i = -paramInt1;
      if (i >= paramInt3) {
        return;
      }
      paramInt5 += i;
      paramInt1 += i;
      paramInt3 -= i;
    }
    if (paramInt1 + paramInt3 > width)
    {
      paramInt3 = width - paramInt1;
      if (paramInt3 <= 0) {
        return;
      }
    }
    if (intPixels == null) {
      if (bytePixels == null)
      {
        intPixels = new int[width * height];
        model = paramColorModel;
      }
      else
      {
        convertToRGB();
      }
    }
    int i = paramInt2 * width + paramInt1;
    int j;
    if (model == paramColorModel)
    {
      for (j = paramInt4; j > 0; j--)
      {
        System.arraycopy(paramArrayOfInt, paramInt5, intPixels, i, paramInt3);
        paramInt5 += paramInt6;
        i += width;
      }
    }
    else
    {
      if (model != ColorModel.getRGBdefault()) {
        convertToRGB();
      }
      j = width - paramInt3;
      int k = paramInt6 - paramInt3;
      for (int m = paramInt4; m > 0; m--)
      {
        for (int n = paramInt3; n > 0; n--) {
          intPixels[(i++)] = paramColorModel.getRGB(paramArrayOfInt[(paramInt5++)]);
        }
        paramInt5 += k;
        i += j;
      }
    }
  }
  
  public void imageComplete(int paramInt)
  {
    switch (paramInt)
    {
    case 1: 
    case 4: 
      model = null;
      width = -1;
      height = -1;
      intPixels = null;
      bytePixels = null;
      break;
    case 2: 
    case 3: 
      if ((width > 0) && (height > 0))
      {
        WritableRaster localWritableRaster;
        if ((model instanceof DirectColorModel))
        {
          if (intPixels == null) {
            break;
          }
          localWritableRaster = createDCMraster();
        }
        else if ((model instanceof IndexColorModel))
        {
          localObject1 = new int[] { 0 };
          if (bytePixels == null) {
            break;
          }
          localObject2 = new DataBufferByte(bytePixels, width * height);
          localWritableRaster = Raster.createInterleavedRaster((DataBuffer)localObject2, width, height, width, 1, (int[])localObject1, null);
        }
        else
        {
          convertToRGB();
          if (intPixels == null) {
            break;
          }
          localWritableRaster = createDCMraster();
        }
        Object localObject1 = new BufferedImage(model, localWritableRaster, model.isAlphaPremultiplied(), null);
        localObject1 = bufferedImageOp.filter((BufferedImage)localObject1, null);
        Object localObject2 = ((BufferedImage)localObject1).getRaster();
        ColorModel localColorModel = ((BufferedImage)localObject1).getColorModel();
        int i = ((WritableRaster)localObject2).getWidth();
        int j = ((WritableRaster)localObject2).getHeight();
        consumer.setDimensions(i, j);
        consumer.setColorModel(localColorModel);
        Object localObject3;
        if ((localColorModel instanceof DirectColorModel))
        {
          localObject3 = (DataBufferInt)((WritableRaster)localObject2).getDataBuffer();
          consumer.setPixels(0, 0, i, j, localColorModel, ((DataBufferInt)localObject3).getData(), 0, i);
        }
        else if ((localColorModel instanceof IndexColorModel))
        {
          localObject3 = (DataBufferByte)((WritableRaster)localObject2).getDataBuffer();
          consumer.setPixels(0, 0, i, j, localColorModel, ((DataBufferByte)localObject3).getData(), 0, i);
        }
        else
        {
          throw new InternalError("Unknown color model " + localColorModel);
        }
      }
      break;
    }
    consumer.imageComplete(paramInt);
  }
  
  private final WritableRaster createDCMraster()
  {
    DirectColorModel localDirectColorModel = (DirectColorModel)model;
    boolean bool = model.hasAlpha();
    int[] arrayOfInt = new int[3 + (bool ? 1 : 0)];
    arrayOfInt[0] = localDirectColorModel.getRedMask();
    arrayOfInt[1] = localDirectColorModel.getGreenMask();
    arrayOfInt[2] = localDirectColorModel.getBlueMask();
    if (bool) {
      arrayOfInt[3] = localDirectColorModel.getAlphaMask();
    }
    DataBufferInt localDataBufferInt = new DataBufferInt(intPixels, width * height);
    WritableRaster localWritableRaster = Raster.createPackedRaster(localDataBufferInt, width, height, width, arrayOfInt, null);
    return localWritableRaster;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\BufferedImageFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */