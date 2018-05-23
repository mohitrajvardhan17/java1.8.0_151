package sun.font;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import sun.util.logging.PlatformLogger;

public class FontFamily
{
  private static ConcurrentHashMap<String, FontFamily> familyNameMap = new ConcurrentHashMap();
  private static HashMap<String, FontFamily> allLocaleNames;
  protected String familyName;
  protected Font2D plain;
  protected Font2D bold;
  protected Font2D italic;
  protected Font2D bolditalic;
  protected boolean logicalFont = false;
  protected int familyRank;
  private int familyWidth = 0;
  
  public static FontFamily getFamily(String paramString)
  {
    return (FontFamily)familyNameMap.get(paramString.toLowerCase(Locale.ENGLISH));
  }
  
  public static String[] getAllFamilyNames()
  {
    return null;
  }
  
  static void remove(Font2D paramFont2D)
  {
    String str = paramFont2D.getFamilyName(Locale.ENGLISH);
    FontFamily localFontFamily = getFamily(str);
    if (localFontFamily == null) {
      return;
    }
    if (plain == paramFont2D) {
      plain = null;
    }
    if (bold == paramFont2D) {
      bold = null;
    }
    if (italic == paramFont2D) {
      italic = null;
    }
    if (bolditalic == paramFont2D) {
      bolditalic = null;
    }
    if ((plain == null) && (bold == null) && (plain == null) && (bold == null)) {
      familyNameMap.remove(str);
    }
  }
  
  public FontFamily(String paramString, boolean paramBoolean, int paramInt)
  {
    logicalFont = paramBoolean;
    familyName = paramString;
    familyRank = paramInt;
    familyNameMap.put(paramString.toLowerCase(Locale.ENGLISH), this);
  }
  
  FontFamily(String paramString)
  {
    logicalFont = false;
    familyName = paramString;
    familyRank = 4;
  }
  
  public String getFamilyName()
  {
    return familyName;
  }
  
  public int getRank()
  {
    return familyRank;
  }
  
  private boolean isFromSameSource(Font2D paramFont2D)
  {
    if (!(paramFont2D instanceof FileFont)) {
      return false;
    }
    FileFont localFileFont1 = null;
    if ((plain instanceof FileFont)) {
      localFileFont1 = (FileFont)plain;
    } else if ((bold instanceof FileFont)) {
      localFileFont1 = (FileFont)bold;
    } else if ((italic instanceof FileFont)) {
      localFileFont1 = (FileFont)italic;
    } else if ((bolditalic instanceof FileFont)) {
      localFileFont1 = (FileFont)bolditalic;
    }
    if (localFileFont1 == null) {
      return false;
    }
    File localFile1 = new File(platName).getParentFile();
    FileFont localFileFont2 = (FileFont)paramFont2D;
    File localFile2 = new File(platName).getParentFile();
    return Objects.equals(localFile2, localFile1);
  }
  
  private boolean preferredWidth(Font2D paramFont2D)
  {
    int i = paramFont2D.getWidth();
    if (familyWidth == 0)
    {
      familyWidth = i;
      return true;
    }
    if (i == familyWidth) {
      return true;
    }
    if (Math.abs(5 - i) < Math.abs(5 - familyWidth))
    {
      if (FontUtilities.debugFonts()) {
        FontUtilities.getLogger().info("Found more preferred width. New width = " + i + " Old width = " + familyWidth + " in font " + paramFont2D + " nulling out fonts plain: " + plain + " bold: " + bold + " italic: " + italic + " bolditalic: " + bolditalic);
      }
      familyWidth = i;
      plain = (bold = italic = bolditalic = null);
      return true;
    }
    if (FontUtilities.debugFonts()) {
      FontUtilities.getLogger().info("Family rejecting font " + paramFont2D + " of less preferred width " + i);
    }
    return false;
  }
  
  private boolean closerWeight(Font2D paramFont2D1, Font2D paramFont2D2, int paramInt)
  {
    if (familyWidth != paramFont2D2.getWidth()) {
      return false;
    }
    if (paramFont2D1 == null) {
      return true;
    }
    if (FontUtilities.debugFonts()) {
      FontUtilities.getLogger().info("New weight for style " + paramInt + ". Curr.font=" + paramFont2D1 + " New font=" + paramFont2D2 + " Curr.weight=" + paramFont2D1.getWeight() + " New weight=" + paramFont2D2.getWeight());
    }
    int i = paramFont2D2.getWeight();
    switch (paramInt)
    {
    case 0: 
    case 2: 
      return (i <= 400) && (i > paramFont2D1.getWeight());
    case 1: 
    case 3: 
      return Math.abs(i - 700) < Math.abs(paramFont2D1.getWeight() - 700);
    }
    return false;
  }
  
  public void setFont(Font2D paramFont2D, int paramInt)
  {
    if (FontUtilities.isLogging())
    {
      String str;
      if ((paramFont2D instanceof CompositeFont)) {
        str = "Request to add " + paramFont2D.getFamilyName(null) + " with style " + paramInt + " to family " + familyName;
      } else {
        str = "Request to add " + paramFont2D + " with style " + paramInt + " to family " + this;
      }
      FontUtilities.getLogger().info(str);
    }
    if ((paramFont2D.getRank() > familyRank) && (!isFromSameSource(paramFont2D)))
    {
      if (FontUtilities.isLogging()) {
        FontUtilities.getLogger().warning("Rejecting adding " + paramFont2D + " of lower rank " + paramFont2D.getRank() + " to family " + this + " of rank " + familyRank);
      }
      return;
    }
    switch (paramInt)
    {
    case 0: 
      if ((preferredWidth(paramFont2D)) && (closerWeight(plain, paramFont2D, paramInt))) {
        plain = paramFont2D;
      }
      break;
    case 1: 
      if ((preferredWidth(paramFont2D)) && (closerWeight(bold, paramFont2D, paramInt))) {
        bold = paramFont2D;
      }
      break;
    case 2: 
      if ((preferredWidth(paramFont2D)) && (closerWeight(italic, paramFont2D, paramInt))) {
        italic = paramFont2D;
      }
      break;
    case 3: 
      if ((preferredWidth(paramFont2D)) && (closerWeight(bolditalic, paramFont2D, paramInt))) {
        bolditalic = paramFont2D;
      }
      break;
    }
  }
  
  public Font2D getFontWithExactStyleMatch(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return plain;
    case 1: 
      return bold;
    case 2: 
      return italic;
    case 3: 
      return bolditalic;
    }
    return null;
  }
  
  public Font2D getFont(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return plain;
    case 1: 
      if (bold != null) {
        return bold;
      }
      if ((plain != null) && (plain.canDoStyle(paramInt))) {
        return plain;
      }
      return null;
    case 2: 
      if (italic != null) {
        return italic;
      }
      if ((plain != null) && (plain.canDoStyle(paramInt))) {
        return plain;
      }
      return null;
    case 3: 
      if (bolditalic != null) {
        return bolditalic;
      }
      if ((bold != null) && (bold.canDoStyle(paramInt))) {
        return bold;
      }
      if ((italic != null) && (italic.canDoStyle(paramInt))) {
        return italic;
      }
      if ((plain != null) && (plain.canDoStyle(paramInt))) {
        return plain;
      }
      return null;
    }
    return null;
  }
  
  Font2D getClosestStyle(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      if (bold != null) {
        return bold;
      }
      if (italic != null) {
        return italic;
      }
      return bolditalic;
    case 1: 
      if (plain != null) {
        return plain;
      }
      if (bolditalic != null) {
        return bolditalic;
      }
      return italic;
    case 2: 
      if (bolditalic != null) {
        return bolditalic;
      }
      if (plain != null) {
        return plain;
      }
      return bold;
    case 3: 
      if (italic != null) {
        return italic;
      }
      if (bold != null) {
        return bold;
      }
      return plain;
    }
    return null;
  }
  
  static synchronized void addLocaleNames(FontFamily paramFontFamily, String[] paramArrayOfString)
  {
    if (allLocaleNames == null) {
      allLocaleNames = new HashMap();
    }
    for (int i = 0; i < paramArrayOfString.length; i++) {
      allLocaleNames.put(paramArrayOfString[i].toLowerCase(), paramFontFamily);
    }
  }
  
  public static synchronized FontFamily getLocaleFamily(String paramString)
  {
    if (allLocaleNames == null) {
      return null;
    }
    return (FontFamily)allLocaleNames.get(paramString.toLowerCase());
  }
  
  public static FontFamily[] getAllFontFamilies()
  {
    Collection localCollection = familyNameMap.values();
    return (FontFamily[])localCollection.toArray(new FontFamily[0]);
  }
  
  public String toString()
  {
    return "Font family: " + familyName + " plain=" + plain + " bold=" + bold + " italic=" + italic + " bolditalic=" + bolditalic;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\FontFamily.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */