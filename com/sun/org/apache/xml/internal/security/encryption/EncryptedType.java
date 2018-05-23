package com.sun.org.apache.xml.internal.security.encryption;

import com.sun.org.apache.xml.internal.security.keys.KeyInfo;

public abstract interface EncryptedType
{
  public abstract String getId();
  
  public abstract void setId(String paramString);
  
  public abstract String getType();
  
  public abstract void setType(String paramString);
  
  public abstract String getMimeType();
  
  public abstract void setMimeType(String paramString);
  
  public abstract String getEncoding();
  
  public abstract void setEncoding(String paramString);
  
  public abstract EncryptionMethod getEncryptionMethod();
  
  public abstract void setEncryptionMethod(EncryptionMethod paramEncryptionMethod);
  
  public abstract KeyInfo getKeyInfo();
  
  public abstract void setKeyInfo(KeyInfo paramKeyInfo);
  
  public abstract CipherData getCipherData();
  
  public abstract EncryptionProperties getEncryptionProperties();
  
  public abstract void setEncryptionProperties(EncryptionProperties paramEncryptionProperties);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\encryption\EncryptedType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */