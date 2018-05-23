package java.awt.image;

import java.util.Hashtable;

public class ReplicateScaleFilter
  extends ImageFilter
{
  protected int srcWidth;
  protected int srcHeight;
  protected int destWidth;
  protected int destHeight;
  protected int[] srcrows;
  protected int[] srccols;
  protected Object outpixbuf;
  
  public ReplicateScaleFilter(int paramInt1, int paramInt2)
  {
    if ((paramInt1 == 0) || (paramInt2 == 0)) {
      throw new IllegalArgumentException("Width (" + paramInt1 + ") and height (" + paramInt2 + ") must be non-zero");
    }
    destWidth = paramInt1;
    destHeight = paramInt2;
  }
  
  public void setProperties(Hashtable<?, ?> paramHashtable)
  {
    Hashtable localHashtable = (Hashtable)paramHashtable.clone();
    String str1 = "rescale";
    String str2 = destWidth + "x" + destHeight;
    Object localObject = localHashtable.get(str1);
    if ((localObject != null) && ((localObject instanceof String))) {
      str2 = (String)localObject + ", " + str2;
    }
    localHashtable.put(str1, str2);
    super.setProperties(localHashtable);
  }
  
  public void setDimensions(int paramInt1, int paramInt2)
  {
    srcWidth = paramInt1;
    srcHeight = paramInt2;
    if (destWidth < 0)
    {
      if (destHeight < 0)
      {
        destWidth = srcWidth;
        destHeight = srcHeight;
      }
      else
      {
        destWidth = (srcWidth * destHeight / srcHeight);
      }
    }
    else if (destHeight < 0) {
      destHeight = (srcHeight * destWidth / srcWidth);
    }
    consumer.setDimensions(destWidth, destHeight);
  }
  
  private void calculateMaps()
  {
    srcrows = new int[destHeight + 1];
    for (int i = 0; i <= destHeight; i++) {
      srcrows[i] = ((2 * i * srcHeight + srcHeight) / (2 * destHeight));
    }
    srccols = new int[destWidth + 1];
    for (i = 0; i <= destWidth; i++) {
      srccols[i] = ((2 * i * srcWidth + srcWidth) / (2 * destWidth));
    }
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, byte[] paramArrayOfByte, int paramInt5, int paramInt6)
  {
    if ((srcrows == null) || (srccols == null)) {
      calculateMaps();
    }
    int k = (2 * paramInt1 * destWidth + srcWidth - 1) / (2 * srcWidth);
    int m = (2 * paramInt2 * destHeight + srcHeight - 1) / (2 * srcHeight);
    byte[] arrayOfByte;
    if ((outpixbuf != null) && ((outpixbuf instanceof byte[])))
    {
      arrayOfByte = (byte[])outpixbuf;
    }
    else
    {
      arrayOfByte = new byte[destWidth];
      outpixbuf = arrayOfByte;
    }
    int j;
    for (int n = m; (j = srcrows[n]) < paramInt2 + paramInt4; n++)
    {
      int i1 = paramInt5 + paramInt6 * (j - paramInt2);
      int i;
      for (int i2 = k; (i = srccols[i2]) < paramInt1 + paramInt3; i2++) {
        arrayOfByte[i2] = paramArrayOfByte[(i1 + i - paramInt1)];
      }
      if (i2 > k) {
        consumer.setPixels(k, n, i2 - k, 1, paramColorModel, arrayOfByte, k, destWidth);
      }
    }
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, int[] paramArrayOfInt, int paramInt5, int paramInt6)
  {
    if ((srcrows == null) || (srccols == null)) {
      calculateMaps();
    }
    int k = (2 * paramInt1 * destWidth + srcWidth - 1) / (2 * srcWidth);
    int m = (2 * paramInt2 * destHeight + srcHeight - 1) / (2 * srcHeight);
    int[] arrayOfInt;
    if ((outpixbuf != null) && ((outpixbuf instanceof int[])))
    {
      arrayOfInt = (int[])outpixbuf;
    }
    else
    {
      arrayOfInt = new int[destWidth];
      outpixbuf = arrayOfInt;
    }
    int j;
    for (int n = m; (j = srcrows[n]) < paramInt2 + paramInt4; n++)
    {
      int i1 = paramInt5 + paramInt6 * (j - paramInt2);
      int i;
      for (int i2 = k; (i = srccols[i2]) < paramInt1 + paramInt3; i2++) {
        arrayOfInt[i2] = paramArrayOfInt[(i1 + i - paramInt1)];
      }
      if (i2 > k) {
        consumer.setPixels(k, n, i2 - k, 1, paramColorModel, arrayOfInt, k, destWidth);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\ReplicateScaleFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */