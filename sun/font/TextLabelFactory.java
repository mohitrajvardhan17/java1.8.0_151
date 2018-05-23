package sun.font;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.text.Bidi;

public final class TextLabelFactory
{
  private final FontRenderContext frc;
  private final char[] text;
  private final Bidi bidi;
  private Bidi lineBidi;
  private final int flags;
  private int lineStart;
  private int lineLimit;
  
  public TextLabelFactory(FontRenderContext paramFontRenderContext, char[] paramArrayOfChar, Bidi paramBidi, int paramInt)
  {
    frc = paramFontRenderContext;
    text = ((char[])paramArrayOfChar.clone());
    bidi = paramBidi;
    flags = paramInt;
    lineBidi = paramBidi;
    lineStart = 0;
    lineLimit = paramArrayOfChar.length;
  }
  
  public FontRenderContext getFontRenderContext()
  {
    return frc;
  }
  
  public Bidi getLineBidi()
  {
    return lineBidi;
  }
  
  public void setLineContext(int paramInt1, int paramInt2)
  {
    lineStart = paramInt1;
    lineLimit = paramInt2;
    if (bidi != null) {
      lineBidi = bidi.createLineBidi(paramInt1, paramInt2);
    }
  }
  
  public ExtendedTextLabel createExtended(Font paramFont, CoreMetrics paramCoreMetrics, Decoration paramDecoration, int paramInt1, int paramInt2)
  {
    if ((paramInt1 >= paramInt2) || (paramInt1 < lineStart) || (paramInt2 > lineLimit)) {
      throw new IllegalArgumentException("bad start: " + paramInt1 + " or limit: " + paramInt2);
    }
    int i = lineBidi == null ? 0 : lineBidi.getLevelAt(paramInt1 - lineStart);
    int j = (lineBidi == null) || (lineBidi.baseIsLeftToRight()) ? 0 : 1;
    int k = flags & 0xFFFFFFF6;
    if ((i & 0x1) != 0) {
      k |= 0x1;
    }
    if ((j & 0x1) != 0) {
      k |= 0x8;
    }
    StandardTextSource localStandardTextSource = new StandardTextSource(text, paramInt1, paramInt2 - paramInt1, lineStart, lineLimit - lineStart, i, k, paramFont, frc, paramCoreMetrics);
    return new ExtendedTextSourceLabel(localStandardTextSource, paramDecoration);
  }
  
  public TextLabel createSimple(Font paramFont, CoreMetrics paramCoreMetrics, int paramInt1, int paramInt2)
  {
    if ((paramInt1 >= paramInt2) || (paramInt1 < lineStart) || (paramInt2 > lineLimit)) {
      throw new IllegalArgumentException("bad start: " + paramInt1 + " or limit: " + paramInt2);
    }
    int i = lineBidi == null ? 0 : lineBidi.getLevelAt(paramInt1 - lineStart);
    int j = (lineBidi == null) || (lineBidi.baseIsLeftToRight()) ? 0 : 1;
    int k = flags & 0xFFFFFFF6;
    if ((i & 0x1) != 0) {
      k |= 0x1;
    }
    if ((j & 0x1) != 0) {
      k |= 0x8;
    }
    StandardTextSource localStandardTextSource = new StandardTextSource(text, paramInt1, paramInt2 - paramInt1, lineStart, lineLimit - lineStart, i, k, paramFont, frc, paramCoreMetrics);
    return new TextSourceLabel(localStandardTextSource);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\TextLabelFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */