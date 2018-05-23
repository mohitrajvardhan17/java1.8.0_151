package com.sun.org.apache.xml.internal.security.encryption;

import java.util.Iterator;
import org.w3c.dom.Element;

public abstract interface EncryptionMethod
{
  public abstract String getAlgorithm();
  
  public abstract int getKeySize();
  
  public abstract void setKeySize(int paramInt);
  
  public abstract byte[] getOAEPparams();
  
  public abstract void setOAEPparams(byte[] paramArrayOfByte);
  
  public abstract void setDigestAlgorithm(String paramString);
  
  public abstract String getDigestAlgorithm();
  
  public abstract void setMGFAlgorithm(String paramString);
  
  public abstract String getMGFAlgorithm();
  
  public abstract Iterator<Element> getEncryptionMethodInformation();
  
  public abstract void addEncryptionMethodInformation(Element paramElement);
  
  public abstract void removeEncryptionMethodInformation(Element paramElement);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\encryption\EncryptionMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */