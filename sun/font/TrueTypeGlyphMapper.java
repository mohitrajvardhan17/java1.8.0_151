package sun.font;

import java.nio.ByteBuffer;
import java.util.Locale;
import sun.util.logging.PlatformLogger;

public class TrueTypeGlyphMapper
  extends CharToGlyphMapper
{
  static final char REVERSE_SOLIDUS = '\\';
  static final char JA_YEN = '¥';
  static final char JA_FULLWIDTH_TILDE_CHAR = '～';
  static final char JA_WAVE_DASH_CHAR = '〜';
  static final boolean isJAlocale = Locale.JAPAN.equals(Locale.getDefault());
  private final boolean needsJAremapping;
  private boolean remapJAWaveDash;
  TrueTypeFont font;
  CMap cmap;
  int numGlyphs;
  
  public TrueTypeGlyphMapper(TrueTypeFont paramTrueTypeFont)
  {
    font = paramTrueTypeFont;
    try
    {
      cmap = CMap.initialize(paramTrueTypeFont);
    }
    catch (Exception localException)
    {
      cmap = null;
    }
    if (cmap == null) {
      handleBadCMAP();
    }
    missingGlyph = 0;
    ByteBuffer localByteBuffer = paramTrueTypeFont.getTableBuffer(1835104368);
    if ((localByteBuffer != null) && (localByteBuffer.capacity() >= 6)) {
      numGlyphs = localByteBuffer.getChar(4);
    } else {
      handleBadCMAP();
    }
    if ((FontUtilities.isSolaris) && (isJAlocale) && (paramTrueTypeFont.supportsJA()))
    {
      needsJAremapping = true;
      if ((FontUtilities.isSolaris8) && (getGlyphFromCMAP(12316) == missingGlyph)) {
        remapJAWaveDash = true;
      }
    }
    else
    {
      needsJAremapping = false;
    }
  }
  
  public int getNumGlyphs()
  {
    return numGlyphs;
  }
  
  private char getGlyphFromCMAP(int paramInt)
  {
    try
    {
      int i = cmap.getGlyph(paramInt);
      if ((i < numGlyphs) || (i >= 65534)) {
        return i;
      }
      if (FontUtilities.isLogging()) {
        FontUtilities.getLogger().warning(font + " out of range glyph id=" + Integer.toHexString(i) + " for char " + Integer.toHexString(paramInt));
      }
      return (char)missingGlyph;
    }
    catch (Exception localException)
    {
      handleBadCMAP();
    }
    return (char)missingGlyph;
  }
  
  private void handleBadCMAP()
  {
    if (FontUtilities.isLogging()) {
      FontUtilities.getLogger().severe("Null Cmap for " + font + "substituting for this font");
    }
    SunFontManager.getInstance().deRegisterBadFont(font);
    cmap = CMap.theNullCmap;
  }
  
  private final char remapJAChar(char paramChar)
  {
    switch (paramChar)
    {
    case '\\': 
      return '¥';
    case '〜': 
      if (remapJAWaveDash) {
        return 65374;
      }
      break;
    }
    return paramChar;
  }
  
  private final int remapJAIntChar(int paramInt)
  {
    switch (paramInt)
    {
    case 92: 
      return 165;
    case 12316: 
      if (remapJAWaveDash) {
        return 65374;
      }
      break;
    }
    return paramInt;
  }
  
  public int charToGlyph(char paramChar)
  {
    if (needsJAremapping) {
      paramChar = remapJAChar(paramChar);
    }
    int i = getGlyphFromCMAP(paramChar);
    if ((font.checkUseNatives()) && (i < font.glyphToCharMap.length)) {
      font.glyphToCharMap[i] = paramChar;
    }
    return i;
  }
  
  public int charToGlyph(int paramInt)
  {
    if (needsJAremapping) {
      paramInt = remapJAIntChar(paramInt);
    }
    int i = getGlyphFromCMAP(paramInt);
    if ((font.checkUseNatives()) && (i < font.glyphToCharMap.length)) {
      font.glyphToCharMap[i] = ((char)paramInt);
    }
    return i;
  }
  
  public void charsToGlyphs(int paramInt, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    for (int i = 0; i < paramInt; i++)
    {
      if (needsJAremapping) {
        paramArrayOfInt2[i] = getGlyphFromCMAP(remapJAIntChar(paramArrayOfInt1[i]));
      } else {
        paramArrayOfInt2[i] = getGlyphFromCMAP(paramArrayOfInt1[i]);
      }
      if ((font.checkUseNatives()) && (paramArrayOfInt2[i] < font.glyphToCharMap.length)) {
        font.glyphToCharMap[paramArrayOfInt2[i]] = ((char)paramArrayOfInt1[i]);
      }
    }
  }
  
  public void charsToGlyphs(int paramInt, char[] paramArrayOfChar, int[] paramArrayOfInt)
  {
    for (int i = 0; i < paramInt; i++)
    {
      int j;
      if (needsJAremapping) {
        j = remapJAChar(paramArrayOfChar[i]);
      } else {
        j = paramArrayOfChar[i];
      }
      if ((j >= 55296) && (j <= 56319) && (i < paramInt - 1))
      {
        int k = paramArrayOfChar[(i + 1)];
        if ((k >= 56320) && (k <= 57343))
        {
          j = (j - 55296) * 1024 + k - 56320 + 65536;
          paramArrayOfInt[i] = getGlyphFromCMAP(j);
          i++;
          paramArrayOfInt[i] = 65535;
          continue;
        }
      }
      paramArrayOfInt[i] = getGlyphFromCMAP(j);
      if ((font.checkUseNatives()) && (paramArrayOfInt[i] < font.glyphToCharMap.length)) {
        font.glyphToCharMap[paramArrayOfInt[i]] = ((char)j);
      }
    }
  }
  
  public boolean charsToGlyphsNS(int paramInt, char[] paramArrayOfChar, int[] paramArrayOfInt)
  {
    for (int i = 0; i < paramInt; i++)
    {
      int j;
      if (needsJAremapping) {
        j = remapJAChar(paramArrayOfChar[i]);
      } else {
        j = paramArrayOfChar[i];
      }
      if ((j >= 55296) && (j <= 56319) && (i < paramInt - 1))
      {
        int k = paramArrayOfChar[(i + 1)];
        if ((k >= 56320) && (k <= 57343))
        {
          j = (j - 55296) * 1024 + k - 56320 + 65536;
          paramArrayOfInt[(i + 1)] = 65535;
        }
      }
      paramArrayOfInt[i] = getGlyphFromCMAP(j);
      if ((font.checkUseNatives()) && (paramArrayOfInt[i] < font.glyphToCharMap.length)) {
        font.glyphToCharMap[paramArrayOfInt[i]] = ((char)j);
      }
      if (j >= 768)
      {
        if (FontUtilities.isComplexCharCode(j)) {
          return true;
        }
        if (j >= 65536) {
          i++;
        }
      }
    }
    return false;
  }
  
  boolean hasSupplementaryChars()
  {
    return ((cmap instanceof CMap.CMapFormat8)) || ((cmap instanceof CMap.CMapFormat10)) || ((cmap instanceof CMap.CMapFormat12));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\TrueTypeGlyphMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */