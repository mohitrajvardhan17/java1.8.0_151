package com.sun.org.apache.xerces.internal.xs;

public abstract interface XSImplementation
{
  public abstract StringList getRecognizedVersions();
  
  public abstract XSLoader createXSLoader(StringList paramStringList)
    throws XSException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xs\XSImplementation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */