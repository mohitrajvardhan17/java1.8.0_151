package sun.awt;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import sun.awt.image.ImageRepresentation;
import sun.awt.image.ToolkitImage;

public class IconInfo
{
  private int[] intIconData;
  private long[] longIconData;
  private Image image;
  private final int width;
  private final int height;
  private int scaledWidth;
  private int scaledHeight;
  private int rawLength;
  
  public IconInfo(int[] paramArrayOfInt)
  {
    intIconData = (null == paramArrayOfInt ? null : Arrays.copyOf(paramArrayOfInt, paramArrayOfInt.length));
    width = paramArrayOfInt[0];
    height = paramArrayOfInt[1];
    scaledWidth = width;
    scaledHeight = height;
    rawLength = (width * height + 2);
  }
  
  public IconInfo(long[] paramArrayOfLong)
  {
    longIconData = (null == paramArrayOfLong ? null : Arrays.copyOf(paramArrayOfLong, paramArrayOfLong.length));
    width = ((int)paramArrayOfLong[0]);
    height = ((int)paramArrayOfLong[1]);
    scaledWidth = width;
    scaledHeight = height;
    rawLength = (width * height + 2);
  }
  
  public IconInfo(Image paramImage)
  {
    image = paramImage;
    if ((paramImage instanceof ToolkitImage))
    {
      ImageRepresentation localImageRepresentation = ((ToolkitImage)paramImage).getImageRep();
      localImageRepresentation.reconstruct(32);
      width = localImageRepresentation.getWidth();
      height = localImageRepresentation.getHeight();
    }
    else
    {
      width = paramImage.getWidth(null);
      height = paramImage.getHeight(null);
    }
    scaledWidth = width;
    scaledHeight = height;
    rawLength = (width * height + 2);
  }
  
  public void setScaledSize(int paramInt1, int paramInt2)
  {
    scaledWidth = paramInt1;
    scaledHeight = paramInt2;
    rawLength = (paramInt1 * paramInt2 + 2);
  }
  
  public boolean isValid()
  {
    return (width > 0) && (height > 0);
  }
  
  public int getWidth()
  {
    return width;
  }
  
  public int getHeight()
  {
    return height;
  }
  
  public String toString()
  {
    return "IconInfo[w=" + width + ",h=" + height + ",sw=" + scaledWidth + ",sh=" + scaledHeight + "]";
  }
  
  public int getRawLength()
  {
    return rawLength;
  }
  
  public int[] getIntData()
  {
    if (intIconData == null) {
      if (longIconData != null) {
        intIconData = longArrayToIntArray(longIconData);
      } else if (image != null) {
        intIconData = imageToIntArray(image, scaledWidth, scaledHeight);
      }
    }
    return intIconData;
  }
  
  public long[] getLongData()
  {
    if (longIconData == null) {
      if (intIconData != null)
      {
        longIconData = intArrayToLongArray(intIconData);
      }
      else if (image != null)
      {
        int[] arrayOfInt = imageToIntArray(image, scaledWidth, scaledHeight);
        longIconData = intArrayToLongArray(arrayOfInt);
      }
    }
    return longIconData;
  }
  
  public Image getImage()
  {
    if (image == null) {
      if (intIconData != null)
      {
        image = intArrayToImage(intIconData);
      }
      else if (longIconData != null)
      {
        int[] arrayOfInt = longArrayToIntArray(longIconData);
        image = intArrayToImage(arrayOfInt);
      }
    }
    return image;
  }
  
  private static int[] longArrayToIntArray(long[] paramArrayOfLong)
  {
    int[] arrayOfInt = new int[paramArrayOfLong.length];
    for (int i = 0; i < paramArrayOfLong.length; i++) {
      arrayOfInt[i] = ((int)paramArrayOfLong[i]);
    }
    return arrayOfInt;
  }
  
  private static long[] intArrayToLongArray(int[] paramArrayOfInt)
  {
    long[] arrayOfLong = new long[paramArrayOfInt.length];
    for (int i = 0; i < paramArrayOfInt.length; i++) {
      arrayOfLong[i] = paramArrayOfInt[i];
    }
    return arrayOfLong;
  }
  
  static Image intArrayToImage(int[] paramArrayOfInt)
  {
    DirectColorModel localDirectColorModel = new DirectColorModel(ColorSpace.getInstance(1000), 32, 16711680, 65280, 255, -16777216, false, 3);
    DataBufferInt localDataBufferInt = new DataBufferInt(paramArrayOfInt, paramArrayOfInt.length - 2, 2);
    WritableRaster localWritableRaster = Raster.createPackedRaster(localDataBufferInt, paramArrayOfInt[0], paramArrayOfInt[1], paramArrayOfInt[0], new int[] { 16711680, 65280, 255, -16777216 }, null);
    BufferedImage localBufferedImage = new BufferedImage(localDirectColorModel, localWritableRaster, false, null);
    return localBufferedImage;
  }
  
  static int[] imageToIntArray(Image paramImage, int paramInt1, int paramInt2)
  {
    if ((paramInt1 <= 0) || (paramInt2 <= 0)) {
      return null;
    }
    DirectColorModel localDirectColorModel = new DirectColorModel(ColorSpace.getInstance(1000), 32, 16711680, 65280, 255, -16777216, false, 3);
    DataBufferInt localDataBufferInt = new DataBufferInt(paramInt1 * paramInt2);
    WritableRaster localWritableRaster = Raster.createPackedRaster(localDataBufferInt, paramInt1, paramInt2, paramInt1, new int[] { 16711680, 65280, 255, -16777216 }, null);
    BufferedImage localBufferedImage = new BufferedImage(localDirectColorModel, localWritableRaster, false, null);
    Graphics localGraphics = localBufferedImage.getGraphics();
    localGraphics.drawImage(paramImage, 0, 0, paramInt1, paramInt2, null);
    localGraphics.dispose();
    int[] arrayOfInt1 = localDataBufferInt.getData();
    int[] arrayOfInt2 = new int[paramInt1 * paramInt2 + 2];
    arrayOfInt2[0] = paramInt1;
    arrayOfInt2[1] = paramInt2;
    System.arraycopy(arrayOfInt1, 0, arrayOfInt2, 2, paramInt1 * paramInt2);
    return arrayOfInt2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\IconInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */