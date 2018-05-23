package com.sun.org.apache.xerces.internal.xs;

public abstract interface XSParticle
  extends XSObject
{
  public abstract int getMinOccurs();
  
  public abstract int getMaxOccurs();
  
  public abstract boolean getMaxOccursUnbounded();
  
  public abstract XSTerm getTerm();
  
  public abstract XSObjectList getAnnotations();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xs\XSParticle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */