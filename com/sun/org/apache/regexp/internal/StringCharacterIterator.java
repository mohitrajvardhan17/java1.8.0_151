package com.sun.org.apache.regexp.internal;

public final class StringCharacterIterator
  implements CharacterIterator
{
  private final String src;
  
  public StringCharacterIterator(String paramString)
  {
    src = paramString;
  }
  
  public String substring(int paramInt1, int paramInt2)
  {
    return src.substring(paramInt1, paramInt2);
  }
  
  public String substring(int paramInt)
  {
    return src.substring(paramInt);
  }
  
  public char charAt(int paramInt)
  {
    return src.charAt(paramInt);
  }
  
  public boolean isEnd(int paramInt)
  {
    return paramInt >= src.length();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\regexp\internal\StringCharacterIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */