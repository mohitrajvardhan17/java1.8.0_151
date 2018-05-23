package com.sun.org.apache.xerces.internal.xs;

public abstract interface XSNotationDeclaration
  extends XSObject
{
  public abstract String getSystemId();
  
  public abstract String getPublicId();
  
  public abstract XSAnnotation getAnnotation();
  
  public abstract XSObjectList getAnnotations();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xs\XSNotationDeclaration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */