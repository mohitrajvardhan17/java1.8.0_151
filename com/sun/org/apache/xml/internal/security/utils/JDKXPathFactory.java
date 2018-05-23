package com.sun.org.apache.xml.internal.security.utils;

public class JDKXPathFactory
  extends XPathFactory
{
  public JDKXPathFactory() {}
  
  public XPathAPI newXPathAPI()
  {
    return new JDKXPathAPI();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\utils\JDKXPathFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */