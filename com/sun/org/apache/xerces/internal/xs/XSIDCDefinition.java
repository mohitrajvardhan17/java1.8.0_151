package com.sun.org.apache.xerces.internal.xs;

public abstract interface XSIDCDefinition
  extends XSObject
{
  public static final short IC_KEY = 1;
  public static final short IC_KEYREF = 2;
  public static final short IC_UNIQUE = 3;
  
  public abstract short getCategory();
  
  public abstract String getSelectorStr();
  
  public abstract StringList getFieldStrs();
  
  public abstract XSIDCDefinition getRefKey();
  
  public abstract XSObjectList getAnnotations();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xs\XSIDCDefinition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */