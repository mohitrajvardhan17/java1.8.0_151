package com.sun.org.apache.xml.internal.security.c14n.implementations;

public class Canonicalizer20010315ExclWithComments
  extends Canonicalizer20010315Excl
{
  public Canonicalizer20010315ExclWithComments()
  {
    super(true);
  }
  
  public final String engineGetURI()
  {
    return "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
  }
  
  public final boolean engineGetIncludeComments()
  {
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\c14n\implementations\Canonicalizer20010315ExclWithComments.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */