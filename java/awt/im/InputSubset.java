package java.awt.im;

public final class InputSubset
  extends Character.Subset
{
  public static final InputSubset LATIN = new InputSubset("LATIN");
  public static final InputSubset LATIN_DIGITS = new InputSubset("LATIN_DIGITS");
  public static final InputSubset TRADITIONAL_HANZI = new InputSubset("TRADITIONAL_HANZI");
  public static final InputSubset SIMPLIFIED_HANZI = new InputSubset("SIMPLIFIED_HANZI");
  public static final InputSubset KANJI = new InputSubset("KANJI");
  public static final InputSubset HANJA = new InputSubset("HANJA");
  public static final InputSubset HALFWIDTH_KATAKANA = new InputSubset("HALFWIDTH_KATAKANA");
  public static final InputSubset FULLWIDTH_LATIN = new InputSubset("FULLWIDTH_LATIN");
  public static final InputSubset FULLWIDTH_DIGITS = new InputSubset("FULLWIDTH_DIGITS");
  
  private InputSubset(String paramString)
  {
    super(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\im\InputSubset.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */