package sun.text.normalizer;

import java.text.CharacterIterator;

public class CharacterIteratorWrapper
  extends UCharacterIterator
{
  private CharacterIterator iterator;
  
  public CharacterIteratorWrapper(CharacterIterator paramCharacterIterator)
  {
    if (paramCharacterIterator == null) {
      throw new IllegalArgumentException();
    }
    iterator = paramCharacterIterator;
  }
  
  public int current()
  {
    int i = iterator.current();
    if (i == 65535) {
      return -1;
    }
    return i;
  }
  
  public int getLength()
  {
    return iterator.getEndIndex() - iterator.getBeginIndex();
  }
  
  public int getIndex()
  {
    return iterator.getIndex();
  }
  
  public int next()
  {
    int i = iterator.current();
    iterator.next();
    if (i == 65535) {
      return -1;
    }
    return i;
  }
  
  public int previous()
  {
    int i = iterator.previous();
    if (i == 65535) {
      return -1;
    }
    return i;
  }
  
  public void setIndex(int paramInt)
  {
    iterator.setIndex(paramInt);
  }
  
  public int getText(char[] paramArrayOfChar, int paramInt)
  {
    int i = iterator.getEndIndex() - iterator.getBeginIndex();
    int j = iterator.getIndex();
    if ((paramInt < 0) || (paramInt + i > paramArrayOfChar.length)) {
      throw new IndexOutOfBoundsException(Integer.toString(i));
    }
    for (int k = iterator.first(); k != 65535; k = iterator.next()) {
      paramArrayOfChar[(paramInt++)] = k;
    }
    iterator.setIndex(j);
    return i;
  }
  
  public Object clone()
  {
    try
    {
      CharacterIteratorWrapper localCharacterIteratorWrapper = (CharacterIteratorWrapper)super.clone();
      iterator = ((CharacterIterator)iterator.clone());
      return localCharacterIteratorWrapper;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\text\normalizer\CharacterIteratorWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */