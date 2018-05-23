package com.sun.org.apache.xerces.internal.xs;

public abstract interface XSAttributeUse
  extends XSObject
{
  public abstract boolean getRequired();
  
  public abstract XSAttributeDeclaration getAttrDeclaration();
  
  public abstract short getConstraintType();
  
  public abstract String getConstraintValue();
  
  public abstract Object getActualVC()
    throws XSException;
  
  public abstract short getActualVCType()
    throws XSException;
  
  public abstract ShortList getItemValueTypes()
    throws XSException;
  
  public abstract XSObjectList getAnnotations();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xs\XSAttributeUse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */