package sun.awt.windows;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import sun.awt.AWTCharset;
import sun.awt.AWTCharset.Encoder;

final class WDefaultFontCharset
  extends AWTCharset
{
  private String fontName;
  
  WDefaultFontCharset(String paramString)
  {
    super("WDefaultFontCharset", Charset.forName("windows-1252"));
    fontName = paramString;
  }
  
  public CharsetEncoder newEncoder()
  {
    return new Encoder(null);
  }
  
  private synchronized native boolean canConvert(char paramChar);
  
  private static native void initIDs();
  
  static {}
  
  private class Encoder
    extends AWTCharset.Encoder
  {
    private Encoder()
    {
      super();
    }
    
    public boolean canEncode(char paramChar)
    {
      return WDefaultFontCharset.this.canConvert(paramChar);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WDefaultFontCharset.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */