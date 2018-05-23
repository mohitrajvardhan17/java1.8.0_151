package com.sun.org.apache.xerces.internal.xs;

public abstract interface XSTypeDefinition
  extends XSObject
{
  public static final short COMPLEX_TYPE = 15;
  public static final short SIMPLE_TYPE = 16;
  
  public abstract short getTypeCategory();
  
  public abstract XSTypeDefinition getBaseType();
  
  public abstract boolean isFinal(short paramShort);
  
  public abstract short getFinal();
  
  public abstract boolean getAnonymous();
  
  public abstract boolean derivedFromType(XSTypeDefinition paramXSTypeDefinition, short paramShort);
  
  public abstract boolean derivedFrom(String paramString1, String paramString2, short paramShort);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xs\XSTypeDefinition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */