package javax.xml.crypto.dsig.keyinfo;

import java.math.BigInteger;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.PublicKey;
import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.NoSuchMechanismException;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.XMLStructure;
import sun.security.jca.GetInstance;
import sun.security.jca.GetInstance.Instance;

public abstract class KeyInfoFactory
{
  private String mechanismType;
  private Provider provider;
  
  protected KeyInfoFactory() {}
  
  public static KeyInfoFactory getInstance(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("mechanismType cannot be null");
    }
    GetInstance.Instance localInstance;
    try
    {
      localInstance = GetInstance.getInstance("KeyInfoFactory", null, paramString);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new NoSuchMechanismException(localNoSuchAlgorithmException);
    }
    KeyInfoFactory localKeyInfoFactory = (KeyInfoFactory)impl;
    mechanismType = paramString;
    provider = provider;
    return localKeyInfoFactory;
  }
  
  public static KeyInfoFactory getInstance(String paramString, Provider paramProvider)
  {
    if (paramString == null) {
      throw new NullPointerException("mechanismType cannot be null");
    }
    if (paramProvider == null) {
      throw new NullPointerException("provider cannot be null");
    }
    GetInstance.Instance localInstance;
    try
    {
      localInstance = GetInstance.getInstance("KeyInfoFactory", null, paramString, paramProvider);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new NoSuchMechanismException(localNoSuchAlgorithmException);
    }
    KeyInfoFactory localKeyInfoFactory = (KeyInfoFactory)impl;
    mechanismType = paramString;
    provider = provider;
    return localKeyInfoFactory;
  }
  
  public static KeyInfoFactory getInstance(String paramString1, String paramString2)
    throws NoSuchProviderException
  {
    if (paramString1 == null) {
      throw new NullPointerException("mechanismType cannot be null");
    }
    if (paramString2 == null) {
      throw new NullPointerException("provider cannot be null");
    }
    if (paramString2.length() == 0) {
      throw new NoSuchProviderException();
    }
    GetInstance.Instance localInstance;
    try
    {
      localInstance = GetInstance.getInstance("KeyInfoFactory", null, paramString1, paramString2);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new NoSuchMechanismException(localNoSuchAlgorithmException);
    }
    KeyInfoFactory localKeyInfoFactory = (KeyInfoFactory)impl;
    mechanismType = paramString1;
    provider = provider;
    return localKeyInfoFactory;
  }
  
  public static KeyInfoFactory getInstance()
  {
    return getInstance("DOM");
  }
  
  public final String getMechanismType()
  {
    return mechanismType;
  }
  
  public final Provider getProvider()
  {
    return provider;
  }
  
  public abstract KeyInfo newKeyInfo(List paramList);
  
  public abstract KeyInfo newKeyInfo(List paramList, String paramString);
  
  public abstract KeyName newKeyName(String paramString);
  
  public abstract KeyValue newKeyValue(PublicKey paramPublicKey)
    throws KeyException;
  
  public abstract PGPData newPGPData(byte[] paramArrayOfByte);
  
  public abstract PGPData newPGPData(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, List paramList);
  
  public abstract PGPData newPGPData(byte[] paramArrayOfByte, List paramList);
  
  public abstract RetrievalMethod newRetrievalMethod(String paramString);
  
  public abstract RetrievalMethod newRetrievalMethod(String paramString1, String paramString2, List paramList);
  
  public abstract X509Data newX509Data(List paramList);
  
  public abstract X509IssuerSerial newX509IssuerSerial(String paramString, BigInteger paramBigInteger);
  
  public abstract boolean isFeatureSupported(String paramString);
  
  public abstract URIDereferencer getURIDereferencer();
  
  public abstract KeyInfo unmarshalKeyInfo(XMLStructure paramXMLStructure)
    throws MarshalException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\crypto\dsig\keyinfo\KeyInfoFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */