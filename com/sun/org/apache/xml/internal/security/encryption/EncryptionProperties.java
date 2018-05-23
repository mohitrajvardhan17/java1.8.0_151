package com.sun.org.apache.xml.internal.security.encryption;

import java.util.Iterator;

public abstract interface EncryptionProperties
{
  public abstract String getId();
  
  public abstract void setId(String paramString);
  
  public abstract Iterator<EncryptionProperty> getEncryptionProperties();
  
  public abstract void addEncryptionProperty(EncryptionProperty paramEncryptionProperty);
  
  public abstract void removeEncryptionProperty(EncryptionProperty paramEncryptionProperty);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\encryption\EncryptionProperties.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */