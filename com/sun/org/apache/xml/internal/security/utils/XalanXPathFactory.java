package com.sun.org.apache.xml.internal.security.utils;

public class XalanXPathFactory
  extends XPathFactory
{
  public XalanXPathFactory() {}
  
  public XPathAPI newXPathAPI()
  {
    return new XalanXPathAPI();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\utils\XalanXPathFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */