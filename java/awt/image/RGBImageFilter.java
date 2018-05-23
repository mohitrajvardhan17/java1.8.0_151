package java.awt.image;

public abstract class RGBImageFilter
  extends ImageFilter
{
  protected ColorModel origmodel;
  protected ColorModel newmodel;
  protected boolean canFilterIndexColorModel;
  
  public RGBImageFilter() {}
  
  public void setColorModel(ColorModel paramColorModel)
  {
    if ((canFilterIndexColorModel) && ((paramColorModel instanceof IndexColorModel)))
    {
      IndexColorModel localIndexColorModel = filterIndexColorModel((IndexColorModel)paramColorModel);
      substituteColorModel(paramColorModel, localIndexColorModel);
      consumer.setColorModel(localIndexColorModel);
    }
    else
    {
      consumer.setColorModel(ColorModel.getRGBdefault());
    }
  }
  
  public void substituteColorModel(ColorModel paramColorModel1, ColorModel paramColorModel2)
  {
    origmodel = paramColorModel1;
    newmodel = paramColorModel2;
  }
  
  public IndexColorModel filterIndexColorModel(IndexColorModel paramIndexColorModel)
  {
    int i = paramIndexColorModel.getMapSize();
    byte[] arrayOfByte1 = new byte[i];
    byte[] arrayOfByte2 = new byte[i];
    byte[] arrayOfByte3 = new byte[i];
    byte[] arrayOfByte4 = new byte[i];
    paramIndexColorModel.getReds(arrayOfByte1);
    paramIndexColorModel.getGreens(arrayOfByte2);
    paramIndexColorModel.getBlues(arrayOfByte3);
    paramIndexColorModel.getAlphas(arrayOfByte4);
    int j = paramIndexColorModel.getTransparentPixel();
    int k = 0;
    for (int m = 0; m < i; m++)
    {
      int n = filterRGB(-1, -1, paramIndexColorModel.getRGB(m));
      arrayOfByte4[m] = ((byte)(n >> 24));
      if ((arrayOfByte4[m] != -1) && (m != j)) {
        k = 1;
      }
      arrayOfByte1[m] = ((byte)(n >> 16));
      arrayOfByte2[m] = ((byte)(n >> 8));
      arrayOfByte3[m] = ((byte)(n >> 0));
    }
    if (k != 0) {
      return new IndexColorModel(paramIndexColorModel.getPixelSize(), i, arrayOfByte1, arrayOfByte2, arrayOfByte3, arrayOfByte4);
    }
    return new IndexColorModel(paramIndexColorModel.getPixelSize(), i, arrayOfByte1, arrayOfByte2, arrayOfByte3, j);
  }
  
  public void filterRGBPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, int paramInt5, int paramInt6)
  {
    int i = paramInt5;
    for (int j = 0; j < paramInt4; j++)
    {
      for (int k = 0; k < paramInt3; k++)
      {
        paramArrayOfInt[i] = filterRGB(paramInt1 + k, paramInt2 + j, paramArrayOfInt[i]);
        i++;
      }
      i += paramInt6 - paramInt3;
    }
    consumer.setPixels(paramInt1, paramInt2, paramInt3, paramInt4, ColorModel.getRGBdefault(), paramArrayOfInt, paramInt5, paramInt6);
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, byte[] paramArrayOfByte, int paramInt5, int paramInt6)
  {
    if (paramColorModel == origmodel)
    {
      consumer.setPixels(paramInt1, paramInt2, paramInt3, paramInt4, newmodel, paramArrayOfByte, paramInt5, paramInt6);
    }
    else
    {
      int[] arrayOfInt = new int[paramInt3];
      int i = paramInt5;
      for (int j = 0; j < paramInt4; j++)
      {
        for (int k = 0; k < paramInt3; k++)
        {
          arrayOfInt[k] = paramColorModel.getRGB(paramArrayOfByte[i] & 0xFF);
          i++;
        }
        i += paramInt6 - paramInt3;
        filterRGBPixels(paramInt1, paramInt2 + j, paramInt3, 1, arrayOfInt, 0, paramInt3);
      }
    }
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, int[] paramArrayOfInt, int paramInt5, int paramInt6)
  {
    if (paramColorModel == origmodel)
    {
      consumer.setPixels(paramInt1, paramInt2, paramInt3, paramInt4, newmodel, paramArrayOfInt, paramInt5, paramInt6);
    }
    else
    {
      int[] arrayOfInt = new int[paramInt3];
      int i = paramInt5;
      for (int j = 0; j < paramInt4; j++)
      {
        for (int k = 0; k < paramInt3; k++)
        {
          arrayOfInt[k] = paramColorModel.getRGB(paramArrayOfInt[i]);
          i++;
        }
        i += paramInt6 - paramInt3;
        filterRGBPixels(paramInt1, paramInt2 + j, paramInt3, 1, arrayOfInt, 0, paramInt3);
      }
    }
  }
  
  public abstract int filterRGB(int paramInt1, int paramInt2, int paramInt3);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\RGBImageFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */