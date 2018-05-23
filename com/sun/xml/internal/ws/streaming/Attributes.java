package com.sun.xml.internal.ws.streaming;

import javax.xml.namespace.QName;

public abstract interface Attributes
{
  public abstract int getLength();
  
  public abstract boolean isNamespaceDeclaration(int paramInt);
  
  public abstract QName getName(int paramInt);
  
  public abstract String getURI(int paramInt);
  
  public abstract String getLocalName(int paramInt);
  
  public abstract String getPrefix(int paramInt);
  
  public abstract String getValue(int paramInt);
  
  public abstract int getIndex(QName paramQName);
  
  public abstract int getIndex(String paramString1, String paramString2);
  
  public abstract int getIndex(String paramString);
  
  public abstract String getValue(QName paramQName);
  
  public abstract String getValue(String paramString1, String paramString2);
  
  public abstract String getValue(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\streaming\Attributes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */