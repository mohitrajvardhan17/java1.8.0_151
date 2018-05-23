package com.sun.org.apache.xml.internal.utils;

import java.io.Serializable;

public class StringVector
  implements Serializable
{
  static final long serialVersionUID = 4995234972032919748L;
  protected int m_blocksize;
  protected String[] m_map;
  protected int m_firstFree = 0;
  protected int m_mapSize;
  
  public StringVector()
  {
    m_blocksize = 8;
    m_mapSize = m_blocksize;
    m_map = new String[m_blocksize];
  }
  
  public StringVector(int paramInt)
  {
    m_blocksize = paramInt;
    m_mapSize = paramInt;
    m_map = new String[paramInt];
  }
  
  public int getLength()
  {
    return m_firstFree;
  }
  
  public final int size()
  {
    return m_firstFree;
  }
  
  public final void addElement(String paramString)
  {
    if (m_firstFree + 1 >= m_mapSize)
    {
      m_mapSize += m_blocksize;
      String[] arrayOfString = new String[m_mapSize];
      System.arraycopy(m_map, 0, arrayOfString, 0, m_firstFree + 1);
      m_map = arrayOfString;
    }
    m_map[m_firstFree] = paramString;
    m_firstFree += 1;
  }
  
  public final String elementAt(int paramInt)
  {
    return m_map[paramInt];
  }
  
  public final boolean contains(String paramString)
  {
    if (null == paramString) {
      return false;
    }
    for (int i = 0; i < m_firstFree; i++) {
      if (m_map[i].equals(paramString)) {
        return true;
      }
    }
    return false;
  }
  
  public final boolean containsIgnoreCase(String paramString)
  {
    if (null == paramString) {
      return false;
    }
    for (int i = 0; i < m_firstFree; i++) {
      if (m_map[i].equalsIgnoreCase(paramString)) {
        return true;
      }
    }
    return false;
  }
  
  public final void push(String paramString)
  {
    if (m_firstFree + 1 >= m_mapSize)
    {
      m_mapSize += m_blocksize;
      String[] arrayOfString = new String[m_mapSize];
      System.arraycopy(m_map, 0, arrayOfString, 0, m_firstFree + 1);
      m_map = arrayOfString;
    }
    m_map[m_firstFree] = paramString;
    m_firstFree += 1;
  }
  
  public final String pop()
  {
    if (m_firstFree <= 0) {
      return null;
    }
    m_firstFree -= 1;
    String str = m_map[m_firstFree];
    m_map[m_firstFree] = null;
    return str;
  }
  
  public final String peek()
  {
    return m_firstFree <= 0 ? null : m_map[(m_firstFree - 1)];
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\StringVector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */