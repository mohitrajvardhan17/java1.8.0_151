package sun.awt.windows;

import sun.awt.PlatformFont;

final class WFontPeer
  extends PlatformFont
{
  private String textComponentFontName;
  
  public WFontPeer(String paramString, int paramInt)
  {
    super(paramString, paramInt);
    if (fontConfig != null) {
      textComponentFontName = ((WFontConfiguration)fontConfig).getTextComponentFontName(familyName, paramInt);
    }
  }
  
  protected char getMissingGlyphCharacter()
  {
    return '❑';
  }
  
  private static native void initIDs();
  
  static {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WFontPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */