package com.sun.org.apache.xml.internal.utils;

public class StringToStringTableVector
{
  private int m_blocksize;
  private StringToStringTable[] m_map;
  private int m_firstFree = 0;
  private int m_mapSize;
  
  public StringToStringTableVector()
  {
    m_blocksize = 8;
    m_mapSize = m_blocksize;
    m_map = new StringToStringTable[m_blocksize];
  }
  
  public StringToStringTableVector(int paramInt)
  {
    m_blocksize = paramInt;
    m_mapSize = paramInt;
    m_map = new StringToStringTable[paramInt];
  }
  
  public final int getLength()
  {
    return m_firstFree;
  }
  
  public final int size()
  {
    return m_firstFree;
  }
  
  public final void addElement(StringToStringTable paramStringToStringTable)
  {
    if (m_firstFree + 1 >= m_mapSize)
    {
      m_mapSize += m_blocksize;
      StringToStringTable[] arrayOfStringToStringTable = new StringToStringTable[m_mapSize];
      System.arraycopy(m_map, 0, arrayOfStringToStringTable, 0, m_firstFree + 1);
      m_map = arrayOfStringToStringTable;
    }
    m_map[m_firstFree] = paramStringToStringTable;
    m_firstFree += 1;
  }
  
  public final String get(String paramString)
  {
    for (int i = m_firstFree - 1; i >= 0; i--)
    {
      String str = m_map[i].get(paramString);
      if (str != null) {
        return str;
      }
    }
    return null;
  }
  
  public final boolean containsKey(String paramString)
  {
    for (int i = m_firstFree - 1; i >= 0; i--) {
      if (m_map[i].get(paramString) != null) {
        return true;
      }
    }
    return false;
  }
  
  public final void removeLastElem()
  {
    if (m_firstFree > 0)
    {
      m_map[m_firstFree] = null;
      m_firstFree -= 1;
    }
  }
  
  public final StringToStringTable elementAt(int paramInt)
  {
    return m_map[paramInt];
  }
  
  public final boolean contains(StringToStringTable paramStringToStringTable)
  {
    for (int i = 0; i < m_firstFree; i++) {
      if (m_map[i].equals(paramStringToStringTable)) {
        return true;
      }
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\StringToStringTableVector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */