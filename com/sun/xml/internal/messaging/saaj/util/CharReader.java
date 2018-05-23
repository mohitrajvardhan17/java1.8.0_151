package com.sun.xml.internal.messaging.saaj.util;

import java.io.CharArrayReader;

public class CharReader
  extends CharArrayReader
{
  public CharReader(char[] paramArrayOfChar, int paramInt)
  {
    super(paramArrayOfChar, 0, paramInt);
  }
  
  public CharReader(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    super(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public char[] getChars()
  {
    return buf;
  }
  
  public int getCount()
  {
    return count;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\util\CharReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */