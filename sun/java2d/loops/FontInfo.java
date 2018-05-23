package sun.java2d.loops;

import java.awt.Font;
import sun.font.Font2D;
import sun.font.FontStrike;

public class FontInfo
  implements Cloneable
{
  public Font font;
  public Font2D font2D;
  public FontStrike fontStrike;
  public double[] devTx;
  public double[] glyphTx;
  public int pixelHeight;
  public float originX;
  public float originY;
  public int aaHint;
  public boolean lcdRGBOrder;
  public boolean lcdSubPixPos;
  
  public FontInfo() {}
  
  public String mtx(double[] paramArrayOfDouble)
  {
    return "[" + paramArrayOfDouble[0] + ", " + paramArrayOfDouble[1] + ", " + paramArrayOfDouble[2] + ", " + paramArrayOfDouble[3] + "]";
  }
  
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    return null;
  }
  
  public String toString()
  {
    return "FontInfo[font=" + font + ", devTx=" + mtx(devTx) + ", glyphTx=" + mtx(glyphTx) + ", pixelHeight=" + pixelHeight + ", origin=(" + originX + "," + originY + "), aaHint=" + aaHint + ", lcdRGBOrder=" + (lcdRGBOrder ? "RGB" : "BGR") + "lcdSubPixPos=" + lcdSubPixPos + "]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\loops\FontInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */