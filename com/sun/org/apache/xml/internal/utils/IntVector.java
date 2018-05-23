package com.sun.org.apache.xml.internal.utils;

public class IntVector
  implements Cloneable
{
  protected int m_blocksize;
  protected int[] m_map;
  protected int m_firstFree = 0;
  protected int m_mapSize;
  
  public IntVector()
  {
    m_blocksize = 32;
    m_mapSize = m_blocksize;
    m_map = new int[m_blocksize];
  }
  
  public IntVector(int paramInt)
  {
    m_blocksize = paramInt;
    m_mapSize = paramInt;
    m_map = new int[paramInt];
  }
  
  public IntVector(int paramInt1, int paramInt2)
  {
    m_blocksize = paramInt2;
    m_mapSize = paramInt1;
    m_map = new int[paramInt1];
  }
  
  public IntVector(IntVector paramIntVector)
  {
    m_map = new int[m_mapSize];
    m_mapSize = m_mapSize;
    m_firstFree = m_firstFree;
    m_blocksize = m_blocksize;
    System.arraycopy(m_map, 0, m_map, 0, m_firstFree);
  }
  
  public final int size()
  {
    return m_firstFree;
  }
  
  public final void setSize(int paramInt)
  {
    m_firstFree = paramInt;
  }
  
  public final void addElement(int paramInt)
  {
    if (m_firstFree + 1 >= m_mapSize)
    {
      m_mapSize += m_blocksize;
      int[] arrayOfInt = new int[m_mapSize];
      System.arraycopy(m_map, 0, arrayOfInt, 0, m_firstFree + 1);
      m_map = arrayOfInt;
    }
    m_map[m_firstFree] = paramInt;
    m_firstFree += 1;
  }
  
  public final void addElements(int paramInt1, int paramInt2)
  {
    if (m_firstFree + paramInt2 >= m_mapSize)
    {
      m_mapSize += m_blocksize + paramInt2;
      int[] arrayOfInt = new int[m_mapSize];
      System.arraycopy(m_map, 0, arrayOfInt, 0, m_firstFree + 1);
      m_map = arrayOfInt;
    }
    for (int i = 0; i < paramInt2; i++)
    {
      m_map[m_firstFree] = paramInt1;
      m_firstFree += 1;
    }
  }
  
  public final void addElements(int paramInt)
  {
    if (m_firstFree + paramInt >= m_mapSize)
    {
      m_mapSize += m_blocksize + paramInt;
      int[] arrayOfInt = new int[m_mapSize];
      System.arraycopy(m_map, 0, arrayOfInt, 0, m_firstFree + 1);
      m_map = arrayOfInt;
    }
    m_firstFree += paramInt;
  }
  
  public final void insertElementAt(int paramInt1, int paramInt2)
  {
    if (m_firstFree + 1 >= m_mapSize)
    {
      m_mapSize += m_blocksize;
      int[] arrayOfInt = new int[m_mapSize];
      System.arraycopy(m_map, 0, arrayOfInt, 0, m_firstFree + 1);
      m_map = arrayOfInt;
    }
    if (paramInt2 <= m_firstFree - 1) {
      System.arraycopy(m_map, paramInt2, m_map, paramInt2 + 1, m_firstFree - paramInt2);
    }
    m_map[paramInt2] = paramInt1;
    m_firstFree += 1;
  }
  
  public final void removeAllElements()
  {
    for (int i = 0; i < m_firstFree; i++) {
      m_map[i] = Integer.MIN_VALUE;
    }
    m_firstFree = 0;
  }
  
  public final boolean removeElement(int paramInt)
  {
    for (int i = 0; i < m_firstFree; i++) {
      if (m_map[i] == paramInt)
      {
        if (i + 1 < m_firstFree) {
          System.arraycopy(m_map, i + 1, m_map, i - 1, m_firstFree - i);
        } else {
          m_map[i] = Integer.MIN_VALUE;
        }
        m_firstFree -= 1;
        return true;
      }
    }
    return false;
  }
  
  public final void removeElementAt(int paramInt)
  {
    if (paramInt > m_firstFree) {
      System.arraycopy(m_map, paramInt + 1, m_map, paramInt, m_firstFree);
    } else {
      m_map[paramInt] = Integer.MIN_VALUE;
    }
    m_firstFree -= 1;
  }
  
  public final void setElementAt(int paramInt1, int paramInt2)
  {
    m_map[paramInt2] = paramInt1;
  }
  
  public final int elementAt(int paramInt)
  {
    return m_map[paramInt];
  }
  
  public final boolean contains(int paramInt)
  {
    for (int i = 0; i < m_firstFree; i++) {
      if (m_map[i] == paramInt) {
        return true;
      }
    }
    return false;
  }
  
  public final int indexOf(int paramInt1, int paramInt2)
  {
    for (int i = paramInt2; i < m_firstFree; i++) {
      if (m_map[i] == paramInt1) {
        return i;
      }
    }
    return Integer.MIN_VALUE;
  }
  
  public final int indexOf(int paramInt)
  {
    for (int i = 0; i < m_firstFree; i++) {
      if (m_map[i] == paramInt) {
        return i;
      }
    }
    return Integer.MIN_VALUE;
  }
  
  public final int lastIndexOf(int paramInt)
  {
    for (int i = m_firstFree - 1; i >= 0; i--) {
      if (m_map[i] == paramInt) {
        return i;
      }
    }
    return Integer.MIN_VALUE;
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    return new IntVector(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\IntVector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */