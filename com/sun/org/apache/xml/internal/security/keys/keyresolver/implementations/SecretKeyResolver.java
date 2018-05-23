package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SecretKeyResolver
  extends KeyResolverSpi
{
  private static Logger log = Logger.getLogger(SecretKeyResolver.class.getName());
  private KeyStore keyStore;
  private char[] password;
  
  public SecretKeyResolver(KeyStore paramKeyStore, char[] paramArrayOfChar)
  {
    keyStore = paramKeyStore;
    password = paramArrayOfChar;
  }
  
  public boolean engineCanResolve(Element paramElement, String paramString, StorageResolver paramStorageResolver)
  {
    return XMLUtils.elementIsInSignatureSpace(paramElement, "KeyName");
  }
  
  public PublicKey engineLookupAndResolvePublicKey(Element paramElement, String paramString, StorageResolver paramStorageResolver)
    throws KeyResolverException
  {
    return null;
  }
  
  public X509Certificate engineLookupResolveX509Certificate(Element paramElement, String paramString, StorageResolver paramStorageResolver)
    throws KeyResolverException
  {
    return null;
  }
  
  public SecretKey engineResolveSecretKey(Element paramElement, String paramString, StorageResolver paramStorageResolver)
    throws KeyResolverException
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Can I resolve " + paramElement.getTagName() + "?");
    }
    if (XMLUtils.elementIsInSignatureSpace(paramElement, "KeyName"))
    {
      String str = paramElement.getFirstChild().getNodeValue();
      try
      {
        Key localKey = keyStore.getKey(str, password);
        if ((localKey instanceof SecretKey)) {
          return (SecretKey)localKey;
        }
      }
      catch (Exception localException)
      {
        log.log(Level.FINE, "Cannot recover the key", localException);
      }
    }
    log.log(Level.FINE, "I can't");
    return null;
  }
  
  public PrivateKey engineLookupAndResolvePrivateKey(Element paramElement, String paramString, StorageResolver paramStorageResolver)
    throws KeyResolverException
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\keys\keyresolver\implementations\SecretKeyResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */