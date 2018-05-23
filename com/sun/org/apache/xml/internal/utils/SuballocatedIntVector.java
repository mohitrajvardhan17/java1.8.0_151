package com.sun.org.apache.xml.internal.utils;

public class SuballocatedIntVector
{
  protected int m_blocksize;
  protected int m_SHIFT;
  protected int m_MASK;
  protected static final int NUMBLOCKS_DEFAULT = 32;
  protected int m_numblocks = 32;
  protected int[][] m_map;
  protected int m_firstFree = 0;
  protected int[] m_map0;
  protected int[] m_buildCache;
  protected int m_buildCacheStartIndex;
  
  public SuballocatedIntVector()
  {
    this(2048);
  }
  
  public SuballocatedIntVector(int paramInt1, int paramInt2)
  {
    for (m_SHIFT = 0; 0 != paramInt1 >>>= 1; m_SHIFT += 1) {}
    m_blocksize = (1 << m_SHIFT);
    m_MASK = (m_blocksize - 1);
    m_numblocks = paramInt2;
    m_map0 = new int[m_blocksize];
    m_map = new int[paramInt2][];
    m_map[0] = m_map0;
    m_buildCache = m_map0;
    m_buildCacheStartIndex = 0;
  }
  
  public SuballocatedIntVector(int paramInt)
  {
    this(paramInt, 32);
  }
  
  public int size()
  {
    return m_firstFree;
  }
  
  public void setSize(int paramInt)
  {
    if (m_firstFree > paramInt) {
      m_firstFree = paramInt;
    }
  }
  
  public void addElement(int paramInt)
  {
    int i = m_firstFree - m_buildCacheStartIndex;
    if ((i >= 0) && (i < m_blocksize))
    {
      m_buildCache[i] = paramInt;
      m_firstFree += 1;
    }
    else
    {
      int j = m_firstFree >>> m_SHIFT;
      int k = m_firstFree & m_MASK;
      if (j >= m_map.length)
      {
        int m = j + m_numblocks;
        int[][] arrayOfInt1 = new int[m][];
        System.arraycopy(m_map, 0, arrayOfInt1, 0, m_map.length);
        m_map = arrayOfInt1;
      }
      int[] arrayOfInt = m_map[j];
      if (null == arrayOfInt) {
        arrayOfInt = m_map[j] = new int[m_blocksize];
      }
      arrayOfInt[k] = paramInt;
      m_buildCache = arrayOfInt;
      m_buildCacheStartIndex = (m_firstFree - k);
      m_firstFree += 1;
    }
  }
  
  private void addElements(int paramInt1, int paramInt2)
  {
    int i;
    if (m_firstFree + paramInt2 < m_blocksize)
    {
      for (i = 0; i < paramInt2; i++) {
        m_map0[(m_firstFree++)] = paramInt1;
      }
    }
    else
    {
      i = m_firstFree >>> m_SHIFT;
      int j = m_firstFree & m_MASK;
      m_firstFree += paramInt2;
      while (paramInt2 > 0)
      {
        if (i >= m_map.length)
        {
          int k = i + m_numblocks;
          int[][] arrayOfInt1 = new int[k][];
          System.arraycopy(m_map, 0, arrayOfInt1, 0, m_map.length);
          m_map = arrayOfInt1;
        }
        int[] arrayOfInt = m_map[i];
        if (null == arrayOfInt) {
          arrayOfInt = m_map[i] = new int[m_blocksize];
        }
        int m = m_blocksize - j < paramInt2 ? m_blocksize - j : paramInt2;
        paramInt2 -= m;
        while (m-- > 0) {
          arrayOfInt[(j++)] = paramInt1;
        }
        i++;
        j = 0;
      }
    }
  }
  
  private void addElements(int paramInt)
  {
    int i = m_firstFree + paramInt;
    if (i > m_blocksize)
    {
      int j = m_firstFree >>> m_SHIFT;
      int k = m_firstFree + paramInt >>> m_SHIFT;
      for (int m = j + 1; m <= k; m++) {
        m_map[m] = new int[m_blocksize];
      }
    }
    m_firstFree = i;
  }
  
  private void insertElementAt(int paramInt1, int paramInt2)
  {
    if (paramInt2 == m_firstFree)
    {
      addElement(paramInt1);
    }
    else
    {
      int i;
      int m;
      if (paramInt2 > m_firstFree)
      {
        i = paramInt2 >>> m_SHIFT;
        if (i >= m_map.length)
        {
          int j = i + m_numblocks;
          int[][] arrayOfInt = new int[j][];
          System.arraycopy(m_map, 0, arrayOfInt, 0, m_map.length);
          m_map = arrayOfInt;
        }
        int[] arrayOfInt1 = m_map[i];
        if (null == arrayOfInt1) {
          arrayOfInt1 = m_map[i] = new int[m_blocksize];
        }
        m = paramInt2 & m_MASK;
        arrayOfInt1[m] = paramInt1;
        m_firstFree = (m + 1);
      }
      else
      {
        i = paramInt2 >>> m_SHIFT;
        int k = m_firstFree >>> m_SHIFT;
        m_firstFree += 1;
        m = paramInt2 & m_MASK;
        while (i <= k)
        {
          int i1 = m_blocksize - m - 1;
          int[] arrayOfInt2 = m_map[i];
          int n;
          if (null == arrayOfInt2)
          {
            n = 0;
            arrayOfInt2 = m_map[i] = new int[m_blocksize];
          }
          else
          {
            n = arrayOfInt2[(m_blocksize - 1)];
            System.arraycopy(arrayOfInt2, m, arrayOfInt2, m + 1, i1);
          }
          arrayOfInt2[m] = paramInt1;
          paramInt1 = n;
          m = 0;
          i++;
        }
      }
    }
  }
  
  public void removeAllElements()
  {
    m_firstFree = 0;
    m_buildCache = m_map0;
    m_buildCacheStartIndex = 0;
  }
  
  private boolean removeElement(int paramInt)
  {
    int i = indexOf(paramInt, 0);
    if (i < 0) {
      return false;
    }
    removeElementAt(i);
    return true;
  }
  
  private void removeElementAt(int paramInt)
  {
    if (paramInt < m_firstFree)
    {
      int i = paramInt >>> m_SHIFT;
      int j = m_firstFree >>> m_SHIFT;
      int k = paramInt & m_MASK;
      while (i <= j)
      {
        int m = m_blocksize - k - 1;
        int[] arrayOfInt1 = m_map[i];
        if (null == arrayOfInt1) {
          arrayOfInt1 = m_map[i] = new int[m_blocksize];
        } else {
          System.arraycopy(arrayOfInt1, k + 1, arrayOfInt1, k, m);
        }
        if (i < j)
        {
          int[] arrayOfInt2 = m_map[(i + 1)];
          if (arrayOfInt2 != null) {
            arrayOfInt1[(m_blocksize - 1)] = (arrayOfInt2 != null ? arrayOfInt2[0] : 0);
          }
        }
        else
        {
          arrayOfInt1[(m_blocksize - 1)] = 0;
        }
        k = 0;
        i++;
      }
    }
    m_firstFree -= 1;
  }
  
  public void setElementAt(int paramInt1, int paramInt2)
  {
    if (paramInt2 < m_blocksize)
    {
      m_map0[paramInt2] = paramInt1;
    }
    else
    {
      int i = paramInt2 >>> m_SHIFT;
      int j = paramInt2 & m_MASK;
      if (i >= m_map.length)
      {
        int k = i + m_numblocks;
        int[][] arrayOfInt1 = new int[k][];
        System.arraycopy(m_map, 0, arrayOfInt1, 0, m_map.length);
        m_map = arrayOfInt1;
      }
      int[] arrayOfInt = m_map[i];
      if (null == arrayOfInt) {
        arrayOfInt = m_map[i] = new int[m_blocksize];
      }
      arrayOfInt[j] = paramInt1;
    }
    if (paramInt2 >= m_firstFree) {
      m_firstFree = (paramInt2 + 1);
    }
  }
  
  public int elementAt(int paramInt)
  {
    if (paramInt < m_blocksize) {
      return m_map0[paramInt];
    }
    return m_map[(paramInt >>> m_SHIFT)][(paramInt & m_MASK)];
  }
  
  private boolean contains(int paramInt)
  {
    return indexOf(paramInt, 0) >= 0;
  }
  
  public int indexOf(int paramInt1, int paramInt2)
  {
    if (paramInt2 >= m_firstFree) {
      return -1;
    }
    int i = paramInt2 >>> m_SHIFT;
    int j = paramInt2 & m_MASK;
    int k = m_firstFree >>> m_SHIFT;
    while (i < k)
    {
      arrayOfInt = m_map[i];
      if (arrayOfInt != null) {
        for (m = j; m < m_blocksize; m++) {
          if (arrayOfInt[m] == paramInt1) {
            return m + i * m_blocksize;
          }
        }
      }
      j = 0;
      i++;
    }
    int m = m_firstFree & m_MASK;
    int[] arrayOfInt = m_map[k];
    for (int n = j; n < m; n++) {
      if (arrayOfInt[n] == paramInt1) {
        return n + k * m_blocksize;
      }
    }
    return -1;
  }
  
  public int indexOf(int paramInt)
  {
    return indexOf(paramInt, 0);
  }
  
  private int lastIndexOf(int paramInt)
  {
    int i = m_firstFree & m_MASK;
    for (int j = m_firstFree >>> m_SHIFT; j >= 0; j--)
    {
      int[] arrayOfInt = m_map[j];
      if (arrayOfInt != null) {
        for (int k = i; k >= 0; k--) {
          if (arrayOfInt[k] == paramInt) {
            return k + j * m_blocksize;
          }
        }
      }
      i = 0;
    }
    return -1;
  }
  
  public final int[] getMap0()
  {
    return m_map0;
  }
  
  public final int[][] getMap()
  {
    return m_map;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\SuballocatedIntVector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */