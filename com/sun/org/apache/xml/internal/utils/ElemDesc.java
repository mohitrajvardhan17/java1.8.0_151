package com.sun.org.apache.xml.internal.utils;

import java.util.HashMap;
import java.util.Map;

class ElemDesc
{
  Map<String, Integer> m_attrs = null;
  int m_flags;
  static final int EMPTY = 2;
  static final int FLOW = 4;
  static final int BLOCK = 8;
  static final int BLOCKFORM = 16;
  static final int BLOCKFORMFIELDSET = 32;
  static final int CDATA = 64;
  static final int PCDATA = 128;
  static final int RAW = 256;
  static final int INLINE = 512;
  static final int INLINEA = 1024;
  static final int INLINELABEL = 2048;
  static final int FONTSTYLE = 4096;
  static final int PHRASE = 8192;
  static final int FORMCTRL = 16384;
  static final int SPECIAL = 32768;
  static final int ASPECIAL = 65536;
  static final int HEADMISC = 131072;
  static final int HEAD = 262144;
  static final int LIST = 524288;
  static final int PREFORMATTED = 1048576;
  static final int WHITESPACESENSITIVE = 2097152;
  static final int ATTRURL = 2;
  static final int ATTREMPTY = 4;
  
  ElemDesc(int paramInt)
  {
    m_flags = paramInt;
  }
  
  boolean is(int paramInt)
  {
    return (m_flags & paramInt) != 0;
  }
  
  void setAttr(String paramString, int paramInt)
  {
    if (null == m_attrs) {
      m_attrs = new HashMap();
    }
    m_attrs.put(paramString, Integer.valueOf(paramInt));
  }
  
  boolean isAttrFlagSet(String paramString, int paramInt)
  {
    if (null != m_attrs)
    {
      Integer localInteger = (Integer)m_attrs.get(paramString);
      if (null != localInteger) {
        return (localInteger.intValue() & paramInt) != 0;
      }
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\ElemDesc.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */