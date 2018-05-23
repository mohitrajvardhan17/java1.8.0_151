package com.sun.org.apache.regexp.internal;

public final class CharacterArrayCharacterIterator
  implements CharacterIterator
{
  private final char[] src;
  private final int off;
  private final int len;
  
  public CharacterArrayCharacterIterator(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    src = paramArrayOfChar;
    off = paramInt1;
    len = paramInt2;
  }
  
  public String substring(int paramInt1, int paramInt2)
  {
    if (paramInt2 > len) {
      throw new IndexOutOfBoundsException("endIndex=" + paramInt2 + "; sequence size=" + len);
    }
    if ((paramInt1 < 0) || (paramInt1 > paramInt2)) {
      throw new IndexOutOfBoundsException("beginIndex=" + paramInt1 + "; endIndex=" + paramInt2);
    }
    return new String(src, off + paramInt1, paramInt2 - paramInt1);
  }
  
  public String substring(int paramInt)
  {
    return substring(paramInt, len);
  }
  
  public char charAt(int paramInt)
  {
    return src[(off + paramInt)];
  }
  
  public boolean isEnd(int paramInt)
  {
    return paramInt >= len;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\regexp\internal\CharacterArrayCharacterIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */