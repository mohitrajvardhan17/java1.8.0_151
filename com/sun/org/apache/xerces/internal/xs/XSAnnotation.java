package com.sun.org.apache.xerces.internal.xs;

public abstract interface XSAnnotation
  extends XSObject
{
  public static final short W3C_DOM_ELEMENT = 1;
  public static final short SAX_CONTENTHANDLER = 2;
  public static final short W3C_DOM_DOCUMENT = 3;
  
  public abstract boolean writeAnnotation(Object paramObject, short paramShort);
  
  public abstract String getAnnotationString();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xs\XSAnnotation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */