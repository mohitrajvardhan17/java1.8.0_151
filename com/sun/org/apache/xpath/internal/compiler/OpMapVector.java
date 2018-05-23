package com.sun.org.apache.xpath.internal.compiler;

public class OpMapVector
{
  protected int m_blocksize;
  protected int[] m_map;
  protected int m_lengthPos = 0;
  protected int m_mapSize;
  
  public OpMapVector(int paramInt1, int paramInt2, int paramInt3)
  {
    m_blocksize = paramInt2;
    m_mapSize = paramInt1;
    m_lengthPos = paramInt3;
    m_map = new int[paramInt1];
  }
  
  public final int elementAt(int paramInt)
  {
    return m_map[paramInt];
  }
  
  public final void setElementAt(int paramInt1, int paramInt2)
  {
    if (paramInt2 >= m_mapSize)
    {
      int i = m_mapSize;
      m_mapSize += m_blocksize;
      int[] arrayOfInt = new int[m_mapSize];
      System.arraycopy(m_map, 0, arrayOfInt, 0, i);
      m_map = arrayOfInt;
    }
    m_map[paramInt2] = paramInt1;
  }
  
  public final void setToSize(int paramInt)
  {
    int[] arrayOfInt = new int[paramInt];
    System.arraycopy(m_map, 0, arrayOfInt, 0, m_map[m_lengthPos]);
    m_mapSize = paramInt;
    m_map = arrayOfInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\compiler\OpMapVector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */