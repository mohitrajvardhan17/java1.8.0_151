package com.sun.org.apache.xml.internal.utils;

public class StringToStringTable
{
  private int m_blocksize;
  private String[] m_map;
  private int m_firstFree = 0;
  private int m_mapSize;
  
  public StringToStringTable()
  {
    m_blocksize = 16;
    m_mapSize = m_blocksize;
    m_map = new String[m_blocksize];
  }
  
  public StringToStringTable(int paramInt)
  {
    m_blocksize = paramInt;
    m_mapSize = paramInt;
    m_map = new String[paramInt];
  }
  
  public final int getLength()
  {
    return m_firstFree;
  }
  
  public final void put(String paramString1, String paramString2)
  {
    if (m_firstFree + 2 >= m_mapSize)
    {
      m_mapSize += m_blocksize;
      String[] arrayOfString = new String[m_mapSize];
      System.arraycopy(m_map, 0, arrayOfString, 0, m_firstFree + 1);
      m_map = arrayOfString;
    }
    m_map[m_firstFree] = paramString1;
    m_firstFree += 1;
    m_map[m_firstFree] = paramString2;
    m_firstFree += 1;
  }
  
  public final String get(String paramString)
  {
    for (int i = 0; i < m_firstFree; i += 2) {
      if (m_map[i].equals(paramString)) {
        return m_map[(i + 1)];
      }
    }
    return null;
  }
  
  public final void remove(String paramString)
  {
    for (int i = 0; i < m_firstFree; i += 2) {
      if (m_map[i].equals(paramString))
      {
        if (i + 2 < m_firstFree) {
          System.arraycopy(m_map, i + 2, m_map, i, m_firstFree - (i + 2));
        }
        m_firstFree -= 2;
        m_map[m_firstFree] = null;
        m_map[(m_firstFree + 1)] = null;
        break;
      }
    }
  }
  
  public final String getIgnoreCase(String paramString)
  {
    if (null == paramString) {
      return null;
    }
    for (int i = 0; i < m_firstFree; i += 2) {
      if (m_map[i].equalsIgnoreCase(paramString)) {
        return m_map[(i + 1)];
      }
    }
    return null;
  }
  
  public final String getByValue(String paramString)
  {
    for (int i = 1; i < m_firstFree; i += 2) {
      if (m_map[i].equals(paramString)) {
        return m_map[(i - 1)];
      }
    }
    return null;
  }
  
  public final String elementAt(int paramInt)
  {
    return m_map[paramInt];
  }
  
  public final boolean contains(String paramString)
  {
    for (int i = 0; i < m_firstFree; i += 2) {
      if (m_map[i].equals(paramString)) {
        return true;
      }
    }
    return false;
  }
  
  public final boolean containsValue(String paramString)
  {
    for (int i = 1; i < m_firstFree; i += 2) {
      if (m_map[i].equals(paramString)) {
        return true;
      }
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\StringToStringTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */