package com.sun.org.apache.xml.internal.serializer;

import java.util.HashMap;
import java.util.Map;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

public final class AttributesImplSerializer
  extends AttributesImpl
{
  private final Map<String, Integer> m_indexFromQName = new HashMap();
  private final StringBuffer m_buff = new StringBuffer();
  private static final int MAX = 12;
  private static final int MAXMinus1 = 11;
  
  public AttributesImplSerializer() {}
  
  public final int getIndex(String paramString)
  {
    int i;
    if (super.getLength() < 12)
    {
      i = super.getIndex(paramString);
      return i;
    }
    Integer localInteger = (Integer)m_indexFromQName.get(paramString);
    if (localInteger == null) {
      i = -1;
    } else {
      i = localInteger.intValue();
    }
    return i;
  }
  
  public final void addAttribute(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    int i = super.getLength();
    super.addAttribute(paramString1, paramString2, paramString3, paramString4, paramString5);
    if (i < 11) {
      return;
    }
    if (i == 11)
    {
      switchOverToHash(12);
    }
    else
    {
      Integer localInteger = Integer.valueOf(i);
      m_indexFromQName.put(paramString3, localInteger);
      m_buff.setLength(0);
      m_buff.append('{').append(paramString1).append('}').append(paramString2);
      String str = m_buff.toString();
      m_indexFromQName.put(str, localInteger);
    }
  }
  
  private void switchOverToHash(int paramInt)
  {
    for (int i = 0; i < paramInt; i++)
    {
      String str1 = super.getQName(i);
      Integer localInteger = Integer.valueOf(i);
      m_indexFromQName.put(str1, localInteger);
      String str2 = super.getURI(i);
      String str3 = super.getLocalName(i);
      m_buff.setLength(0);
      m_buff.append('{').append(str2).append('}').append(str3);
      String str4 = m_buff.toString();
      m_indexFromQName.put(str4, localInteger);
    }
  }
  
  public final void clear()
  {
    int i = super.getLength();
    super.clear();
    if (12 <= i) {
      m_indexFromQName.clear();
    }
  }
  
  public final void setAttributes(Attributes paramAttributes)
  {
    super.setAttributes(paramAttributes);
    int i = paramAttributes.getLength();
    if (12 <= i) {
      switchOverToHash(i);
    }
  }
  
  public final int getIndex(String paramString1, String paramString2)
  {
    int i;
    if (super.getLength() < 12)
    {
      i = super.getIndex(paramString1, paramString2);
      return i;
    }
    m_buff.setLength(0);
    m_buff.append('{').append(paramString1).append('}').append(paramString2);
    String str = m_buff.toString();
    Integer localInteger = (Integer)m_indexFromQName.get(str);
    if (localInteger == null) {
      i = -1;
    } else {
      i = localInteger.intValue();
    }
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\serializer\AttributesImplSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */