package com.sun.org.apache.xml.internal.utils;

import java.io.Serializable;

public class NameSpace
  implements Serializable
{
  static final long serialVersionUID = 1471232939184881839L;
  public NameSpace m_next = null;
  public String m_prefix;
  public String m_uri;
  
  public NameSpace(String paramString1, String paramString2)
  {
    m_prefix = paramString1;
    m_uri = paramString2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\NameSpace.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */