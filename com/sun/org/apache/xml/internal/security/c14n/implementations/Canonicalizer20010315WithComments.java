package com.sun.org.apache.xml.internal.security.c14n.implementations;

public class Canonicalizer20010315WithComments
  extends Canonicalizer20010315
{
  public Canonicalizer20010315WithComments()
  {
    super(true);
  }
  
  public final String engineGetURI()
  {
    return "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";
  }
  
  public final boolean engineGetIncludeComments()
  {
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\c14n\implementations\Canonicalizer20010315WithComments.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */