package com.sun.org.apache.xerces.internal.xs;

public abstract interface XSModelGroup
  extends XSTerm
{
  public static final short COMPOSITOR_SEQUENCE = 1;
  public static final short COMPOSITOR_CHOICE = 2;
  public static final short COMPOSITOR_ALL = 3;
  
  public abstract short getCompositor();
  
  public abstract XSObjectList getParticles();
  
  public abstract XSAnnotation getAnnotation();
  
  public abstract XSObjectList getAnnotations();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xs\XSModelGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */