package com.sun.xml.internal.fastinfoset.util;

public class CharArray
  implements CharSequence
{
  public char[] ch;
  public int start;
  public int length;
  protected int _hash;
  
  protected CharArray() {}
  
  public CharArray(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    set(paramArrayOfChar, paramInt1, paramInt2, paramBoolean);
  }
  
  public final void set(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      ch = new char[paramInt2];
      start = 0;
      length = paramInt2;
      System.arraycopy(paramArrayOfChar, paramInt1, ch, 0, paramInt2);
    }
    else
    {
      ch = paramArrayOfChar;
      start = paramInt1;
      length = paramInt2;
    }
    _hash = 0;
  }
  
  public final void cloneArray()
  {
    char[] arrayOfChar = new char[length];
    System.arraycopy(ch, start, arrayOfChar, 0, length);
    ch = arrayOfChar;
    start = 0;
  }
  
  public String toString()
  {
    return new String(ch, start, length);
  }
  
  public int hashCode()
  {
    if (_hash == 0) {
      for (int i = start; i < start + length; i++) {
        _hash = (31 * _hash + ch[i]);
      }
    }
    return _hash;
  }
  
  public static final int hashCode(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    int i = 0;
    for (int j = paramInt1; j < paramInt1 + paramInt2; j++) {
      i = 31 * i + paramArrayOfChar[j];
    }
    return i;
  }
  
  public final boolean equalsCharArray(CharArray paramCharArray)
  {
    if (this == paramCharArray) {
      return true;
    }
    if (length == length)
    {
      int i = length;
      int j = start;
      int k = start;
      while (i-- != 0) {
        if (ch[(j++)] != ch[(k++)]) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  public final boolean equalsCharArray(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    if (length == paramInt2)
    {
      int i = length;
      int j = start;
      int k = paramInt1;
      while (i-- != 0) {
        if (ch[(j++)] != paramArrayOfChar[(k++)]) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof CharArray))
    {
      CharArray localCharArray = (CharArray)paramObject;
      if (length == length)
      {
        int i = length;
        int j = start;
        int k = start;
        while (i-- != 0) {
          if (ch[(j++)] != ch[(k++)]) {
            return false;
          }
        }
        return true;
      }
    }
    return false;
  }
  
  public final int length()
  {
    return length;
  }
  
  public final char charAt(int paramInt)
  {
    return ch[(start + paramInt)];
  }
  
  public final CharSequence subSequence(int paramInt1, int paramInt2)
  {
    return new CharArray(ch, start + paramInt1, paramInt2 - paramInt1, false);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\util\CharArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */