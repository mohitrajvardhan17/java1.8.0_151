package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.apache.xml.internal.security.encryption.EncryptedKey;
import com.sun.org.apache.xml.internal.security.encryption.XMLCipher;
import com.sun.org.apache.xml.internal.security.encryption.XMLEncryptionException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.Key;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.w3c.dom.Element;

public class EncryptedKeyResolver
  extends KeyResolverSpi
{
  private static Logger log = Logger.getLogger(EncryptedKeyResolver.class.getName());
  private Key kek;
  private String algorithm;
  private List<KeyResolverSpi> internalKeyResolvers;
  
  public EncryptedKeyResolver(String paramString)
  {
    kek = null;
    algorithm = paramString;
  }
  
  public EncryptedKeyResolver(String paramString, Key paramKey)
  {
    algorithm = paramString;
    kek = paramKey;
  }
  
  public void registerInternalKeyResolver(KeyResolverSpi paramKeyResolverSpi)
  {
    if (internalKeyResolvers == null) {
      internalKeyResolvers = new ArrayList();
    }
    internalKeyResolvers.add(paramKeyResolverSpi);
  }
  
  public PublicKey engineLookupAndResolvePublicKey(Element paramElement, String paramString, StorageResolver paramStorageResolver)
  {
    return null;
  }
  
  public X509Certificate engineLookupResolveX509Certificate(Element paramElement, String paramString, StorageResolver paramStorageResolver)
  {
    return null;
  }
  
  public SecretKey engineLookupAndResolveSecretKey(Element paramElement, String paramString, StorageResolver paramStorageResolver)
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "EncryptedKeyResolver - Can I resolve " + paramElement.getTagName());
    }
    if (paramElement == null) {
      return null;
    }
    SecretKey localSecretKey = null;
    boolean bool = XMLUtils.elementIsInEncryptionSpace(paramElement, "EncryptedKey");
    if (bool)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "Passed an Encrypted Key");
      }
      try
      {
        XMLCipher localXMLCipher = XMLCipher.getInstance();
        localXMLCipher.init(4, kek);
        if (internalKeyResolvers != null)
        {
          int i = internalKeyResolvers.size();
          for (int j = 0; j < i; j++) {
            localXMLCipher.registerInternalKeyResolver((KeyResolverSpi)internalKeyResolvers.get(j));
          }
        }
        EncryptedKey localEncryptedKey = localXMLCipher.loadEncryptedKey(paramElement);
        localSecretKey = (SecretKey)localXMLCipher.decryptKey(localEncryptedKey, algorithm);
      }
      catch (XMLEncryptionException localXMLEncryptionException)
      {
        if (log.isLoggable(Level.FINE)) {
          log.log(Level.FINE, localXMLEncryptionException.getMessage(), localXMLEncryptionException);
        }
      }
    }
    return localSecretKey;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\keys\keyresolver\implementations\EncryptedKeyResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */