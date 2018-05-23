package com.sun.org.apache.xml.internal.utils;

public class Trie
{
  public static final int ALPHA_SIZE = 128;
  Node m_Root = new Node();
  private char[] m_charBuffer = new char[0];
  
  public Trie() {}
  
  public Object put(String paramString, Object paramObject)
  {
    int i = paramString.length();
    if (i > m_charBuffer.length) {
      m_charBuffer = new char[i];
    }
    Object localObject1 = m_Root;
    for (int j = 0; j < i; j++)
    {
      Node localNode1 = m_nextChar[Character.toUpperCase(paramString.charAt(j))];
      if (localNode1 != null) {
        localObject1 = localNode1;
      } else {
        while (j < i)
        {
          Node localNode2 = new Node();
          m_nextChar[Character.toUpperCase(paramString.charAt(j))] = localNode2;
          m_nextChar[Character.toLowerCase(paramString.charAt(j))] = localNode2;
          localObject1 = localNode2;
          j++;
        }
      }
    }
    Object localObject2 = m_Value;
    m_Value = paramObject;
    return localObject2;
  }
  
  public Object get(String paramString)
  {
    int i = paramString.length();
    if (m_charBuffer.length < i) {
      return null;
    }
    Node localNode = m_Root;
    switch (i)
    {
    case 0: 
      return null;
    case 1: 
      j = paramString.charAt(0);
      if (j < 128)
      {
        localNode = m_nextChar[j];
        if (localNode != null) {
          return m_Value;
        }
      }
      return null;
    }
    paramString.getChars(0, i, m_charBuffer, 0);
    for (int j = 0; j < i; j++)
    {
      int k = m_charBuffer[j];
      if (128 <= k) {
        return null;
      }
      localNode = m_nextChar[k];
      if (localNode == null) {
        return null;
      }
    }
    return m_Value;
  }
  
  class Node
  {
    Node[] m_nextChar = new Node[''];
    Object m_Value = null;
    
    Node() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\Trie.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */