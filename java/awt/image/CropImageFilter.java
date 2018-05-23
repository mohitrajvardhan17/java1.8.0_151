package java.awt.image;

import java.awt.Rectangle;
import java.util.Hashtable;

public class CropImageFilter
  extends ImageFilter
{
  int cropX;
  int cropY;
  int cropW;
  int cropH;
  
  public CropImageFilter(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    cropX = paramInt1;
    cropY = paramInt2;
    cropW = paramInt3;
    cropH = paramInt4;
  }
  
  public void setProperties(Hashtable<?, ?> paramHashtable)
  {
    Hashtable localHashtable = (Hashtable)paramHashtable.clone();
    localHashtable.put("croprect", new Rectangle(cropX, cropY, cropW, cropH));
    super.setProperties(localHashtable);
  }
  
  public void setDimensions(int paramInt1, int paramInt2)
  {
    consumer.setDimensions(cropW, cropH);
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, byte[] paramArrayOfByte, int paramInt5, int paramInt6)
  {
    int i = paramInt1;
    if (i < cropX) {
      i = cropX;
    }
    int j = addWithoutOverflow(paramInt1, paramInt3);
    if (j > cropX + cropW) {
      j = cropX + cropW;
    }
    int k = paramInt2;
    if (k < cropY) {
      k = cropY;
    }
    int m = addWithoutOverflow(paramInt2, paramInt4);
    if (m > cropY + cropH) {
      m = cropY + cropH;
    }
    if ((i >= j) || (k >= m)) {
      return;
    }
    consumer.setPixels(i - cropX, k - cropY, j - i, m - k, paramColorModel, paramArrayOfByte, paramInt5 + (k - paramInt2) * paramInt6 + (i - paramInt1), paramInt6);
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, int[] paramArrayOfInt, int paramInt5, int paramInt6)
  {
    int i = paramInt1;
    if (i < cropX) {
      i = cropX;
    }
    int j = addWithoutOverflow(paramInt1, paramInt3);
    if (j > cropX + cropW) {
      j = cropX + cropW;
    }
    int k = paramInt2;
    if (k < cropY) {
      k = cropY;
    }
    int m = addWithoutOverflow(paramInt2, paramInt4);
    if (m > cropY + cropH) {
      m = cropY + cropH;
    }
    if ((i >= j) || (k >= m)) {
      return;
    }
    consumer.setPixels(i - cropX, k - cropY, j - i, m - k, paramColorModel, paramArrayOfInt, paramInt5 + (k - paramInt2) * paramInt6 + (i - paramInt1), paramInt6);
  }
  
  private int addWithoutOverflow(int paramInt1, int paramInt2)
  {
    int i = paramInt1 + paramInt2;
    if ((paramInt1 > 0) && (paramInt2 > 0) && (i < 0)) {
      i = Integer.MAX_VALUE;
    } else if ((paramInt1 < 0) && (paramInt2 < 0) && (i > 0)) {
      i = Integer.MIN_VALUE;
    }
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\CropImageFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */