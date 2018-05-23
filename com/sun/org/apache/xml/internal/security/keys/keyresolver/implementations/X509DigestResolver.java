package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.X509Data;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509Digest;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import javax.security.auth.x500.X500Principal;
import org.w3c.dom.Element;

public class X509DigestResolver
  extends KeyResolverSpi
{
  private static Logger log = Logger.getLogger(X509DigestResolver.class.getName());
  
  public X509DigestResolver() {}
  
  public boolean engineCanResolve(Element paramElement, String paramString, StorageResolver paramStorageResolver)
  {
    if (XMLUtils.elementIsInSignatureSpace(paramElement, "X509Data")) {
      try
      {
        X509Data localX509Data = new X509Data(paramElement, paramString);
        return localX509Data.containsDigest();
      }
      catch (XMLSecurityException localXMLSecurityException)
      {
        return false;
      }
    }
    return false;
  }
  
  public PublicKey engineLookupAndResolvePublicKey(Element paramElement, String paramString, StorageResolver paramStorageResolver)
    throws KeyResolverException
  {
    X509Certificate localX509Certificate = engineLookupResolveX509Certificate(paramElement, paramString, paramStorageResolver);
    if (localX509Certificate != null) {
      return localX509Certificate.getPublicKey();
    }
    return null;
  }
  
  public X509Certificate engineLookupResolveX509Certificate(Element paramElement, String paramString, StorageResolver paramStorageResolver)
    throws KeyResolverException
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Can I resolve " + paramElement.getTagName());
    }
    if (!engineCanResolve(paramElement, paramString, paramStorageResolver)) {
      return null;
    }
    try
    {
      return resolveCertificate(paramElement, paramString, paramStorageResolver);
    }
    catch (XMLSecurityException localXMLSecurityException)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "XMLSecurityException", localXMLSecurityException);
      }
    }
    return null;
  }
  
  public SecretKey engineLookupAndResolveSecretKey(Element paramElement, String paramString, StorageResolver paramStorageResolver)
    throws KeyResolverException
  {
    return null;
  }
  
  private X509Certificate resolveCertificate(Element paramElement, String paramString, StorageResolver paramStorageResolver)
    throws XMLSecurityException
  {
    XMLX509Digest[] arrayOfXMLX509Digest = null;
    Element[] arrayOfElement = XMLUtils.selectDs11Nodes(paramElement.getFirstChild(), "X509Digest");
    if ((arrayOfElement == null) || (arrayOfElement.length <= 0)) {
      return null;
    }
    try
    {
      checkStorage(paramStorageResolver);
      arrayOfXMLX509Digest = new XMLX509Digest[arrayOfElement.length];
      for (int i = 0; i < arrayOfElement.length; i++) {
        arrayOfXMLX509Digest[i] = new XMLX509Digest(arrayOfElement[i], paramString);
      }
      Iterator localIterator = paramStorageResolver.getIterator();
      while (localIterator.hasNext())
      {
        X509Certificate localX509Certificate = (X509Certificate)localIterator.next();
        for (int j = 0; j < arrayOfXMLX509Digest.length; j++)
        {
          XMLX509Digest localXMLX509Digest = arrayOfXMLX509Digest[j];
          byte[] arrayOfByte = XMLX509Digest.getDigestBytesFromCert(localX509Certificate, localXMLX509Digest.getAlgorithm());
          if (Arrays.equals(localXMLX509Digest.getDigestBytes(), arrayOfByte))
          {
            if (log.isLoggable(Level.FINE)) {
              log.log(Level.FINE, "Found certificate with: " + localX509Certificate.getSubjectX500Principal().getName());
            }
            return localX509Certificate;
          }
        }
      }
    }
    catch (XMLSecurityException localXMLSecurityException)
    {
      throw new KeyResolverException("empty", localXMLSecurityException);
    }
    return null;
  }
  
  private void checkStorage(StorageResolver paramStorageResolver)
    throws KeyResolverException
  {
    if (paramStorageResolver == null)
    {
      Object[] arrayOfObject = { "X509Digest" };
      KeyResolverException localKeyResolverException = new KeyResolverException("KeyResolver.needStorageResolver", arrayOfObject);
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "", localKeyResolverException);
      }
      throw localKeyResolverException;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\keys\keyresolver\implementations\X509DigestResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */