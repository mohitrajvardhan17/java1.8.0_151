package sun.font;

import java.awt.Font;

public abstract class FontAccess
{
  private static FontAccess access;
  
  public FontAccess() {}
  
  public static synchronized void setFontAccess(FontAccess paramFontAccess)
  {
    if (access != null) {
      throw new InternalError("Attempt to set FontAccessor twice");
    }
    access = paramFontAccess;
  }
  
  public static synchronized FontAccess getFontAccess()
  {
    return access;
  }
  
  public abstract Font2D getFont2D(Font paramFont);
  
  public abstract void setFont2D(Font paramFont, Font2DHandle paramFont2DHandle);
  
  public abstract void setCreatedFont(Font paramFont);
  
  public abstract boolean isCreatedFont(Font paramFont);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\FontAccess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */