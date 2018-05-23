package com.sun.org.apache.xml.internal.utils;

public class SuballocatedByteVector
{
  protected int m_blocksize;
  protected int m_numblocks = 32;
  protected byte[][] m_map;
  protected int m_firstFree = 0;
  protected byte[] m_map0;
  
  public SuballocatedByteVector()
  {
    this(2048);
  }
  
  public SuballocatedByteVector(int paramInt)
  {
    m_blocksize = paramInt;
    m_map0 = new byte[paramInt];
    m_map = new byte[m_numblocks][];
    m_map[0] = m_map0;
  }
  
  public SuballocatedByteVector(int paramInt1, int paramInt2)
  {
    this(paramInt1);
  }
  
  public int size()
  {
    return m_firstFree;
  }
  
  private void setSize(int paramInt)
  {
    if (m_firstFree < paramInt) {
      m_firstFree = paramInt;
    }
  }
  
  public void addElement(byte paramByte)
  {
    if (m_firstFree < m_blocksize)
    {
      m_map0[(m_firstFree++)] = paramByte;
    }
    else
    {
      int i = m_firstFree / m_blocksize;
      int j = m_firstFree % m_blocksize;
      m_firstFree += 1;
      if (i >= m_map.length)
      {
        int k = i + m_numblocks;
        byte[][] arrayOfByte1 = new byte[k][];
        System.arraycopy(m_map, 0, arrayOfByte1, 0, m_map.length);
        m_map = arrayOfByte1;
      }
      byte[] arrayOfByte = m_map[i];
      if (null == arrayOfByte) {
        arrayOfByte = m_map[i] = new byte[m_blocksize];
      }
      arrayOfByte[j] = paramByte;
    }
  }
  
  private void addElements(byte paramByte, int paramInt)
  {
    int i;
    if (m_firstFree + paramInt < m_blocksize)
    {
      for (i = 0; i < paramInt; i++) {
        m_map0[(m_firstFree++)] = paramByte;
      }
    }
    else
    {
      i = m_firstFree / m_blocksize;
      int j = m_firstFree % m_blocksize;
      m_firstFree += paramInt;
      while (paramInt > 0)
      {
        if (i >= m_map.length)
        {
          int k = i + m_numblocks;
          byte[][] arrayOfByte1 = new byte[k][];
          System.arraycopy(m_map, 0, arrayOfByte1, 0, m_map.length);
          m_map = arrayOfByte1;
        }
        byte[] arrayOfByte = m_map[i];
        if (null == arrayOfByte) {
          arrayOfByte = m_map[i] = new byte[m_blocksize];
        }
        int m = m_blocksize - j < paramInt ? m_blocksize - j : paramInt;
        paramInt -= m;
        while (m-- > 0) {
          arrayOfByte[(j++)] = paramByte;
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
      int j = m_firstFree % m_blocksize;
      int k = (m_firstFree + paramInt) % m_blocksize;
      for (int m = j + 1; m <= k; m++) {
        m_map[m] = new byte[m_blocksize];
      }
    }
    m_firstFree = i;
  }
  
  private void insertElementAt(byte paramByte, int paramInt)
  {
    if (paramInt == m_firstFree)
    {
      addElement(paramByte);
    }
    else
    {
      int i;
      int m;
      if (paramInt > m_firstFree)
      {
        i = paramInt / m_blocksize;
        if (i >= m_map.length)
        {
          int j = i + m_numblocks;
          byte[][] arrayOfByte = new byte[j][];
          System.arraycopy(m_map, 0, arrayOfByte, 0, m_map.length);
          m_map = arrayOfByte;
        }
        byte[] arrayOfByte1 = m_map[i];
        if (null == arrayOfByte1) {
          arrayOfByte1 = m_map[i] = new byte[m_blocksize];
        }
        m = paramInt % m_blocksize;
        arrayOfByte1[m] = paramByte;
        m_firstFree = (m + 1);
      }
      else
      {
        i = paramInt / m_blocksize;
        int k = m_firstFree + 1 / m_blocksize;
        m_firstFree += 1;
        m = paramInt % m_blocksize;
        while (i <= k)
        {
          int n = m_blocksize - m - 1;
          byte[] arrayOfByte2 = m_map[i];
          byte b;
          if (null == arrayOfByte2)
          {
            b = 0;
            arrayOfByte2 = m_map[i] = new byte[m_blocksize];
          }
          else
          {
            b = arrayOfByte2[(m_blocksize - 1)];
            System.arraycopy(arrayOfByte2, m, arrayOfByte2, m + 1, n);
          }
          arrayOfByte2[m] = paramByte;
          paramByte = b;
          m = 0;
          i++;
        }
      }
    }
  }
  
  public void removeAllElements()
  {
    m_firstFree = 0;
  }
  
  private boolean removeElement(byte paramByte)
  {
    int i = indexOf(paramByte, 0);
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
      int i = paramInt / m_blocksize;
      int j = m_firstFree / m_blocksize;
      int k = paramInt % m_blocksize;
      while (i <= j)
      {
        int m = m_blocksize - k - 1;
        byte[] arrayOfByte1 = m_map[i];
        if (null == arrayOfByte1) {
          arrayOfByte1 = m_map[i] = new byte[m_blocksize];
        } else {
          System.arraycopy(arrayOfByte1, k + 1, arrayOfByte1, k, m);
        }
        if (i < j)
        {
          byte[] arrayOfByte2 = m_map[(i + 1)];
          if (arrayOfByte2 != null) {
            arrayOfByte1[(m_blocksize - 1)] = (arrayOfByte2 != null ? arrayOfByte2[0] : 0);
          }
        }
        else
        {
          arrayOfByte1[(m_blocksize - 1)] = 0;
        }
        k = 0;
        i++;
      }
    }
    m_firstFree -= 1;
  }
  
  public void setElementAt(byte paramByte, int paramInt)
  {
    if (paramInt < m_blocksize)
    {
      m_map0[paramInt] = paramByte;
      return;
    }
    int i = paramInt / m_blocksize;
    int j = paramInt % m_blocksize;
    if (i >= m_map.length)
    {
      int k = i + m_numblocks;
      byte[][] arrayOfByte1 = new byte[k][];
      System.arraycopy(m_map, 0, arrayOfByte1, 0, m_map.length);
      m_map = arrayOfByte1;
    }
    byte[] arrayOfByte = m_map[i];
    if (null == arrayOfByte) {
      arrayOfByte = m_map[i] = new byte[m_blocksize];
    }
    arrayOfByte[j] = paramByte;
    if (paramInt >= m_firstFree) {
      m_firstFree = (paramInt + 1);
    }
  }
  
  public byte elementAt(int paramInt)
  {
    if (paramInt < m_blocksize) {
      return m_map0[paramInt];
    }
    return m_map[(paramInt / m_blocksize)][(paramInt % m_blocksize)];
  }
  
  private boolean contains(byte paramByte)
  {
    return indexOf(paramByte, 0) >= 0;
  }
  
  public int indexOf(byte paramByte, int paramInt)
  {
    if (paramInt >= m_firstFree) {
      return -1;
    }
    int i = paramInt / m_blocksize;
    int j = paramInt % m_blocksize;
    int k = m_firstFree / m_blocksize;
    while (i < k)
    {
      arrayOfByte = m_map[i];
      if (arrayOfByte != null) {
        for (m = j; m < m_blocksize; m++) {
          if (arrayOfByte[m] == paramByte) {
            return m + i * m_blocksize;
          }
        }
      }
      j = 0;
      i++;
    }
    int m = m_firstFree % m_blocksize;
    byte[] arrayOfByte = m_map[k];
    for (int n = j; n < m; n++) {
      if (arrayOfByte[n] == paramByte) {
        return n + k * m_blocksize;
      }
    }
    return -1;
  }
  
  public int indexOf(byte paramByte)
  {
    return indexOf(paramByte, 0);
  }
  
  private int lastIndexOf(byte paramByte)
  {
    int i = m_firstFree % m_blocksize;
    for (int j = m_firstFree / m_blocksize; j >= 0; j--)
    {
      byte[] arrayOfByte = m_map[j];
      if (arrayOfByte != null) {
        for (int k = i; k >= 0; k--) {
          if (arrayOfByte[k] == paramByte) {
            return k + j * m_blocksize;
          }
        }
      }
      i = 0;
    }
    return -1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\SuballocatedByteVector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */