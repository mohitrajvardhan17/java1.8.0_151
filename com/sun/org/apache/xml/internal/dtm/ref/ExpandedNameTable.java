package com.sun.org.apache.xml.internal.dtm.ref;

public class ExpandedNameTable
{
  private ExtendedType[] m_extendedTypes;
  private static int m_initialSize = 128;
  private int m_nextType;
  public static final int ELEMENT = 1;
  public static final int ATTRIBUTE = 2;
  public static final int TEXT = 3;
  public static final int CDATA_SECTION = 4;
  public static final int ENTITY_REFERENCE = 5;
  public static final int ENTITY = 6;
  public static final int PROCESSING_INSTRUCTION = 7;
  public static final int COMMENT = 8;
  public static final int DOCUMENT = 9;
  public static final int DOCUMENT_TYPE = 10;
  public static final int DOCUMENT_FRAGMENT = 11;
  public static final int NOTATION = 12;
  public static final int NAMESPACE = 13;
  ExtendedType hashET = new ExtendedType(-1, "", "");
  private static ExtendedType[] m_defaultExtendedTypes;
  private static float m_loadFactor = 0.75F;
  private static int m_initialCapacity = 203;
  private int m_capacity = m_initialCapacity;
  private int m_threshold = (int)(m_capacity * m_loadFactor);
  private HashEntry[] m_table = new HashEntry[m_capacity];
  
  public ExpandedNameTable()
  {
    initExtendedTypes();
  }
  
  private void initExtendedTypes()
  {
    m_extendedTypes = new ExtendedType[m_initialSize];
    for (int i = 0; i < 14; i++)
    {
      m_extendedTypes[i] = m_defaultExtendedTypes[i];
      m_table[i] = new HashEntry(m_defaultExtendedTypes[i], i, i, null);
    }
    m_nextType = 14;
  }
  
  public int getExpandedTypeID(String paramString1, String paramString2, int paramInt)
  {
    return getExpandedTypeID(paramString1, paramString2, paramInt, false);
  }
  
  public int getExpandedTypeID(String paramString1, String paramString2, int paramInt, boolean paramBoolean)
  {
    if (null == paramString1) {
      paramString1 = "";
    }
    if (null == paramString2) {
      paramString2 = "";
    }
    int i = paramInt + paramString1.hashCode() + paramString2.hashCode();
    hashET.redefine(paramInt, paramString1, paramString2, i);
    int j = i % m_capacity;
    if (j < 0) {
      j = -j;
    }
    for (Object localObject1 = m_table[j]; localObject1 != null; localObject1 = next) {
      if ((hash == i) && (key.equals(hashET))) {
        return value;
      }
    }
    if (paramBoolean) {
      return -1;
    }
    if (m_nextType > m_threshold)
    {
      rehash();
      j = i % m_capacity;
      if (j < 0) {
        j = -j;
      }
    }
    localObject1 = new ExtendedType(paramInt, paramString1, paramString2, i);
    if (m_extendedTypes.length == m_nextType)
    {
      localObject2 = new ExtendedType[m_extendedTypes.length * 2];
      System.arraycopy(m_extendedTypes, 0, localObject2, 0, m_extendedTypes.length);
      m_extendedTypes = ((ExtendedType[])localObject2);
    }
    m_extendedTypes[m_nextType] = localObject1;
    Object localObject2 = new HashEntry((ExtendedType)localObject1, m_nextType, i, m_table[j]);
    m_table[j] = localObject2;
    return m_nextType++;
  }
  
  private void rehash()
  {
    int i = m_capacity;
    HashEntry[] arrayOfHashEntry = m_table;
    int j = 2 * i + 1;
    m_capacity = j;
    m_threshold = ((int)(j * m_loadFactor));
    m_table = new HashEntry[j];
    for (int k = i - 1; k >= 0; k--)
    {
      HashEntry localHashEntry1 = arrayOfHashEntry[k];
      while (localHashEntry1 != null)
      {
        HashEntry localHashEntry2 = localHashEntry1;
        localHashEntry1 = next;
        int m = hash % j;
        if (m < 0) {
          m = -m;
        }
        next = m_table[m];
        m_table[m] = localHashEntry2;
      }
    }
  }
  
  public int getExpandedTypeID(int paramInt)
  {
    return paramInt;
  }
  
  public String getLocalName(int paramInt)
  {
    return m_extendedTypes[paramInt].getLocalName();
  }
  
  public final int getLocalNameID(int paramInt)
  {
    if (m_extendedTypes[paramInt].getLocalName().equals("")) {
      return 0;
    }
    return paramInt;
  }
  
  public String getNamespace(int paramInt)
  {
    String str = m_extendedTypes[paramInt].getNamespace();
    return str.equals("") ? null : str;
  }
  
  public final int getNamespaceID(int paramInt)
  {
    if (m_extendedTypes[paramInt].getNamespace().equals("")) {
      return 0;
    }
    return paramInt;
  }
  
  public final short getType(int paramInt)
  {
    return (short)m_extendedTypes[paramInt].getNodeType();
  }
  
  public int getSize()
  {
    return m_nextType;
  }
  
  public ExtendedType[] getExtendedTypes()
  {
    return m_extendedTypes;
  }
  
  static
  {
    m_defaultExtendedTypes = new ExtendedType[14];
    for (int i = 0; i < 14; i++) {
      m_defaultExtendedTypes[i] = new ExtendedType(i, "", "");
    }
  }
  
  private static final class HashEntry
  {
    ExtendedType key;
    int value;
    int hash;
    HashEntry next;
    
    protected HashEntry(ExtendedType paramExtendedType, int paramInt1, int paramInt2, HashEntry paramHashEntry)
    {
      key = paramExtendedType;
      value = paramInt1;
      hash = paramInt2;
      next = paramHashEntry;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\ExpandedNameTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */