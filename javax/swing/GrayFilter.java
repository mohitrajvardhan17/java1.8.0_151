package javax.swing;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.RGBImageFilter;

public class GrayFilter
  extends RGBImageFilter
{
  private boolean brighter;
  private int percent;
  
  public static Image createDisabledImage(Image paramImage)
  {
    GrayFilter localGrayFilter = new GrayFilter(true, 50);
    FilteredImageSource localFilteredImageSource = new FilteredImageSource(paramImage.getSource(), localGrayFilter);
    Image localImage = Toolkit.getDefaultToolkit().createImage(localFilteredImageSource);
    return localImage;
  }
  
  public GrayFilter(boolean paramBoolean, int paramInt)
  {
    brighter = paramBoolean;
    percent = paramInt;
    canFilterIndexColorModel = true;
  }
  
  public int filterRGB(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = (int)((0.3D * (paramInt3 >> 16 & 0xFF) + 0.59D * (paramInt3 >> 8 & 0xFF) + 0.11D * (paramInt3 & 0xFF)) / 3.0D);
    if (brighter) {
      i = 255 - (255 - i) * (100 - percent) / 100;
    } else {
      i = i * (100 - percent) / 100;
    }
    if (i < 0) {
      i = 0;
    }
    if (i > 255) {
      i = 255;
    }
    return paramInt3 & 0xFF000000 | i << 16 | i << 8 | i << 0;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\GrayFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */