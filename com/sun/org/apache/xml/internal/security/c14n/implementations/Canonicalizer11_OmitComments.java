package com.sun.org.apache.xml.internal.security.c14n.implementations;

public class Canonicalizer11_OmitComments
  extends Canonicalizer11
{
  public Canonicalizer11_OmitComments()
  {
    super(false);
  }
  
  public final String engineGetURI()
  {
    return "http://www.w3.org/2006/12/xml-c14n11";
  }
  
  public final boolean engineGetIncludeComments()
  {
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\c14n\implementations\Canonicalizer11_OmitComments.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */