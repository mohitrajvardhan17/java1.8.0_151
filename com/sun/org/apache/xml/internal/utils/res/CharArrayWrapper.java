package com.sun.org.apache.xml.internal.utils.res;

public class CharArrayWrapper
{
  private char[] m_char;
  
  public CharArrayWrapper(char[] paramArrayOfChar)
  {
    m_char = paramArrayOfChar;
  }
  
  public char getChar(int paramInt)
  {
    return m_char[paramInt];
  }
  
  public int getLength()
  {
    return m_char.length;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\res\CharArrayWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */