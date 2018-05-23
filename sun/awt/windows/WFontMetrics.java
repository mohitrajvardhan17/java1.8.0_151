package sun.awt.windows;

import java.awt.Font;
import java.awt.FontMetrics;
import java.util.Hashtable;

final class WFontMetrics
  extends FontMetrics
{
  int[] widths;
  int ascent;
  int descent;
  int leading;
  int height;
  int maxAscent;
  int maxDescent;
  int maxHeight;
  int maxAdvance;
  static Hashtable table = new Hashtable();
  
  public WFontMetrics(Font paramFont)
  {
    super(paramFont);
    init();
  }
  
  public int getLeading()
  {
    return leading;
  }
  
  public int getAscent()
  {
    return ascent;
  }
  
  public int getDescent()
  {
    return descent;
  }
  
  public int getHeight()
  {
    return height;
  }
  
  public int getMaxAscent()
  {
    return maxAscent;
  }
  
  public int getMaxDescent()
  {
    return maxDescent;
  }
  
  public int getMaxAdvance()
  {
    return maxAdvance;
  }
  
  public native int stringWidth(String paramString);
  
  public native int charsWidth(char[] paramArrayOfChar, int paramInt1, int paramInt2);
  
  public native int bytesWidth(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  public int[] getWidths()
  {
    return widths;
  }
  
  native void init();
  
  static FontMetrics getFontMetrics(Font paramFont)
  {
    Object localObject = (FontMetrics)table.get(paramFont);
    if (localObject == null) {
      table.put(paramFont, localObject = new WFontMetrics(paramFont));
    }
    return (FontMetrics)localObject;
  }
  
  private static native void initIDs();
  
  static {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WFontMetrics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */