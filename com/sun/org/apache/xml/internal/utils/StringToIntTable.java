package com.sun.org.apache.xml.internal.utils;

public class StringToIntTable
{
  public static final int INVALID_KEY = -10000;
  private int m_blocksize;
  private String[] m_map;
  private int[] m_values;
  private int m_firstFree = 0;
  private int m_mapSize;
  
  public StringToIntTable()
  {
    m_blocksize = 8;
    m_mapSize = m_blocksize;
    m_map = new String[m_blocksize];
    m_values = new int[m_blocksize];
  }
  
  public StringToIntTable(int paramInt)
  {
    m_blocksize = paramInt;
    m_mapSize = paramInt;
    m_map = new String[paramInt];
    m_values = new int[m_blocksize];
  }
  
  public final int getLength()
  {
    return m_firstFree;
  }
  
  public final void put(String paramString, int paramInt)
  {
    if (m_firstFree + 1 >= m_mapSize)
    {
      m_mapSize += m_blocksize;
      String[] arrayOfString = new String[m_mapSize];
      System.arraycopy(m_map, 0, arrayOfString, 0, m_firstFree + 1);
      m_map = arrayOfString;
      int[] arrayOfInt = new int[m_mapSize];
      System.arraycopy(m_values, 0, arrayOfInt, 0, m_firstFree + 1);
      m_values = arrayOfInt;
    }
    m_map[m_firstFree] = paramString;
    m_values[m_firstFree] = paramInt;
    m_firstFree += 1;
  }
  
  public final int get(String paramString)
  {
    for (int i = 0; i < m_firstFree; i++) {
      if (m_map[i].equals(paramString)) {
        return m_values[i];
      }
    }
    return 55536;
  }
  
  public final int getIgnoreCase(String paramString)
  {
    if (null == paramString) {
      return 55536;
    }
    for (int i = 0; i < m_firstFree; i++) {
      if (m_map[i].equalsIgnoreCase(paramString)) {
        return m_values[i];
      }
    }
    return 55536;
  }
  
  public final boolean contains(String paramString)
  {
    for (int i = 0; i < m_firstFree; i++) {
      if (m_map[i].equals(paramString)) {
        return true;
      }
    }
    return false;
  }
  
  public final String[] keys()
  {
    String[] arrayOfString = new String[m_firstFree];
    for (int i = 0; i < m_firstFree; i++) {
      arrayOfString[i] = m_map[i];
    }
    return arrayOfString;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\StringToIntTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */