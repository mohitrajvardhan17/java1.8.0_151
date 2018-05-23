package com.sun.org.apache.xerces.internal.impl.xpath;

public class XPathException
  extends Exception
{
  static final long serialVersionUID = -948482312169512085L;
  private String fKey;
  
  public XPathException()
  {
    fKey = "c-general-xpath";
  }
  
  public XPathException(String paramString)
  {
    fKey = paramString;
  }
  
  public String getKey()
  {
    return fKey;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xpath\XPathException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */