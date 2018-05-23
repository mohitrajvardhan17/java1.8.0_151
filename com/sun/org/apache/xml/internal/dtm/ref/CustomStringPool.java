package com.sun.org.apache.xml.internal.dtm.ref;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class CustomStringPool
  extends DTMStringPool
{
  final Map<String, Integer> m_stringToInt = new HashMap();
  public static final int NULL = -1;
  
  public CustomStringPool() {}
  
  public void removeAllElements()
  {
    m_intToString.removeAllElements();
    if (m_stringToInt != null) {
      m_stringToInt.clear();
    }
  }
  
  public String indexToString(int paramInt)
    throws ArrayIndexOutOfBoundsException
  {
    return (String)m_intToString.elementAt(paramInt);
  }
  
  public int stringToIndex(String paramString)
  {
    if (paramString == null) {
      return -1;
    }
    Integer localInteger = (Integer)m_stringToInt.get(paramString);
    if (localInteger == null)
    {
      m_intToString.addElement(paramString);
      localInteger = Integer.valueOf(m_intToString.size());
      m_stringToInt.put(paramString, localInteger);
    }
    return localInteger.intValue();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\CustomStringPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */