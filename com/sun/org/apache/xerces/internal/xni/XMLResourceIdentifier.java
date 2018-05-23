package com.sun.org.apache.xerces.internal.xni;

public abstract interface XMLResourceIdentifier
{
  public abstract void setPublicId(String paramString);
  
  public abstract String getPublicId();
  
  public abstract void setExpandedSystemId(String paramString);
  
  public abstract String getExpandedSystemId();
  
  public abstract void setLiteralSystemId(String paramString);
  
  public abstract String getLiteralSystemId();
  
  public abstract void setBaseSystemId(String paramString);
  
  public abstract String getBaseSystemId();
  
  public abstract void setNamespace(String paramString);
  
  public abstract String getNamespace();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xni\XMLResourceIdentifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */