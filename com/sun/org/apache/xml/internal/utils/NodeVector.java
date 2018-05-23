package com.sun.org.apache.xml.internal.utils;

import java.io.Serializable;

public class NodeVector
  implements Serializable, Cloneable
{
  static final long serialVersionUID = -713473092200731870L;
  private int m_blocksize;
  private int[] m_map;
  protected int m_firstFree = 0;
  private int m_mapSize;
  
  public NodeVector()
  {
    m_blocksize = 32;
    m_mapSize = 0;
  }
  
  public NodeVector(int paramInt)
  {
    m_blocksize = paramInt;
    m_mapSize = 0;
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    NodeVector localNodeVector = (NodeVector)super.clone();
    if ((null != m_map) && (m_map == m_map))
    {
      m_map = new int[m_map.length];
      System.arraycopy(m_map, 0, m_map, 0, m_map.length);
    }
    return localNodeVector;
  }
  
  public int size()
  {
    return m_firstFree;
  }
  
  public void addElement(int paramInt)
  {
    if (m_firstFree + 1 >= m_mapSize) {
      if (null == m_map)
      {
        m_map = new int[m_blocksize];
        m_mapSize = m_blocksize;
      }
      else
      {
        m_mapSize += m_blocksize;
        int[] arrayOfInt = new int[m_mapSize];
        System.arraycopy(m_map, 0, arrayOfInt, 0, m_firstFree + 1);
        m_map = arrayOfInt;
      }
    }
    m_map[m_firstFree] = paramInt;
    m_firstFree += 1;
  }
  
  public final void push(int paramInt)
  {
    int i = m_firstFree;
    if (i + 1 >= m_mapSize) {
      if (null == m_map)
      {
        m_map = new int[m_blocksize];
        m_mapSize = m_blocksize;
      }
      else
      {
        m_mapSize += m_blocksize;
        int[] arrayOfInt = new int[m_mapSize];
        System.arraycopy(m_map, 0, arrayOfInt, 0, i + 1);
        m_map = arrayOfInt;
      }
    }
    m_map[i] = paramInt;
    i++;
    m_firstFree = i;
  }
  
  public final int pop()
  {
    m_firstFree -= 1;
    int i = m_map[m_firstFree];
    m_map[m_firstFree] = -1;
    return i;
  }
  
  public final int popAndTop()
  {
    m_firstFree -= 1;
    m_map[m_firstFree] = -1;
    return m_firstFree == 0 ? -1 : m_map[(m_firstFree - 1)];
  }
  
  public final void popQuick()
  {
    m_firstFree -= 1;
    m_map[m_firstFree] = -1;
  }
  
  public final int peepOrNull()
  {
    return (null != m_map) && (m_firstFree > 0) ? m_map[(m_firstFree - 1)] : -1;
  }
  
  public final void pushPair(int paramInt1, int paramInt2)
  {
    if (null == m_map)
    {
      m_map = new int[m_blocksize];
      m_mapSize = m_blocksize;
    }
    else if (m_firstFree + 2 >= m_mapSize)
    {
      m_mapSize += m_blocksize;
      int[] arrayOfInt = new int[m_mapSize];
      System.arraycopy(m_map, 0, arrayOfInt, 0, m_firstFree);
      m_map = arrayOfInt;
    }
    m_map[m_firstFree] = paramInt1;
    m_map[(m_firstFree + 1)] = paramInt2;
    m_firstFree += 2;
  }
  
  public final void popPair()
  {
    m_firstFree -= 2;
    m_map[m_firstFree] = -1;
    m_map[(m_firstFree + 1)] = -1;
  }
  
  public final void setTail(int paramInt)
  {
    m_map[(m_firstFree - 1)] = paramInt;
  }
  
  public final void setTailSub1(int paramInt)
  {
    m_map[(m_firstFree - 2)] = paramInt;
  }
  
  public final int peepTail()
  {
    return m_map[(m_firstFree - 1)];
  }
  
  public final int peepTailSub1()
  {
    return m_map[(m_firstFree - 2)];
  }
  
  public void insertInOrder(int paramInt)
  {
    for (int i = 0; i < m_firstFree; i++) {
      if (paramInt < m_map[i])
      {
        insertElementAt(paramInt, i);
        return;
      }
    }
    addElement(paramInt);
  }
  
  public void insertElementAt(int paramInt1, int paramInt2)
  {
    if (null == m_map)
    {
      m_map = new int[m_blocksize];
      m_mapSize = m_blocksize;
    }
    else if (m_firstFree + 1 >= m_mapSize)
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
  
  public void appendNodes(NodeVector paramNodeVector)
  {
    int i = paramNodeVector.size();
    if (null == m_map)
    {
      m_mapSize = (i + m_blocksize);
      m_map = new int[m_mapSize];
    }
    else if (m_firstFree + i >= m_mapSize)
    {
      m_mapSize += i + m_blocksize;
      int[] arrayOfInt = new int[m_mapSize];
      System.arraycopy(m_map, 0, arrayOfInt, 0, m_firstFree + i);
      m_map = arrayOfInt;
    }
    System.arraycopy(m_map, 0, m_map, m_firstFree, i);
    m_firstFree += i;
  }
  
  public void removeAllElements()
  {
    if (null == m_map) {
      return;
    }
    for (int i = 0; i < m_firstFree; i++) {
      m_map[i] = -1;
    }
    m_firstFree = 0;
  }
  
  public void RemoveAllNoClear()
  {
    if (null == m_map) {
      return;
    }
    m_firstFree = 0;
  }
  
  public boolean removeElement(int paramInt)
  {
    if (null == m_map) {
      return false;
    }
    for (int i = 0; i < m_firstFree; i++)
    {
      int j = m_map[i];
      if (j == paramInt)
      {
        if (i > m_firstFree) {
          System.arraycopy(m_map, i + 1, m_map, i - 1, m_firstFree - i);
        } else {
          m_map[i] = -1;
        }
        m_firstFree -= 1;
        return true;
      }
    }
    return false;
  }
  
  public void removeElementAt(int paramInt)
  {
    if (null == m_map) {
      return;
    }
    if (paramInt > m_firstFree) {
      System.arraycopy(m_map, paramInt + 1, m_map, paramInt - 1, m_firstFree - paramInt);
    } else {
      m_map[paramInt] = -1;
    }
  }
  
  public void setElementAt(int paramInt1, int paramInt2)
  {
    if (null == m_map)
    {
      m_map = new int[m_blocksize];
      m_mapSize = m_blocksize;
    }
    if (paramInt2 == -1) {
      addElement(paramInt1);
    }
    m_map[paramInt2] = paramInt1;
  }
  
  public int elementAt(int paramInt)
  {
    if (null == m_map) {
      return -1;
    }
    return m_map[paramInt];
  }
  
  public boolean contains(int paramInt)
  {
    if (null == m_map) {
      return false;
    }
    for (int i = 0; i < m_firstFree; i++)
    {
      int j = m_map[i];
      if (j == paramInt) {
        return true;
      }
    }
    return false;
  }
  
  public int indexOf(int paramInt1, int paramInt2)
  {
    if (null == m_map) {
      return -1;
    }
    for (int i = paramInt2; i < m_firstFree; i++)
    {
      int j = m_map[i];
      if (j == paramInt1) {
        return i;
      }
    }
    return -1;
  }
  
  public int indexOf(int paramInt)
  {
    if (null == m_map) {
      return -1;
    }
    for (int i = 0; i < m_firstFree; i++)
    {
      int j = m_map[i];
      if (j == paramInt) {
        return i;
      }
    }
    return -1;
  }
  
  public void sort(int[] paramArrayOfInt, int paramInt1, int paramInt2)
    throws Exception
  {
    int i = paramInt1;
    int j = paramInt2;
    if (i >= j) {
      return;
    }
    if (i == j - 1)
    {
      if (paramArrayOfInt[i] > paramArrayOfInt[j])
      {
        k = paramArrayOfInt[i];
        paramArrayOfInt[i] = paramArrayOfInt[j];
        paramArrayOfInt[j] = k;
      }
      return;
    }
    int k = paramArrayOfInt[((i + j) / 2)];
    paramArrayOfInt[((i + j) / 2)] = paramArrayOfInt[j];
    paramArrayOfInt[j] = k;
    while (i < j)
    {
      while ((paramArrayOfInt[i] <= k) && (i < j)) {
        i++;
      }
      while ((k <= paramArrayOfInt[j]) && (i < j)) {
        j--;
      }
      if (i < j)
      {
        int m = paramArrayOfInt[i];
        paramArrayOfInt[i] = paramArrayOfInt[j];
        paramArrayOfInt[j] = m;
      }
    }
    paramArrayOfInt[paramInt2] = paramArrayOfInt[j];
    paramArrayOfInt[j] = k;
    sort(paramArrayOfInt, paramInt1, i - 1);
    sort(paramArrayOfInt, j + 1, paramInt2);
  }
  
  public void sort()
    throws Exception
  {
    sort(m_map, 0, m_firstFree - 1);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\NodeVector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */