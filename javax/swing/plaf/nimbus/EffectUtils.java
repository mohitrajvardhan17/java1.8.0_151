package javax.swing.plaf.nimbus;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

class EffectUtils
{
  EffectUtils() {}
  
  static void clearImage(BufferedImage paramBufferedImage)
  {
    Graphics2D localGraphics2D = paramBufferedImage.createGraphics();
    localGraphics2D.setComposite(AlphaComposite.Clear);
    localGraphics2D.fillRect(0, 0, paramBufferedImage.getWidth(), paramBufferedImage.getHeight());
    localGraphics2D.dispose();
  }
  
  static BufferedImage gaussianBlur(BufferedImage paramBufferedImage1, BufferedImage paramBufferedImage2, int paramInt)
  {
    int i = paramBufferedImage1.getWidth();
    int j = paramBufferedImage1.getHeight();
    if ((paramBufferedImage2 == null) || (paramBufferedImage2.getWidth() != i) || (paramBufferedImage2.getHeight() != j) || (paramBufferedImage1.getType() != paramBufferedImage2.getType())) {
      paramBufferedImage2 = createColorModelCompatibleImage(paramBufferedImage1);
    }
    float[] arrayOfFloat = createGaussianKernel(paramInt);
    Object localObject1;
    Object localObject2;
    if (paramBufferedImage1.getType() == 2)
    {
      localObject1 = new int[i * j];
      localObject2 = new int[i * j];
      getPixels(paramBufferedImage1, 0, 0, i, j, (int[])localObject1);
      blur((int[])localObject1, (int[])localObject2, i, j, arrayOfFloat, paramInt);
      blur((int[])localObject2, (int[])localObject1, j, i, arrayOfFloat, paramInt);
      setPixels(paramBufferedImage2, 0, 0, i, j, (int[])localObject1);
    }
    else if (paramBufferedImage1.getType() == 10)
    {
      localObject1 = new byte[i * j];
      localObject2 = new byte[i * j];
      getPixels(paramBufferedImage1, 0, 0, i, j, (byte[])localObject1);
      blur((byte[])localObject1, (byte[])localObject2, i, j, arrayOfFloat, paramInt);
      blur((byte[])localObject2, (byte[])localObject1, j, i, arrayOfFloat, paramInt);
      setPixels(paramBufferedImage2, 0, 0, i, j, (byte[])localObject1);
    }
    else
    {
      throw new IllegalArgumentException("EffectUtils.gaussianBlur() src image is not a supported type, type=[" + paramBufferedImage1.getType() + "]");
    }
    return paramBufferedImage2;
  }
  
  private static void blur(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt1, int paramInt2, float[] paramArrayOfFloat, int paramInt3)
  {
    for (int n = 0; n < paramInt2; n++)
    {
      int i1 = n;
      int i2 = n * paramInt1;
      for (int i3 = 0; i3 < paramInt1; i3++)
      {
        float f4;
        float f3;
        float f2;
        float f1 = f2 = f3 = f4 = 0.0F;
        for (int i4 = -paramInt3; i4 <= paramInt3; i4++)
        {
          int i5 = i3 + i4;
          if ((i5 < 0) || (i5 >= paramInt1)) {
            i5 = (i3 + paramInt1) % paramInt1;
          }
          int i6 = paramArrayOfInt1[(i2 + i5)];
          float f5 = paramArrayOfFloat[(paramInt3 + i4)];
          f1 += f5 * (i6 >> 24 & 0xFF);
          f2 += f5 * (i6 >> 16 & 0xFF);
          f3 += f5 * (i6 >> 8 & 0xFF);
          f4 += f5 * (i6 & 0xFF);
        }
        int i = (int)(f1 + 0.5F);
        int j = (int)(f2 + 0.5F);
        int k = (int)(f3 + 0.5F);
        int m = (int)(f4 + 0.5F);
        paramArrayOfInt2[i1] = ((i > 255 ? 'ÿ' : i) << 24 | (j > 255 ? 'ÿ' : j) << 16 | (k > 255 ? 'ÿ' : k) << 8 | (m > 255 ? 'ÿ' : m));
        i1 += paramInt2;
      }
    }
  }
  
  static void blur(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, float[] paramArrayOfFloat, int paramInt3)
  {
    for (int j = 0; j < paramInt2; j++)
    {
      int k = j;
      int m = j * paramInt1;
      for (int n = 0; n < paramInt1; n++)
      {
        float f1 = 0.0F;
        for (int i1 = -paramInt3; i1 <= paramInt3; i1++)
        {
          int i2 = n + i1;
          if ((i2 < 0) || (i2 >= paramInt1)) {
            i2 = (n + paramInt1) % paramInt1;
          }
          int i3 = paramArrayOfByte1[(m + i2)] & 0xFF;
          float f2 = paramArrayOfFloat[(paramInt3 + i1)];
          f1 += f2 * i3;
        }
        int i = (int)(f1 + 0.5F);
        paramArrayOfByte2[k] = ((byte)(i > 255 ? 'ÿ' : i));
        k += paramInt2;
      }
    }
  }
  
  static float[] createGaussianKernel(int paramInt)
  {
    if (paramInt < 1) {
      throw new IllegalArgumentException("Radius must be >= 1");
    }
    float[] arrayOfFloat = new float[paramInt * 2 + 1];
    float f1 = paramInt / 3.0F;
    float f2 = 2.0F * f1 * f1;
    float f3 = (float)Math.sqrt(f2 * 3.141592653589793D);
    float f4 = 0.0F;
    for (int i = -paramInt; i <= paramInt; i++)
    {
      float f5 = i * i;
      int j = i + paramInt;
      arrayOfFloat[j] = ((float)Math.exp(-f5 / f2) / f3);
      f4 += arrayOfFloat[j];
    }
    for (i = 0; i < arrayOfFloat.length; i++) {
      arrayOfFloat[i] /= f4;
    }
    return arrayOfFloat;
  }
  
  static byte[] getPixels(BufferedImage paramBufferedImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte)
  {
    if ((paramInt3 == 0) || (paramInt4 == 0)) {
      return new byte[0];
    }
    if (paramArrayOfByte == null) {
      paramArrayOfByte = new byte[paramInt3 * paramInt4];
    } else if (paramArrayOfByte.length < paramInt3 * paramInt4) {
      throw new IllegalArgumentException("pixels array must have a length >= w*h");
    }
    int i = paramBufferedImage.getType();
    if (i == 10)
    {
      WritableRaster localWritableRaster = paramBufferedImage.getRaster();
      return (byte[])localWritableRaster.getDataElements(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfByte);
    }
    throw new IllegalArgumentException("Only type BYTE_GRAY is supported");
  }
  
  static void setPixels(BufferedImage paramBufferedImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte)
  {
    if ((paramArrayOfByte == null) || (paramInt3 == 0) || (paramInt4 == 0)) {
      return;
    }
    if (paramArrayOfByte.length < paramInt3 * paramInt4) {
      throw new IllegalArgumentException("pixels array must have a length >= w*h");
    }
    int i = paramBufferedImage.getType();
    if (i == 10)
    {
      WritableRaster localWritableRaster = paramBufferedImage.getRaster();
      localWritableRaster.setDataElements(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfByte);
    }
    else
    {
      throw new IllegalArgumentException("Only type BYTE_GRAY is supported");
    }
  }
  
  public static int[] getPixels(BufferedImage paramBufferedImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt)
  {
    if ((paramInt3 == 0) || (paramInt4 == 0)) {
      return new int[0];
    }
    if (paramArrayOfInt == null) {
      paramArrayOfInt = new int[paramInt3 * paramInt4];
    } else if (paramArrayOfInt.length < paramInt3 * paramInt4) {
      throw new IllegalArgumentException("pixels array must have a length >= w*h");
    }
    int i = paramBufferedImage.getType();
    if ((i == 2) || (i == 1))
    {
      WritableRaster localWritableRaster = paramBufferedImage.getRaster();
      return (int[])localWritableRaster.getDataElements(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt);
    }
    return paramBufferedImage.getRGB(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt, 0, paramInt3);
  }
  
  public static void setPixels(BufferedImage paramBufferedImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt)
  {
    if ((paramArrayOfInt == null) || (paramInt3 == 0) || (paramInt4 == 0)) {
      return;
    }
    if (paramArrayOfInt.length < paramInt3 * paramInt4) {
      throw new IllegalArgumentException("pixels array must have a length >= w*h");
    }
    int i = paramBufferedImage.getType();
    if ((i == 2) || (i == 1))
    {
      WritableRaster localWritableRaster = paramBufferedImage.getRaster();
      localWritableRaster.setDataElements(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt);
    }
    else
    {
      paramBufferedImage.setRGB(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt, 0, paramInt3);
    }
  }
  
  public static BufferedImage createColorModelCompatibleImage(BufferedImage paramBufferedImage)
  {
    ColorModel localColorModel = paramBufferedImage.getColorModel();
    return new BufferedImage(localColorModel, localColorModel.createCompatibleWritableRaster(paramBufferedImage.getWidth(), paramBufferedImage.getHeight()), localColorModel.isAlphaPremultiplied(), null);
  }
  
  public static BufferedImage createCompatibleTranslucentImage(int paramInt1, int paramInt2)
  {
    return isHeadless() ? new BufferedImage(paramInt1, paramInt2, 2) : getGraphicsConfiguration().createCompatibleImage(paramInt1, paramInt2, 3);
  }
  
  private static boolean isHeadless()
  {
    return GraphicsEnvironment.isHeadless();
  }
  
  private static GraphicsConfiguration getGraphicsConfiguration()
  {
    return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\EffectUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */