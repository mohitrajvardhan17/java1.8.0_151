package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;

public abstract interface XMLEntityDescription
  extends XMLResourceIdentifier
{
  public abstract void setEntityName(String paramString);
  
  public abstract String getEntityName();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\XMLEntityDescription.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */