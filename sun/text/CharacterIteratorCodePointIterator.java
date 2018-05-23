package sun.text;

import java.text.CharacterIterator;

final class CharacterIteratorCodePointIterator
  extends CodePointIterator
{
  private CharacterIterator iter;
  
  public CharacterIteratorCodePointIterator(CharacterIterator paramCharacterIterator)
  {
    iter = paramCharacterIterator;
  }
  
  public void setToStart()
  {
    iter.setIndex(iter.getBeginIndex());
  }
  
  public void setToLimit()
  {
    iter.setIndex(iter.getEndIndex());
  }
  
  public int next()
  {
    int i = iter.current();
    if (i != 65535)
    {
      int j = iter.next();
      if ((Character.isHighSurrogate(i)) && (j != 65535) && (Character.isLowSurrogate(j)))
      {
        iter.next();
        return Character.toCodePoint(i, j);
      }
      return i;
    }
    return -1;
  }
  
  public int prev()
  {
    int i = iter.previous();
    if (i != 65535)
    {
      if (Character.isLowSurrogate(i))
      {
        char c = iter.previous();
        if (Character.isHighSurrogate(c)) {
          return Character.toCodePoint(c, i);
        }
        iter.next();
      }
      return i;
    }
    return -1;
  }
  
  public int charIndex()
  {
    return iter.getIndex();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\text\CharacterIteratorCodePointIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */