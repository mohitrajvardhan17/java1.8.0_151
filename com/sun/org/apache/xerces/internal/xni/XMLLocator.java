package com.sun.org.apache.xerces.internal.xni;

public abstract interface XMLLocator
{
  public abstract String getPublicId();
  
  public abstract String getLiteralSystemId();
  
  public abstract String getBaseSystemId();
  
  public abstract String getExpandedSystemId();
  
  public abstract int getLineNumber();
  
  public abstract int getColumnNumber();
  
  public abstract int getCharacterOffset();
  
  public abstract String getEncoding();
  
  public abstract String getXMLVersion();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xni\XMLLocator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */