package sun.font;

import java.security.AccessController;
import sun.security.action.GetPropertyAction;

public final class CompositeFont
  extends Font2D
{
  private boolean[] deferredInitialisation;
  String[] componentFileNames;
  String[] componentNames;
  private PhysicalFont[] components;
  int numSlots;
  int numMetricsSlots;
  int[] exclusionRanges;
  int[] maxIndices;
  int numGlyphs = 0;
  int localeSlot = -1;
  boolean isStdComposite = true;
  
  public CompositeFont(String paramString, String[] paramArrayOfString1, String[] paramArrayOfString2, int paramInt, int[] paramArrayOfInt1, int[] paramArrayOfInt2, boolean paramBoolean, SunFontManager paramSunFontManager)
  {
    handle = new Font2DHandle(this);
    fullName = paramString;
    componentFileNames = paramArrayOfString1;
    componentNames = paramArrayOfString2;
    if (paramArrayOfString2 == null) {
      numSlots = componentFileNames.length;
    } else {
      numSlots = componentNames.length;
    }
    numSlots = (numSlots <= 254 ? numSlots : 254);
    numMetricsSlots = paramInt;
    exclusionRanges = paramArrayOfInt1;
    maxIndices = paramArrayOfInt2;
    if (paramSunFontManager.getEUDCFont() != null)
    {
      numSlots += 1;
      if (componentNames != null)
      {
        componentNames = new String[numSlots];
        System.arraycopy(paramArrayOfString2, 0, componentNames, 0, numSlots - 1);
        componentNames[(numSlots - 1)] = paramSunFontManager.getEUDCFont().getFontName(null);
      }
      if (componentFileNames != null)
      {
        componentFileNames = new String[numSlots];
        System.arraycopy(paramArrayOfString1, 0, componentFileNames, 0, numSlots - 1);
      }
      components = new PhysicalFont[numSlots];
      components[(numSlots - 1)] = paramSunFontManager.getEUDCFont();
      deferredInitialisation = new boolean[numSlots];
      if (paramBoolean) {
        for (i = 0; i < numSlots - 1; i++) {
          deferredInitialisation[i] = true;
        }
      }
    }
    else
    {
      components = new PhysicalFont[numSlots];
      deferredInitialisation = new boolean[numSlots];
      if (paramBoolean) {
        for (i = 0; i < numSlots; i++) {
          deferredInitialisation[i] = true;
        }
      }
    }
    fontRank = 2;
    int i = fullName.indexOf('.');
    if (i > 0)
    {
      familyName = fullName.substring(0, i);
      if (i + 1 < fullName.length())
      {
        String str = fullName.substring(i + 1);
        if ("plain".equals(str)) {
          style = 0;
        } else if ("bold".equals(str)) {
          style = 1;
        } else if ("italic".equals(str)) {
          style = 2;
        } else if ("bolditalic".equals(str)) {
          style = 3;
        }
      }
    }
    else
    {
      familyName = fullName;
    }
  }
  
  CompositeFont(PhysicalFont paramPhysicalFont, CompositeFont paramCompositeFont)
  {
    isStdComposite = false;
    handle = new Font2DHandle(this);
    fullName = fullName;
    familyName = familyName;
    style = style;
    numMetricsSlots = 1;
    numSlots += 1;
    synchronized (FontManagerFactory.getInstance())
    {
      components = new PhysicalFont[numSlots];
      components[0] = paramPhysicalFont;
      System.arraycopy(components, 0, components, 1, numSlots);
      if (componentNames != null)
      {
        componentNames = new String[numSlots];
        componentNames[0] = fullName;
        System.arraycopy(componentNames, 0, componentNames, 1, numSlots);
      }
      if (componentFileNames != null)
      {
        componentFileNames = new String[numSlots];
        componentFileNames[0] = null;
        System.arraycopy(componentFileNames, 0, componentFileNames, 1, numSlots);
      }
      deferredInitialisation = new boolean[numSlots];
      deferredInitialisation[0] = false;
      System.arraycopy(deferredInitialisation, 0, deferredInitialisation, 1, numSlots);
    }
  }
  
  private void doDeferredInitialisation(int paramInt)
  {
    if (deferredInitialisation[paramInt] == 0) {
      return;
    }
    SunFontManager localSunFontManager = SunFontManager.getInstance();
    synchronized (localSunFontManager)
    {
      if (componentNames == null) {
        componentNames = new String[numSlots];
      }
      if (components[paramInt] == null)
      {
        if ((componentFileNames != null) && (componentFileNames[paramInt] != null)) {
          components[paramInt] = localSunFontManager.initialiseDeferredFont(componentFileNames[paramInt]);
        }
        if (components[paramInt] == null) {
          components[paramInt] = localSunFontManager.getDefaultPhysicalFont();
        }
        String str = components[paramInt].getFontName(null);
        if (componentNames[paramInt] == null) {
          componentNames[paramInt] = str;
        } else if (!componentNames[paramInt].equalsIgnoreCase(str)) {
          try
          {
            components[paramInt] = ((PhysicalFont)localSunFontManager.findFont2D(componentNames[paramInt], style, 1));
          }
          catch (ClassCastException localClassCastException)
          {
            components[paramInt] = localSunFontManager.getDefaultPhysicalFont();
          }
        }
      }
      deferredInitialisation[paramInt] = false;
    }
  }
  
  void replaceComponentFont(PhysicalFont paramPhysicalFont1, PhysicalFont paramPhysicalFont2)
  {
    if (components == null) {
      return;
    }
    for (int i = 0; i < numSlots; i++) {
      if (components[i] == paramPhysicalFont1)
      {
        components[i] = paramPhysicalFont2;
        if (componentNames != null) {
          componentNames[i] = paramPhysicalFont2.getFontName(null);
        }
      }
    }
  }
  
  public boolean isExcludedChar(int paramInt1, int paramInt2)
  {
    if ((exclusionRanges == null) || (maxIndices == null) || (paramInt1 >= numMetricsSlots)) {
      return false;
    }
    int i = 0;
    int j = maxIndices[paramInt1];
    if (paramInt1 > 0) {
      i = maxIndices[(paramInt1 - 1)];
    }
    for (int k = i; j > k; k += 2) {
      if ((paramInt2 >= exclusionRanges[k]) && (paramInt2 <= exclusionRanges[(k + 1)])) {
        return true;
      }
    }
    return false;
  }
  
  public void getStyleMetrics(float paramFloat, float[] paramArrayOfFloat, int paramInt)
  {
    PhysicalFont localPhysicalFont = getSlotFont(0);
    if (localPhysicalFont == null) {
      super.getStyleMetrics(paramFloat, paramArrayOfFloat, paramInt);
    } else {
      localPhysicalFont.getStyleMetrics(paramFloat, paramArrayOfFloat, paramInt);
    }
  }
  
  public int getNumSlots()
  {
    return numSlots;
  }
  
  public PhysicalFont getSlotFont(int paramInt)
  {
    if (deferredInitialisation[paramInt] != 0) {
      doDeferredInitialisation(paramInt);
    }
    SunFontManager localSunFontManager = SunFontManager.getInstance();
    try
    {
      PhysicalFont localPhysicalFont = components[paramInt];
      if (localPhysicalFont == null) {
        try
        {
          localPhysicalFont = (PhysicalFont)localSunFontManager.findFont2D(componentNames[paramInt], style, 1);
          components[paramInt] = localPhysicalFont;
        }
        catch (ClassCastException localClassCastException)
        {
          localPhysicalFont = localSunFontManager.getDefaultPhysicalFont();
        }
      }
      return localPhysicalFont;
    }
    catch (Exception localException) {}
    return localSunFontManager.getDefaultPhysicalFont();
  }
  
  FontStrike createStrike(FontStrikeDesc paramFontStrikeDesc)
  {
    return new CompositeStrike(this, paramFontStrikeDesc);
  }
  
  public boolean isStdComposite()
  {
    return isStdComposite;
  }
  
  protected int getValidatedGlyphCode(int paramInt)
  {
    int i = paramInt >>> 24;
    if (i >= numSlots) {
      return getMapper().getMissingGlyphCode();
    }
    int j = paramInt & 0xFFFFFF;
    PhysicalFont localPhysicalFont = getSlotFont(i);
    if (localPhysicalFont.getValidatedGlyphCode(j) == localPhysicalFont.getMissingGlyphCode()) {
      return getMapper().getMissingGlyphCode();
    }
    return paramInt;
  }
  
  public CharToGlyphMapper getMapper()
  {
    if (mapper == null) {
      mapper = new CompositeGlyphMapper(this);
    }
    return mapper;
  }
  
  public boolean hasSupplementaryChars()
  {
    for (int i = 0; i < numSlots; i++) {
      if (getSlotFont(i).hasSupplementaryChars()) {
        return true;
      }
    }
    return false;
  }
  
  public int getNumGlyphs()
  {
    if (numGlyphs == 0) {
      numGlyphs = getMapper().getNumGlyphs();
    }
    return numGlyphs;
  }
  
  public int getMissingGlyphCode()
  {
    return getMapper().getMissingGlyphCode();
  }
  
  public boolean canDisplay(char paramChar)
  {
    return getMapper().canDisplay(paramChar);
  }
  
  public boolean useAAForPtSize(int paramInt)
  {
    if (localeSlot == -1)
    {
      int i = numMetricsSlots;
      if ((i == 1) && (!isStdComposite())) {
        i = numSlots;
      }
      for (int j = 0; j < i; j++) {
        if (getSlotFont(j).supportsEncoding(null))
        {
          localeSlot = j;
          break;
        }
      }
      if (localeSlot == -1) {
        localeSlot = 0;
      }
    }
    return getSlotFont(localeSlot).useAAForPtSize(paramInt);
  }
  
  public String toString()
  {
    String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("line.separator"));
    String str2 = "";
    for (int i = 0; i < numSlots; i++) {
      str2 = str2 + "    Slot[" + i + "]=" + getSlotFont(i) + str1;
    }
    return "** Composite Font: Family=" + familyName + " Name=" + fullName + " style=" + style + str1 + str2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\CompositeFont.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */