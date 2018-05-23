package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509SKI;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import javax.security.auth.x500.X500Principal;
import org.w3c.dom.Element;

public class X509SKIResolver
  extends KeyResolverSpi
{
  private static Logger log = Logger.getLogger(X509SKIResolver.class.getName());
  
  public X509SKIResolver() {}
  
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
    if (!XMLUtils.elementIsInSignatureSpace(paramElement, "X509Data"))
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "I can't");
      }
      return null;
    }
    XMLX509SKI[] arrayOfXMLX509SKI = null;
    Element[] arrayOfElement = null;
    arrayOfElement = XMLUtils.selectDsNodes(paramElement.getFirstChild(), "X509SKI");
    if ((arrayOfElement == null) || (arrayOfElement.length <= 0))
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "I can't");
      }
      return null;
    }
    try
    {
      Object localObject;
      if (paramStorageResolver == null)
      {
        Object[] arrayOfObject = { "X509SKI" };
        localObject = new KeyResolverException("KeyResolver.needStorageResolver", arrayOfObject);
        if (log.isLoggable(Level.FINE)) {
          log.log(Level.FINE, "", (Throwable)localObject);
        }
        throw ((Throwable)localObject);
      }
      arrayOfXMLX509SKI = new XMLX509SKI[arrayOfElement.length];
      for (int i = 0; i < arrayOfElement.length; i++) {
        arrayOfXMLX509SKI[i] = new XMLX509SKI(arrayOfElement[i], paramString);
      }
      Iterator localIterator = paramStorageResolver.getIterator();
      while (localIterator.hasNext())
      {
        localObject = (X509Certificate)localIterator.next();
        XMLX509SKI localXMLX509SKI = new XMLX509SKI(paramElement.getOwnerDocument(), (X509Certificate)localObject);
        for (int j = 0; j < arrayOfXMLX509SKI.length; j++) {
          if (localXMLX509SKI.equals(arrayOfXMLX509SKI[j]))
          {
            if (log.isLoggable(Level.FINE)) {
              log.log(Level.FINE, "Return PublicKey from " + ((X509Certificate)localObject).getSubjectX500Principal().getName());
            }
            return (X509Certificate)localObject;
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
  
  public SecretKey engineLookupAndResolveSecretKey(Element paramElement, String paramString, StorageResolver paramStorageResolver)
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\keys\keyresolver\implementations\X509SKIResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */