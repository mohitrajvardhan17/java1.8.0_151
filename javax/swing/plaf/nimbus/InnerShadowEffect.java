package javax.swing.plaf.nimbus;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Arrays;

class InnerShadowEffect
  extends ShadowEffect
{
  InnerShadowEffect() {}
  
  Effect.EffectType getEffectType()
  {
    return Effect.EffectType.OVER;
  }
  
  BufferedImage applyEffect(BufferedImage paramBufferedImage1, BufferedImage paramBufferedImage2, int paramInt1, int paramInt2)
  {
    if ((paramBufferedImage1 == null) || (paramBufferedImage1.getType() != 2)) {
      throw new IllegalArgumentException("Effect only works with source images of type BufferedImage.TYPE_INT_ARGB.");
    }
    if ((paramBufferedImage2 != null) && (paramBufferedImage2.getType() != 2)) {
      throw new IllegalArgumentException("Effect only works with destination images of type BufferedImage.TYPE_INT_ARGB.");
    }
    double d = Math.toRadians(angle - 90);
    int i = (int)(Math.sin(d) * distance);
    int j = (int)(Math.cos(d) * distance);
    int k = i + size;
    int m = i + size;
    int n = paramInt1 + i + size + size;
    int i1 = paramInt2 + i + size;
    int[] arrayOfInt = getArrayCache().getTmpIntArray(paramInt1);
    byte[] arrayOfByte1 = getArrayCache().getTmpByteArray1(n * i1);
    Arrays.fill(arrayOfByte1, (byte)-1);
    byte[] arrayOfByte2 = getArrayCache().getTmpByteArray2(n * i1);
    byte[] arrayOfByte3 = getArrayCache().getTmpByteArray3(n * i1);
    WritableRaster localWritableRaster1 = paramBufferedImage1.getRaster();
    for (int i2 = 0; i2 < paramInt2; i2++)
    {
      int i3 = i2 + m;
      i4 = i3 * n;
      localWritableRaster1.getDataElements(0, i2, paramInt1, 1, arrayOfInt);
      for (i5 = 0; i5 < paramInt1; i5++)
      {
        i6 = i5 + k;
        arrayOfByte1[(i4 + i6)] = ((byte)(255 - ((arrayOfInt[i5] & 0xFF000000) >>> 24) & 0xFF));
      }
    }
    float[] arrayOfFloat = EffectUtils.createGaussianKernel(size * 2);
    EffectUtils.blur(arrayOfByte1, arrayOfByte3, n, i1, arrayOfFloat, size * 2);
    EffectUtils.blur(arrayOfByte3, arrayOfByte2, i1, n, arrayOfFloat, size * 2);
    float f = Math.min(1.0F / (1.0F - 0.01F * spread), 255.0F);
    for (int i4 = 0; i4 < arrayOfByte2.length; i4++)
    {
      i5 = (int)((arrayOfByte2[i4] & 0xFF) * f);
      arrayOfByte2[i4] = (i5 > 255 ? -1 : (byte)i5);
    }
    if (paramBufferedImage2 == null) {
      paramBufferedImage2 = new BufferedImage(paramInt1, paramInt2, 2);
    }
    WritableRaster localWritableRaster2 = paramBufferedImage2.getRaster();
    int i5 = color.getRed();
    int i6 = color.getGreen();
    int i7 = color.getBlue();
    for (int i8 = 0; i8 < paramInt2; i8++)
    {
      int i9 = i8 + m;
      int i10 = i9 * n;
      int i11 = (i9 - j) * n;
      for (int i12 = 0; i12 < paramInt1; i12++)
      {
        int i13 = i12 + k;
        int i14 = 255 - (arrayOfByte1[(i10 + i13)] & 0xFF);
        int i15 = arrayOfByte2[(i11 + (i13 - i))] & 0xFF;
        int i16 = Math.min(i14, i15);
        arrayOfInt[i12] = (((byte)i16 & 0xFF) << 24 | i5 << 16 | i6 << 8 | i7);
      }
      localWritableRaster2.setDataElements(0, i8, paramInt1, 1, arrayOfInt);
    }
    return paramBufferedImage2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\InnerShadowEffect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */