package com.sun.xml.internal.fastinfoset.util;

public class CharArrayString
  extends CharArray
{
  protected String _s;
  
  public CharArrayString(String paramString)
  {
    this(paramString, true);
  }
  
  public CharArrayString(String paramString, boolean paramBoolean)
  {
    _s = paramString;
    if (paramBoolean)
    {
      ch = _s.toCharArray();
      start = 0;
      length = ch.length;
    }
  }
  
  public String toString()
  {
    return _s;
  }
  
  public int hashCode()
  {
    return _s.hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    Object localObject;
    if ((paramObject instanceof CharArrayString))
    {
      localObject = (CharArrayString)paramObject;
      return _s.equals(_s);
    }
    if ((paramObject instanceof CharArray))
    {
      localObject = (CharArray)paramObject;
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
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\util\CharArrayString.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */