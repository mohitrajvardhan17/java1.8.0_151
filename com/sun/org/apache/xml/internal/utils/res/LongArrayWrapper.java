package com.sun.org.apache.xml.internal.utils.res;

public class LongArrayWrapper
{
  private long[] m_long;
  
  public LongArrayWrapper(long[] paramArrayOfLong)
  {
    m_long = paramArrayOfLong;
  }
  
  public long getLong(int paramInt)
  {
    return m_long[paramInt];
  }
  
  public int getLength()
  {
    return m_long.length;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\res\LongArrayWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */