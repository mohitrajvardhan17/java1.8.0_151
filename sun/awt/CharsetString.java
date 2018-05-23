package sun.awt;

public class CharsetString
{
  public char[] charsetChars;
  public int offset;
  public int length;
  public FontDescriptor fontDescriptor;
  
  public CharsetString(char[] paramArrayOfChar, int paramInt1, int paramInt2, FontDescriptor paramFontDescriptor)
  {
    charsetChars = paramArrayOfChar;
    offset = paramInt1;
    length = paramInt2;
    fontDescriptor = paramFontDescriptor;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\CharsetString.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */