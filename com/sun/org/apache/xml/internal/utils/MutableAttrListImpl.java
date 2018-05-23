package com.sun.org.apache.xml.internal.utils;

import java.io.Serializable;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

public class MutableAttrListImpl
  extends AttributesImpl
  implements Serializable
{
  static final long serialVersionUID = 6289452013442934470L;
  
  public MutableAttrListImpl() {}
  
  public MutableAttrListImpl(Attributes paramAttributes)
  {
    super(paramAttributes);
  }
  
  public void addAttribute(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    if (null == paramString1) {
      paramString1 = "";
    }
    int i = getIndex(paramString3);
    if (i >= 0) {
      setAttribute(i, paramString1, paramString2, paramString3, paramString4, paramString5);
    } else {
      super.addAttribute(paramString1, paramString2, paramString3, paramString4, paramString5);
    }
  }
  
  public void addAttributes(Attributes paramAttributes)
  {
    int i = paramAttributes.getLength();
    for (int j = 0; j < i; j++)
    {
      String str1 = paramAttributes.getURI(j);
      if (null == str1) {
        str1 = "";
      }
      String str2 = paramAttributes.getLocalName(j);
      String str3 = paramAttributes.getQName(j);
      int k = getIndex(str1, str2);
      if (k >= 0) {
        setAttribute(k, str1, str2, str3, paramAttributes.getType(j), paramAttributes.getValue(j));
      } else {
        addAttribute(str1, str2, str3, paramAttributes.getType(j), paramAttributes.getValue(j));
      }
    }
  }
  
  public boolean contains(String paramString)
  {
    return getValue(paramString) != null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\MutableAttrListImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */