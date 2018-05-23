package sun.font;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import sun.awt.SunHints;

public class FontStrikeDesc
{
  static final int AA_ON = 16;
  static final int AA_LCD_H = 32;
  static final int AA_LCD_V = 64;
  static final int FRAC_METRICS_ON = 256;
  static final int FRAC_METRICS_SP = 512;
  AffineTransform devTx;
  AffineTransform glyphTx;
  int style;
  int aaHint;
  int fmHint;
  private int hashCode;
  private int valuemask;
  
  public int hashCode()
  {
    if (hashCode == 0) {
      hashCode = (glyphTx.hashCode() + devTx.hashCode() + valuemask);
    }
    return hashCode;
  }
  
  public boolean equals(Object paramObject)
  {
    try
    {
      FontStrikeDesc localFontStrikeDesc = (FontStrikeDesc)paramObject;
      return (valuemask == valuemask) && (glyphTx.equals(glyphTx)) && (devTx.equals(devTx));
    }
    catch (Exception localException) {}
    return false;
  }
  
  FontStrikeDesc() {}
  
  public static int getAAHintIntVal(Object paramObject, Font2D paramFont2D, int paramInt)
  {
    if ((paramObject == SunHints.VALUE_TEXT_ANTIALIAS_OFF) || (paramObject == SunHints.VALUE_TEXT_ANTIALIAS_DEFAULT)) {
      return 1;
    }
    if (paramObject == SunHints.VALUE_TEXT_ANTIALIAS_ON) {
      return 2;
    }
    if (paramObject == SunHints.VALUE_TEXT_ANTIALIAS_GASP)
    {
      if (paramFont2D.useAAForPtSize(paramInt)) {
        return 2;
      }
      return 1;
    }
    if ((paramObject == SunHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB) || (paramObject == SunHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR)) {
      return 4;
    }
    if ((paramObject == SunHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB) || (paramObject == SunHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR)) {
      return 6;
    }
    return 1;
  }
  
  public static int getAAHintIntVal(Font2D paramFont2D, Font paramFont, FontRenderContext paramFontRenderContext)
  {
    Object localObject = paramFontRenderContext.getAntiAliasingHint();
    if ((localObject == SunHints.VALUE_TEXT_ANTIALIAS_OFF) || (localObject == SunHints.VALUE_TEXT_ANTIALIAS_DEFAULT)) {
      return 1;
    }
    if (localObject == SunHints.VALUE_TEXT_ANTIALIAS_ON) {
      return 2;
    }
    if (localObject == SunHints.VALUE_TEXT_ANTIALIAS_GASP)
    {
      AffineTransform localAffineTransform = paramFontRenderContext.getTransform();
      int i;
      if ((localAffineTransform.isIdentity()) && (!paramFont.isTransformed()))
      {
        i = paramFont.getSize();
      }
      else
      {
        float f = paramFont.getSize2D();
        if (localAffineTransform.isIdentity())
        {
          localAffineTransform = paramFont.getTransform();
          localAffineTransform.scale(f, f);
        }
        else
        {
          localAffineTransform.scale(f, f);
          if (paramFont.isTransformed()) {
            localAffineTransform.concatenate(paramFont.getTransform());
          }
        }
        double d1 = localAffineTransform.getShearX();
        double d2 = localAffineTransform.getScaleY();
        if (d1 != 0.0D) {
          d2 = Math.sqrt(d1 * d1 + d2 * d2);
        }
        i = (int)(Math.abs(d2) + 0.5D);
      }
      if (paramFont2D.useAAForPtSize(i)) {
        return 2;
      }
      return 1;
    }
    if ((localObject == SunHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB) || (localObject == SunHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR)) {
      return 4;
    }
    if ((localObject == SunHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB) || (localObject == SunHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR)) {
      return 6;
    }
    return 1;
  }
  
  public static int getFMHintIntVal(Object paramObject)
  {
    if ((paramObject == SunHints.VALUE_FRACTIONALMETRICS_OFF) || (paramObject == SunHints.VALUE_FRACTIONALMETRICS_DEFAULT)) {
      return 1;
    }
    return 2;
  }
  
  public FontStrikeDesc(AffineTransform paramAffineTransform1, AffineTransform paramAffineTransform2, int paramInt1, int paramInt2, int paramInt3)
  {
    devTx = paramAffineTransform1;
    glyphTx = paramAffineTransform2;
    style = paramInt1;
    aaHint = paramInt2;
    fmHint = paramInt3;
    valuemask = paramInt1;
    switch (paramInt2)
    {
    case 1: 
      break;
    case 2: 
      valuemask |= 0x10;
      break;
    case 4: 
    case 5: 
      valuemask |= 0x20;
      break;
    case 6: 
    case 7: 
      valuemask |= 0x40;
      break;
    }
    if (paramInt3 == 2) {
      valuemask |= 0x100;
    }
  }
  
  FontStrikeDesc(FontStrikeDesc paramFontStrikeDesc)
  {
    devTx = devTx;
    glyphTx = ((AffineTransform)glyphTx.clone());
    style = style;
    aaHint = aaHint;
    fmHint = fmHint;
    hashCode = hashCode;
    valuemask = valuemask;
  }
  
  public String toString()
  {
    return "FontStrikeDesc: Style=" + style + " AA=" + aaHint + " FM=" + fmHint + " devTx=" + devTx + " devTx.FontTx.ptSize=" + glyphTx;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\FontStrikeDesc.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */