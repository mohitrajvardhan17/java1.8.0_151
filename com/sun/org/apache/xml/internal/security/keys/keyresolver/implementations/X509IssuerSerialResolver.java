package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.X509Data;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509IssuerSerial;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import java.math.BigInteger;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.w3c.dom.Element;

public class X509IssuerSerialResolver
  extends KeyResolverSpi
{
  private static Logger log = Logger.getLogger(X509IssuerSerialResolver.class.getName());
  
  public X509IssuerSerialResolver() {}
  
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
      log.log(Level.FINE, "Can I resolve " + paramElement.getTagName() + "?");
    }
    X509Data localX509Data = null;
    try
    {
      localX509Data = new X509Data(paramElement, paramString);
    }
    catch (XMLSignatureException localXMLSignatureException)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "I can't");
      }
      return null;
    }
    catch (XMLSecurityException localXMLSecurityException1)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "I can't");
      }
      return null;
    }
    if (!localX509Data.containsIssuerSerial()) {
      return null;
    }
    try
    {
      if (paramStorageResolver == null)
      {
        Object[] arrayOfObject = { "X509IssuerSerial" };
        localObject = new KeyResolverException("KeyResolver.needStorageResolver", arrayOfObject);
        if (log.isLoggable(Level.FINE)) {
          log.log(Level.FINE, "", (Throwable)localObject);
        }
        throw ((Throwable)localObject);
      }
      int i = localX509Data.lengthIssuerSerial();
      Object localObject = paramStorageResolver.getIterator();
      while (((Iterator)localObject).hasNext())
      {
        X509Certificate localX509Certificate = (X509Certificate)((Iterator)localObject).next();
        XMLX509IssuerSerial localXMLX509IssuerSerial1 = new XMLX509IssuerSerial(paramElement.getOwnerDocument(), localX509Certificate);
        if (log.isLoggable(Level.FINE))
        {
          log.log(Level.FINE, "Found Certificate Issuer: " + localXMLX509IssuerSerial1.getIssuerName());
          log.log(Level.FINE, "Found Certificate Serial: " + localXMLX509IssuerSerial1.getSerialNumber().toString());
        }
        for (int j = 0; j < i; j++)
        {
          XMLX509IssuerSerial localXMLX509IssuerSerial2 = localX509Data.itemIssuerSerial(j);
          if (log.isLoggable(Level.FINE))
          {
            log.log(Level.FINE, "Found Element Issuer:     " + localXMLX509IssuerSerial2.getIssuerName());
            log.log(Level.FINE, "Found Element Serial:     " + localXMLX509IssuerSerial2.getSerialNumber().toString());
          }
          if (localXMLX509IssuerSerial1.equals(localXMLX509IssuerSerial2))
          {
            if (log.isLoggable(Level.FINE)) {
              log.log(Level.FINE, "match !!! ");
            }
            return localX509Certificate;
          }
          if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "no match...");
          }
        }
      }
      return null;
    }
    catch (XMLSecurityException localXMLSecurityException2)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "XMLSecurityException", localXMLSecurityException2);
      }
      throw new KeyResolverException("generic.EmptyMessage", localXMLSecurityException2);
    }
  }
  
  public SecretKey engineLookupAndResolveSecretKey(Element paramElement, String paramString, StorageResolver paramStorageResolver)
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\keys\keyresolver\implementations\X509IssuerSerialResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */