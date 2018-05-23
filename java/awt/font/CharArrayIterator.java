package java.awt.font;

import java.text.CharacterIterator;

class CharArrayIterator
  implements CharacterIterator
{
  private char[] chars;
  private int pos;
  private int begin;
  
  CharArrayIterator(char[] paramArrayOfChar)
  {
    reset(paramArrayOfChar, 0);
  }
  
  CharArrayIterator(char[] paramArrayOfChar, int paramInt)
  {
    reset(paramArrayOfChar, paramInt);
  }
  
  public char first()
  {
    pos = 0;
    return current();
  }
  
  public char last()
  {
    if (chars.length > 0) {
      pos = (chars.length - 1);
    } else {
      pos = 0;
    }
    return current();
  }
  
  public char current()
  {
    if ((pos >= 0) && (pos < chars.length)) {
      return chars[pos];
    }
    return 65535;
  }
  
  public char next()
  {
    if (pos < chars.length - 1)
    {
      pos += 1;
      return chars[pos];
    }
    pos = chars.length;
    return 65535;
  }
  
  public char previous()
  {
    if (pos > 0)
    {
      pos -= 1;
      return chars[pos];
    }
    pos = 0;
    return 65535;
  }
  
  public char setIndex(int paramInt)
  {
    paramInt -= begin;
    if ((paramInt < 0) || (paramInt > chars.length)) {
      throw new IllegalArgumentException("Invalid index");
    }
    pos = paramInt;
    return current();
  }
  
  public int getBeginIndex()
  {
    return begin;
  }
  
  public int getEndIndex()
  {
    return begin + chars.length;
  }
  
  public int getIndex()
  {
    return begin + pos;
  }
  
  public Object clone()
  {
    CharArrayIterator localCharArrayIterator = new CharArrayIterator(chars, begin);
    pos = pos;
    return localCharArrayIterator;
  }
  
  void reset(char[] paramArrayOfChar)
  {
    reset(paramArrayOfChar, 0);
  }
  
  void reset(char[] paramArrayOfChar, int paramInt)
  {
    chars = paramArrayOfChar;
    begin = paramInt;
    pos = 0;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\font\CharArrayIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */