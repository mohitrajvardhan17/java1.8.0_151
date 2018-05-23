package com.sun.org.apache.xml.internal.utils.res;

public class StringArrayWrapper
{
  private String[] m_string;
  
  public StringArrayWrapper(String[] paramArrayOfString)
  {
    m_string = paramArrayOfString;
  }
  
  public String getString(int paramInt)
  {
    return m_string[paramInt];
  }
  
  public int getLength()
  {
    return m_string.length;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\res\StringArrayWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */