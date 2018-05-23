package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xpath.internal.XPathContext;

public class XNull
  extends XNodeSet
{
  static final long serialVersionUID = -6841683711458983005L;
  
  public XNull() {}
  
  public int getType()
  {
    return -1;
  }
  
  public String getTypeString()
  {
    return "#CLASS_NULL";
  }
  
  public double num()
  {
    return 0.0D;
  }
  
  public boolean bool()
  {
    return false;
  }
  
  public String str()
  {
    return "";
  }
  
  public int rtf(XPathContext paramXPathContext)
  {
    return -1;
  }
  
  public boolean equals(XObject paramXObject)
  {
    return paramXObject.getType() == -1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\objects\XNull.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */