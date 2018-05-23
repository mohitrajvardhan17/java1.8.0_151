package com.sun.org.apache.xml.internal.utils.res;

public class IntArrayWrapper
{
  private int[] m_int;
  
  public IntArrayWrapper(int[] paramArrayOfInt)
  {
    m_int = paramArrayOfInt;
  }
  
  public int getInt(int paramInt)
  {
    return m_int[paramInt];
  }
  
  public int getLength()
  {
    return m_int.length;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\res\IntArrayWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */