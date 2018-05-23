package com.sun.xml.internal.messaging.saaj.util;

import java.io.CharArrayWriter;

public class CharWriter
  extends CharArrayWriter
{
  public CharWriter() {}
  
  public CharWriter(int paramInt)
  {
    super(paramInt);
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\util\CharWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */