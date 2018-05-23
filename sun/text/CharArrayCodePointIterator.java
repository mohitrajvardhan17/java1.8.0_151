package sun.text;

final class CharArrayCodePointIterator
  extends CodePointIterator
{
  private char[] text;
  private int start;
  private int limit;
  private int index;
  
  public CharArrayCodePointIterator(char[] paramArrayOfChar)
  {
    text = paramArrayOfChar;
    limit = paramArrayOfChar.length;
  }
  
  public CharArrayCodePointIterator(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt2 < paramInt1) || (paramInt2 > paramArrayOfChar.length)) {
      throw new IllegalArgumentException();
    }
    text = paramArrayOfChar;
    start = (index = paramInt1);
    limit = paramInt2;
  }
  
  public void setToStart()
  {
    index = start;
  }
  
  public void setToLimit()
  {
    index = limit;
  }
  
  public int next()
  {
    if (index < limit)
    {
      char c1 = text[(index++)];
      if ((Character.isHighSurrogate(c1)) && (index < limit))
      {
        char c2 = text[index];
        if (Character.isLowSurrogate(c2))
        {
          index += 1;
          return Character.toCodePoint(c1, c2);
        }
      }
      return c1;
    }
    return -1;
  }
  
  public int prev()
  {
    if (index > start)
    {
      char c1 = text[(--index)];
      if ((Character.isLowSurrogate(c1)) && (index > start))
      {
        char c2 = text[(index - 1)];
        if (Character.isHighSurrogate(c2))
        {
          index -= 1;
          return Character.toCodePoint(c2, c1);
        }
      }
      return c1;
    }
    return -1;
  }
  
  public int charIndex()
  {
    return index;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\text\CharArrayCodePointIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */