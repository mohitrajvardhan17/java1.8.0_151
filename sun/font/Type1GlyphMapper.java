package sun.font;

public final class Type1GlyphMapper
  extends CharToGlyphMapper
{
  Type1Font font;
  FontScaler scaler;
  
  public Type1GlyphMapper(Type1Font paramType1Font)
  {
    font = paramType1Font;
    initMapper();
  }
  
  private void initMapper()
  {
    scaler = font.getScaler();
    try
    {
      missingGlyph = scaler.getMissingGlyphCode();
    }
    catch (FontScalerException localFontScalerException1)
    {
      scaler = FontScaler.getNullScaler();
      try
      {
        missingGlyph = scaler.getMissingGlyphCode();
      }
      catch (FontScalerException localFontScalerException2)
      {
        missingGlyph = 0;
      }
    }
  }
  
  public int getNumGlyphs()
  {
    try
    {
      return scaler.getNumGlyphs();
    }
    catch (FontScalerException localFontScalerException)
    {
      scaler = FontScaler.getNullScaler();
    }
    return getNumGlyphs();
  }
  
  public int getMissingGlyphCode()
  {
    return missingGlyph;
  }
  
  public boolean canDisplay(char paramChar)
  {
    try
    {
      return scaler.getGlyphCode(paramChar) != missingGlyph;
    }
    catch (FontScalerException localFontScalerException)
    {
      scaler = FontScaler.getNullScaler();
    }
    return canDisplay(paramChar);
  }
  
  public int charToGlyph(char paramChar)
  {
    try
    {
      return scaler.getGlyphCode(paramChar);
    }
    catch (FontScalerException localFontScalerException)
    {
      scaler = FontScaler.getNullScaler();
    }
    return charToGlyph(paramChar);
  }
  
  public int charToGlyph(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 65535)) {
      return missingGlyph;
    }
    try
    {
      return scaler.getGlyphCode((char)paramInt);
    }
    catch (FontScalerException localFontScalerException)
    {
      scaler = FontScaler.getNullScaler();
    }
    return charToGlyph(paramInt);
  }
  
  public void charsToGlyphs(int paramInt, char[] paramArrayOfChar, int[] paramArrayOfInt)
  {
    for (int i = 0; i < paramInt; i++)
    {
      int j = paramArrayOfChar[i];
      if ((j >= 55296) && (j <= 56319) && (i < paramInt - 1))
      {
        int k = paramArrayOfChar[(i + 1)];
        if ((k >= 56320) && (k <= 57343))
        {
          j = (j - 55296) * 1024 + k - 56320 + 65536;
          paramArrayOfInt[(i + 1)] = 65535;
        }
      }
      paramArrayOfInt[i] = charToGlyph(j);
      if (j >= 65536) {
        i++;
      }
    }
  }
  
  public void charsToGlyphs(int paramInt, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    for (int i = 0; i < paramInt; i++) {
      paramArrayOfInt2[i] = charToGlyph(paramArrayOfInt1[i]);
    }
  }
  
  public boolean charsToGlyphsNS(int paramInt, char[] paramArrayOfChar, int[] paramArrayOfInt)
  {
    for (int i = 0; i < paramInt; i++)
    {
      int j = paramArrayOfChar[i];
      if ((j >= 55296) && (j <= 56319) && (i < paramInt - 1))
      {
        int k = paramArrayOfChar[(i + 1)];
        if ((k >= 56320) && (k <= 57343))
        {
          j = (j - 55296) * 1024 + k - 56320 + 65536;
          paramArrayOfInt[(i + 1)] = 65535;
        }
      }
      paramArrayOfInt[i] = charToGlyph(j);
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
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\Type1GlyphMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */