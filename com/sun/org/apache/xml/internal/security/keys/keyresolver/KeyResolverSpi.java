package com.sun.org.apache.xml.internal.security.keys.keyresolver;

import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import org.w3c.dom.Element;

public abstract class KeyResolverSpi
{
  protected Map<String, String> properties = null;
  protected boolean globalResolver = false;
  protected boolean secureValidation;
  
  public KeyResolverSpi() {}
  
  public void setSecureValidation(boolean paramBoolean)
  {
    secureValidation = paramBoolean;
  }
  
  public boolean engineCanResolve(Element paramElement, String paramString, StorageResolver paramStorageResolver)
  {
    throw new UnsupportedOperationException();
  }
  
  public PublicKey engineResolvePublicKey(Element paramElement, String paramString, StorageResolver paramStorageResolver)
    throws KeyResolverException
  {
    throw new UnsupportedOperationException();
  }
  
  public PublicKey engineLookupAndResolvePublicKey(Element paramElement, String paramString, StorageResolver paramStorageResolver)
    throws KeyResolverException
  {
    KeyResolverSpi localKeyResolverSpi = cloneIfNeeded();
    if (!localKeyResolverSpi.engineCanResolve(paramElement, paramString, paramStorageResolver)) {
      return null;
    }
    return localKeyResolverSpi.engineResolvePublicKey(paramElement, paramString, paramStorageResolver);
  }
  
  private KeyResolverSpi cloneIfNeeded()
    throws KeyResolverException
  {
    KeyResolverSpi localKeyResolverSpi = this;
    if (globalResolver) {
      try
      {
        localKeyResolverSpi = (KeyResolverSpi)getClass().newInstance();
      }
      catch (InstantiationException localInstantiationException)
      {
        throw new KeyResolverException("", localInstantiationException);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new KeyResolverException("", localIllegalAccessException);
      }
    }
    return localKeyResolverSpi;
  }
  
  public X509Certificate engineResolveX509Certificate(Element paramElement, String paramString, StorageResolver paramStorageResolver)
    throws KeyResolverException
  {
    throw new UnsupportedOperationException();
  }
  
  public X509Certificate engineLookupResolveX509Certificate(Element paramElement, String paramString, StorageResolver paramStorageResolver)
    throws KeyResolverException
  {
    KeyResolverSpi localKeyResolverSpi = cloneIfNeeded();
    if (!localKeyResolverSpi.engineCanResolve(paramElement, paramString, paramStorageResolver)) {
      return null;
    }
    return localKeyResolverSpi.engineResolveX509Certificate(paramElement, paramString, paramStorageResolver);
  }
  
  public SecretKey engineResolveSecretKey(Element paramElement, String paramString, StorageResolver paramStorageResolver)
    throws KeyResolverException
  {
    throw new UnsupportedOperationException();
  }
  
  public SecretKey engineLookupAndResolveSecretKey(Element paramElement, String paramString, StorageResolver paramStorageResolver)
    throws KeyResolverException
  {
    KeyResolverSpi localKeyResolverSpi = cloneIfNeeded();
    if (!localKeyResolverSpi.engineCanResolve(paramElement, paramString, paramStorageResolver)) {
      return null;
    }
    return localKeyResolverSpi.engineResolveSecretKey(paramElement, paramString, paramStorageResolver);
  }
  
  public PrivateKey engineLookupAndResolvePrivateKey(Element paramElement, String paramString, StorageResolver paramStorageResolver)
    throws KeyResolverException
  {
    return null;
  }
  
  public void engineSetProperty(String paramString1, String paramString2)
  {
    if (properties == null) {
      properties = new HashMap();
    }
    properties.put(paramString1, paramString2);
  }
  
  public String engineGetProperty(String paramString)
  {
    if (properties == null) {
      return null;
    }
    return (String)properties.get(paramString);
  }
  
  public boolean understandsProperty(String paramString)
  {
    if (properties == null) {
      return false;
    }
    return properties.get(paramString) != null;
  }
  
  public void setGlobalResolver(boolean paramBoolean)
  {
    globalResolver = paramBoolean;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\keys\keyresolver\KeyResolverSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */