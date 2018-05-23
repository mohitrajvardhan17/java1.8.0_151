package com.sun.org.apache.xml.internal.utils;

public class CharKey
{
  private char m_char;
  
  public CharKey(char paramChar)
  {
    m_char = paramChar;
  }
  
  public CharKey() {}
  
  public final void setChar(char paramChar)
  {
    m_char = paramChar;
  }
  
  public final int hashCode()
  {
    return m_char;
  }
  
  public final boolean equals(Object paramObject)
  {
    return m_char == m_char;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\CharKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */