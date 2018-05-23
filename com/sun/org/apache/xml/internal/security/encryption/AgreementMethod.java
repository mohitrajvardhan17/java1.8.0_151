package com.sun.org.apache.xml.internal.security.encryption;

import com.sun.org.apache.xml.internal.security.keys.KeyInfo;
import java.util.Iterator;
import org.w3c.dom.Element;

public abstract interface AgreementMethod
{
  public abstract byte[] getKANonce();
  
  public abstract void setKANonce(byte[] paramArrayOfByte);
  
  public abstract Iterator<Element> getAgreementMethodInformation();
  
  public abstract void addAgreementMethodInformation(Element paramElement);
  
  public abstract void revoveAgreementMethodInformation(Element paramElement);
  
  public abstract KeyInfo getOriginatorKeyInfo();
  
  public abstract void setOriginatorKeyInfo(KeyInfo paramKeyInfo);
  
  public abstract KeyInfo getRecipientKeyInfo();
  
  public abstract void setRecipientKeyInfo(KeyInfo paramKeyInfo);
  
  public abstract String getAlgorithm();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\encryption\AgreementMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */