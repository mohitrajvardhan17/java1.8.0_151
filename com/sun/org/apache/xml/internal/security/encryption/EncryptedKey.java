package com.sun.org.apache.xml.internal.security.encryption;

public abstract interface EncryptedKey
  extends EncryptedType
{
  public abstract String getRecipient();
  
  public abstract void setRecipient(String paramString);
  
  public abstract ReferenceList getReferenceList();
  
  public abstract void setReferenceList(ReferenceList paramReferenceList);
  
  public abstract String getCarriedName();
  
  public abstract void setCarriedName(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\encryption\EncryptedKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */