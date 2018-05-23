package javax.swing;

import java.awt.Color;
import java.awt.image.RGBImageFilter;

class DebugGraphicsFilter
  extends RGBImageFilter
{
  Color color;
  
  DebugGraphicsFilter(Color paramColor)
  {
    canFilterIndexColorModel = true;
    color = paramColor;
  }
  
  public int filterRGB(int paramInt1, int paramInt2, int paramInt3)
  {
    return color.getRGB() | paramInt3 & 0xFF000000;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\DebugGraphicsFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */