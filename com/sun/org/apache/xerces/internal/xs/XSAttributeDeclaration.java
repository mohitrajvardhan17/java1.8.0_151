package com.sun.org.apache.xerces.internal.xs;

public abstract interface XSAttributeDeclaration
  extends XSObject
{
  public abstract XSSimpleTypeDefinition getTypeDefinition();
  
  public abstract short getScope();
  
  public abstract XSComplexTypeDefinition getEnclosingCTDefinition();
  
  public abstract short getConstraintType();
  
  public abstract String getConstraintValue();
  
  public abstract Object getActualVC()
    throws XSException;
  
  public abstract short getActualVCType()
    throws XSException;
  
  public abstract ShortList getItemValueTypes()
    throws XSException;
  
  public abstract XSAnnotation getAnnotation();
  
  public abstract XSObjectList getAnnotations();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xs\XSAttributeDeclaration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */