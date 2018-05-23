package sun.font;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;

final class StandardTextSource
  extends TextSource
{
  private final char[] chars;
  private final int start;
  private final int len;
  private final int cstart;
  private final int clen;
  private final int level;
  private final int flags;
  private final Font font;
  private final FontRenderContext frc;
  private final CoreMetrics cm;
  
  StandardTextSource(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, Font paramFont, FontRenderContext paramFontRenderContext, CoreMetrics paramCoreMetrics)
  {
    if (paramArrayOfChar == null) {
      throw new IllegalArgumentException("bad chars: null");
    }
    if (paramInt3 < 0) {
      throw new IllegalArgumentException("bad cstart: " + paramInt3);
    }
    if (paramInt1 < paramInt3) {
      throw new IllegalArgumentException("bad start: " + paramInt1 + " for cstart: " + paramInt3);
    }
    if (paramInt4 < 0) {
      throw new IllegalArgumentException("bad clen: " + paramInt4);
    }
    if (paramInt3 + paramInt4 > paramArrayOfChar.length) {
      throw new IllegalArgumentException("bad clen: " + paramInt4 + " cstart: " + paramInt3 + " for array len: " + paramArrayOfChar.length);
    }
    if (paramInt2 < 0) {
      throw new IllegalArgumentException("bad len: " + paramInt2);
    }
    if (paramInt1 + paramInt2 > paramInt3 + paramInt4) {
      throw new IllegalArgumentException("bad len: " + paramInt2 + " start: " + paramInt1 + " for cstart: " + paramInt3 + " clen: " + paramInt4);
    }
    if (paramFont == null) {
      throw new IllegalArgumentException("bad font: null");
    }
    if (paramFontRenderContext == null) {
      throw new IllegalArgumentException("bad frc: null");
    }
    chars = paramArrayOfChar;
    start = paramInt1;
    len = paramInt2;
    cstart = paramInt3;
    clen = paramInt4;
    level = paramInt5;
    flags = paramInt6;
    font = paramFont;
    frc = paramFontRenderContext;
    if (paramCoreMetrics != null)
    {
      cm = paramCoreMetrics;
    }
    else
    {
      LineMetrics localLineMetrics = paramFont.getLineMetrics(paramArrayOfChar, paramInt3, paramInt4, paramFontRenderContext);
      cm = cm;
    }
  }
  
  public char[] getChars()
  {
    return chars;
  }
  
  public int getStart()
  {
    return start;
  }
  
  public int getLength()
  {
    return len;
  }
  
  public int getContextStart()
  {
    return cstart;
  }
  
  public int getContextLength()
  {
    return clen;
  }
  
  public int getLayoutFlags()
  {
    return flags;
  }
  
  public int getBidiLevel()
  {
    return level;
  }
  
  public Font getFont()
  {
    return font;
  }
  
  public FontRenderContext getFRC()
  {
    return frc;
  }
  
  public CoreMetrics getCoreMetrics()
  {
    return cm;
  }
  
  public TextSource getSubSource(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt2 > len)) {
      throw new IllegalArgumentException("bad start (" + paramInt1 + ") or length (" + paramInt2 + ")");
    }
    int i = level;
    if (paramInt3 != 2)
    {
      int j = (flags & 0x8) == 0 ? 1 : 0;
      if (((paramInt3 != 0) || (j == 0)) && ((paramInt3 != 1) || (j != 0))) {
        throw new IllegalArgumentException("direction flag is invalid");
      }
      i = j != 0 ? 0 : 1;
    }
    return new StandardTextSource(chars, start + paramInt1, paramInt2, cstart, clen, i, flags, font, frc, cm);
  }
  
  public String toString()
  {
    return toString(true);
  }
  
  public String toString(boolean paramBoolean)
  {
    StringBuffer localStringBuffer = new StringBuffer(super.toString());
    localStringBuffer.append("[start:");
    localStringBuffer.append(start);
    localStringBuffer.append(", len:");
    localStringBuffer.append(len);
    localStringBuffer.append(", cstart:");
    localStringBuffer.append(cstart);
    localStringBuffer.append(", clen:");
    localStringBuffer.append(clen);
    localStringBuffer.append(", chars:\"");
    int i;
    int j;
    if (paramBoolean == true)
    {
      i = cstart;
      j = cstart + clen;
    }
    else
    {
      i = start;
      j = start + len;
    }
    for (int k = i; k < j; k++)
    {
      if (k > i) {
        localStringBuffer.append(" ");
      }
      localStringBuffer.append(Integer.toHexString(chars[k]));
    }
    localStringBuffer.append("\"");
    localStringBuffer.append(", level:");
    localStringBuffer.append(level);
    localStringBuffer.append(", flags:");
    localStringBuffer.append(flags);
    localStringBuffer.append(", font:");
    localStringBuffer.append(font);
    localStringBuffer.append(", frc:");
    localStringBuffer.append(frc);
    localStringBuffer.append(", cm:");
    localStringBuffer.append(cm);
    localStringBuffer.append("]");
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\StandardTextSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */