package sun.font;

public final class CompositeGlyphMapper
  extends CharToGlyphMapper
{
  public static final int SLOTMASK = -16777216;
  public static final int GLYPHMASK = 16777215;
  public static final int NBLOCKS = 216;
  public static final int BLOCKSZ = 256;
  public static final int MAXUNICODE = 55296;
  CompositeFont font;
  CharToGlyphMapper[] slotMappers;
  int[][] glyphMaps;
  private boolean hasExcludes;
  
  public CompositeGlyphMapper(CompositeFont paramCompositeFont)
  {
    font = paramCompositeFont;
    initMapper();
    hasExcludes = ((exclusionRanges != null) && (maxIndices != null));
  }
  
  public final int compositeGlyphCode(int paramInt1, int paramInt2)
  {
    return paramInt1 << 24 | paramInt2 & 0xFFFFFF;
  }
  
  private final void initMapper()
  {
    if (missingGlyph == -1)
    {
      if (glyphMaps == null) {
        glyphMaps = new int['Ø'][];
      }
      slotMappers = new CharToGlyphMapper[font.numSlots];
      missingGlyph = font.getSlotFont(0).getMissingGlyphCode();
      missingGlyph = compositeGlyphCode(0, missingGlyph);
    }
  }
  
  private int getCachedGlyphCode(int paramInt)
  {
    if (paramInt >= 55296) {
      return -1;
    }
    int[] arrayOfInt;
    if ((arrayOfInt = glyphMaps[(paramInt >> 8)]) == null) {
      return -1;
    }
    return arrayOfInt[(paramInt & 0xFF)];
  }
  
  private void setCachedGlyphCode(int paramInt1, int paramInt2)
  {
    if (paramInt1 >= 55296) {
      return;
    }
    int i = paramInt1 >> 8;
    if (glyphMaps[i] == null)
    {
      glyphMaps[i] = new int['Ā'];
      for (int j = 0; j < 256; j++) {
        glyphMaps[i][j] = -1;
      }
    }
    glyphMaps[i][(paramInt1 & 0xFF)] = paramInt2;
  }
  
  private final CharToGlyphMapper getSlotMapper(int paramInt)
  {
    CharToGlyphMapper localCharToGlyphMapper = slotMappers[paramInt];
    if (localCharToGlyphMapper == null)
    {
      localCharToGlyphMapper = font.getSlotFont(paramInt).getMapper();
      slotMappers[paramInt] = localCharToGlyphMapper;
    }
    return localCharToGlyphMapper;
  }
  
  private final int convertToGlyph(int paramInt)
  {
    for (int i = 0; i < font.numSlots; i++) {
      if ((!hasExcludes) || (!font.isExcludedChar(i, paramInt)))
      {
        CharToGlyphMapper localCharToGlyphMapper = getSlotMapper(i);
        int j = localCharToGlyphMapper.charToGlyph(paramInt);
        if (j != localCharToGlyphMapper.getMissingGlyphCode())
        {
          j = compositeGlyphCode(i, j);
          setCachedGlyphCode(paramInt, j);
          return j;
        }
      }
    }
    return missingGlyph;
  }
  
  public int getNumGlyphs()
  {
    int i = 0;
    for (int j = 0; j < 1; j++)
    {
      CharToGlyphMapper localCharToGlyphMapper = slotMappers[j];
      if (localCharToGlyphMapper == null)
      {
        localCharToGlyphMapper = font.getSlotFont(j).getMapper();
        slotMappers[j] = localCharToGlyphMapper;
      }
      i += localCharToGlyphMapper.getNumGlyphs();
    }
    return i;
  }
  
  public int charToGlyph(int paramInt)
  {
    int i = getCachedGlyphCode(paramInt);
    if (i == -1) {
      i = convertToGlyph(paramInt);
    }
    return i;
  }
  
  public int charToGlyph(int paramInt1, int paramInt2)
  {
    if (paramInt2 >= 0)
    {
      CharToGlyphMapper localCharToGlyphMapper = getSlotMapper(paramInt2);
      int i = localCharToGlyphMapper.charToGlyph(paramInt1);
      if (i != localCharToGlyphMapper.getMissingGlyphCode()) {
        return compositeGlyphCode(paramInt2, i);
      }
    }
    return charToGlyph(paramInt1);
  }
  
  public int charToGlyph(char paramChar)
  {
    int i = getCachedGlyphCode(paramChar);
    if (i == -1) {
      i = convertToGlyph(paramChar);
    }
    return i;
  }
  
  public boolean charsToGlyphsNS(int paramInt, char[] paramArrayOfChar, int[] paramArrayOfInt)
  {
    for (int i = 0; i < paramInt; i++)
    {
      int j = paramArrayOfChar[i];
      if ((j >= 55296) && (j <= 56319) && (i < paramInt - 1))
      {
        k = paramArrayOfChar[(i + 1)];
        if ((k >= 56320) && (k <= 57343))
        {
          j = (j - 55296) * 1024 + k - 56320 + 65536;
          paramArrayOfInt[(i + 1)] = 65535;
        }
      }
      int k = paramArrayOfInt[i] = getCachedGlyphCode(j);
      if (k == -1) {
        paramArrayOfInt[i] = convertToGlyph(j);
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
  
  public void charsToGlyphs(int paramInt, char[] paramArrayOfChar, int[] paramArrayOfInt)
  {
    for (int i = 0; i < paramInt; i++)
    {
      int j = paramArrayOfChar[i];
      if ((j >= 55296) && (j <= 56319) && (i < paramInt - 1))
      {
        k = paramArrayOfChar[(i + 1)];
        if ((k >= 56320) && (k <= 57343))
        {
          j = (j - 55296) * 1024 + k - 56320 + 65536;
          int m = paramArrayOfInt[i] = getCachedGlyphCode(j);
          if (m == -1) {
            paramArrayOfInt[i] = convertToGlyph(j);
          }
          i++;
          paramArrayOfInt[i] = 65535;
          continue;
        }
      }
      int k = paramArrayOfInt[i] = getCachedGlyphCode(j);
      if (k == -1) {
        paramArrayOfInt[i] = convertToGlyph(j);
      }
    }
  }
  
  public void charsToGlyphs(int paramInt, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    for (int i = 0; i < paramInt; i++)
    {
      int j = paramArrayOfInt1[i];
      paramArrayOfInt2[i] = getCachedGlyphCode(j);
      if (paramArrayOfInt2[i] == -1) {
        paramArrayOfInt2[i] = convertToGlyph(j);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\CompositeGlyphMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */