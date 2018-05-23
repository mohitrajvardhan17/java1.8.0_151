package com.sun.org.apache.xml.internal.utils;

public class StringBufferPool
{
  private static ObjectPool m_stringBufPool = new ObjectPool(FastStringBuffer.class);
  
  public StringBufferPool() {}
  
  public static synchronized FastStringBuffer get()
  {
    return (FastStringBuffer)m_stringBufPool.getInstance();
  }
  
  public static synchronized void free(FastStringBuffer paramFastStringBuffer)
  {
    paramFastStringBuffer.setLength(0);
    m_stringBufPool.freeInstance(paramFastStringBuffer);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\StringBufferPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */