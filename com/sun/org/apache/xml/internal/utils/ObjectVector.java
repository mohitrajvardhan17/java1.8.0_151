package com.sun.org.apache.xml.internal.utils;

public class ObjectVector
  implements Cloneable
{
  protected int m_blocksize;
  protected Object[] m_map;
  protected int m_firstFree = 0;
  protected int m_mapSize;
  
  public ObjectVector()
  {
    m_blocksize = 32;
    m_mapSize = m_blocksize;
    m_map = new Object[m_blocksize];
  }
  
  public ObjectVector(int paramInt)
  {
    m_blocksize = paramInt;
    m_mapSize = paramInt;
    m_map = new Object[paramInt];
  }
  
  public ObjectVector(int paramInt1, int paramInt2)
  {
    m_blocksize = paramInt2;
    m_mapSize = paramInt1;
    m_map = new Object[paramInt1];
  }
  
  public ObjectVector(ObjectVector paramObjectVector)
  {
    m_map = new Object[m_mapSize];
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
  
  public final void addElement(Object paramObject)
  {
    if (m_firstFree + 1 >= m_mapSize)
    {
      m_mapSize += m_blocksize;
      Object[] arrayOfObject = new Object[m_mapSize];
      System.arraycopy(m_map, 0, arrayOfObject, 0, m_firstFree + 1);
      m_map = arrayOfObject;
    }
    m_map[m_firstFree] = paramObject;
    m_firstFree += 1;
  }
  
  public final void addElements(Object paramObject, int paramInt)
  {
    if (m_firstFree + paramInt >= m_mapSize)
    {
      m_mapSize += m_blocksize + paramInt;
      Object[] arrayOfObject = new Object[m_mapSize];
      System.arraycopy(m_map, 0, arrayOfObject, 0, m_firstFree + 1);
      m_map = arrayOfObject;
    }
    for (int i = 0; i < paramInt; i++)
    {
      m_map[m_firstFree] = paramObject;
      m_firstFree += 1;
    }
  }
  
  public final void addElements(int paramInt)
  {
    if (m_firstFree + paramInt >= m_mapSize)
    {
      m_mapSize += m_blocksize + paramInt;
      Object[] arrayOfObject = new Object[m_mapSize];
      System.arraycopy(m_map, 0, arrayOfObject, 0, m_firstFree + 1);
      m_map = arrayOfObject;
    }
    m_firstFree += paramInt;
  }
  
  public final void insertElementAt(Object paramObject, int paramInt)
  {
    if (m_firstFree + 1 >= m_mapSize)
    {
      m_mapSize += m_blocksize;
      Object[] arrayOfObject = new Object[m_mapSize];
      System.arraycopy(m_map, 0, arrayOfObject, 0, m_firstFree + 1);
      m_map = arrayOfObject;
    }
    if (paramInt <= m_firstFree - 1) {
      System.arraycopy(m_map, paramInt, m_map, paramInt + 1, m_firstFree - paramInt);
    }
    m_map[paramInt] = paramObject;
    m_firstFree += 1;
  }
  
  public final void removeAllElements()
  {
    for (int i = 0; i < m_firstFree; i++) {
      m_map[i] = null;
    }
    m_firstFree = 0;
  }
  
  public final boolean removeElement(Object paramObject)
  {
    for (int i = 0; i < m_firstFree; i++) {
      if (m_map[i] == paramObject)
      {
        if (i + 1 < m_firstFree) {
          System.arraycopy(m_map, i + 1, m_map, i - 1, m_firstFree - i);
        } else {
          m_map[i] = null;
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
      m_map[paramInt] = null;
    }
    m_firstFree -= 1;
  }
  
  public final void setElementAt(Object paramObject, int paramInt)
  {
    m_map[paramInt] = paramObject;
  }
  
  public final Object elementAt(int paramInt)
  {
    return m_map[paramInt];
  }
  
  public final boolean contains(Object paramObject)
  {
    for (int i = 0; i < m_firstFree; i++) {
      if (m_map[i] == paramObject) {
        return true;
      }
    }
    return false;
  }
  
  public final int indexOf(Object paramObject, int paramInt)
  {
    for (int i = paramInt; i < m_firstFree; i++) {
      if (m_map[i] == paramObject) {
        return i;
      }
    }
    return Integer.MIN_VALUE;
  }
  
  public final int indexOf(Object paramObject)
  {
    for (int i = 0; i < m_firstFree; i++) {
      if (m_map[i] == paramObject) {
        return i;
      }
    }
    return Integer.MIN_VALUE;
  }
  
  public final int lastIndexOf(Object paramObject)
  {
    for (int i = m_firstFree - 1; i >= 0; i--) {
      if (m_map[i] == paramObject) {
        return i;
      }
    }
    return Integer.MIN_VALUE;
  }
  
  public final void setToSize(int paramInt)
  {
    Object[] arrayOfObject = new Object[paramInt];
    System.arraycopy(m_map, 0, arrayOfObject, 0, m_firstFree);
    m_mapSize = paramInt;
    m_map = arrayOfObject;
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    return new ObjectVector(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\ObjectVector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */