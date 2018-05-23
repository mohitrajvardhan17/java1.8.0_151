package com.sun.org.apache.xml.internal.security.encryption;

public abstract interface CipherData
{
  public static final int VALUE_TYPE = 1;
  public static final int REFERENCE_TYPE = 2;
  
  public abstract int getDataType();
  
  public abstract CipherValue getCipherValue();
  
  public abstract void setCipherValue(CipherValue paramCipherValue)
    throws XMLEncryptionException;
  
  public abstract CipherReference getCipherReference();
  
  public abstract void setCipherReference(CipherReference paramCipherReference)
    throws XMLEncryptionException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\encryption\CipherData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */