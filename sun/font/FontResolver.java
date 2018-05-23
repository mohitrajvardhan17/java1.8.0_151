package sun.font;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Map;
import sun.text.CodePointIterator;

public final class FontResolver
{
  private Font[] allFonts;
  private Font[] supplementaryFonts;
  private int[] supplementaryIndices;
  private static final int DEFAULT_SIZE = 12;
  private Font defaultFont = new Font("Dialog", 0, 12);
  private static final int SHIFT = 9;
  private static final int BLOCKSIZE = 128;
  private static final int MASK = 127;
  private int[][] blocks = new int['Ȁ'][];
  private static FontResolver INSTANCE;
  
  private FontResolver() {}
  
  private Font[] getAllFonts()
  {
    if (allFonts == null)
    {
      allFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
      for (int i = 0; i < allFonts.length; i++) {
        allFonts[i] = allFonts[i].deriveFont(12.0F);
      }
    }
    return allFonts;
  }
  
  private int getIndexFor(char paramChar)
  {
    if (defaultFont.canDisplay(paramChar)) {
      return 1;
    }
    for (int i = 0; i < getAllFonts().length; i++) {
      if (allFonts[i].canDisplay(paramChar)) {
        return i + 2;
      }
    }
    return 1;
  }
  
  private Font[] getAllSCFonts()
  {
    if (supplementaryFonts == null)
    {
      ArrayList localArrayList1 = new ArrayList();
      ArrayList localArrayList2 = new ArrayList();
      for (int i = 0; i < getAllFonts().length; i++)
      {
        Font localFont = allFonts[i];
        Font2D localFont2D = FontUtilities.getFont2D(localFont);
        if (localFont2D.hasSupplementaryChars())
        {
          localArrayList1.add(localFont);
          localArrayList2.add(Integer.valueOf(i));
        }
      }
      i = localArrayList1.size();
      supplementaryIndices = new int[i];
      for (int j = 0; j < i; j++) {
        supplementaryIndices[j] = ((Integer)localArrayList2.get(j)).intValue();
      }
      supplementaryFonts = ((Font[])localArrayList1.toArray(new Font[i]));
    }
    return supplementaryFonts;
  }
  
  private int getIndexFor(int paramInt)
  {
    if (defaultFont.canDisplay(paramInt)) {
      return 1;
    }
    for (int i = 0; i < getAllSCFonts().length; i++) {
      if (supplementaryFonts[i].canDisplay(paramInt)) {
        return supplementaryIndices[i] + 2;
      }
    }
    return 1;
  }
  
  public int getFontIndex(char paramChar)
  {
    int i = paramChar >> '\t';
    int[] arrayOfInt = blocks[i];
    if (arrayOfInt == null)
    {
      arrayOfInt = new int[''];
      blocks[i] = arrayOfInt;
    }
    int j = paramChar & 0x7F;
    if (arrayOfInt[j] == 0) {
      arrayOfInt[j] = getIndexFor(paramChar);
    }
    return arrayOfInt[j];
  }
  
  public int getFontIndex(int paramInt)
  {
    if (paramInt < 65536) {
      return getFontIndex((char)paramInt);
    }
    return getIndexFor(paramInt);
  }
  
  public int nextFontRunIndex(CodePointIterator paramCodePointIterator)
  {
    int i = paramCodePointIterator.next();
    int j = 1;
    if (i != -1)
    {
      j = getFontIndex(i);
      while ((i = paramCodePointIterator.next()) != -1) {
        if (getFontIndex(i) != j) {
          paramCodePointIterator.prev();
        }
      }
    }
    return j;
  }
  
  public Font getFont(int paramInt, Map paramMap)
  {
    Font localFont = defaultFont;
    if (paramInt >= 2) {
      localFont = allFonts[(paramInt - 2)];
    }
    return localFont.deriveFont(paramMap);
  }
  
  public static FontResolver getInstance()
  {
    if (INSTANCE == null) {
      INSTANCE = new FontResolver();
    }
    return INSTANCE;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\FontResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */