package com.sun.org.apache.xerces.internal.impl.xs.identity;

public abstract interface FieldActivator
{
  public abstract void startValueScopeFor(IdentityConstraint paramIdentityConstraint, int paramInt);
  
  public abstract XPathMatcher activateField(Field paramField, int paramInt);
  
  public abstract void setMayMatch(Field paramField, Boolean paramBoolean);
  
  public abstract Boolean mayMatch(Field paramField);
  
  public abstract void endValueScopeFor(IdentityConstraint paramIdentityConstraint, int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\identity\FieldActivator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */