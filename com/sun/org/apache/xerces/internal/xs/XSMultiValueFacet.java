package com.sun.org.apache.xerces.internal.xs;

public abstract interface XSMultiValueFacet
  extends XSObject
{
  public abstract short getFacetKind();
  
  public abstract StringList getLexicalFacetValues();
  
  public abstract XSObjectList getAnnotations();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xs\XSMultiValueFacet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */