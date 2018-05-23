package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509SubjectName;
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
import org.w3c.dom.Element;

public class X509SubjectNameResolver
  extends KeyResolverSpi
{
  private static Logger log = Logger.getLogger(X509SubjectNameResolver.class.getName());
  
  public X509SubjectNameResolver() {}
  
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
    Element[] arrayOfElement = null;
    XMLX509SubjectName[] arrayOfXMLX509SubjectName = null;
    if (!XMLUtils.elementIsInSignatureSpace(paramElement, "X509Data"))
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "I can't");
      }
      return null;
    }
    arrayOfElement = XMLUtils.selectDsNodes(paramElement.getFirstChild(), "X509SubjectName");
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
        Object[] arrayOfObject = { "X509SubjectName" };
        localObject = new KeyResolverException("KeyResolver.needStorageResolver", arrayOfObject);
        if (log.isLoggable(Level.FINE)) {
          log.log(Level.FINE, "", (Throwable)localObject);
        }
        throw ((Throwable)localObject);
      }
      arrayOfXMLX509SubjectName = new XMLX509SubjectName[arrayOfElement.length];
      for (int i = 0; i < arrayOfElement.length; i++) {
        arrayOfXMLX509SubjectName[i] = new XMLX509SubjectName(arrayOfElement[i], paramString);
      }
      Iterator localIterator = paramStorageResolver.getIterator();
      while (localIterator.hasNext())
      {
        localObject = (X509Certificate)localIterator.next();
        XMLX509SubjectName localXMLX509SubjectName = new XMLX509SubjectName(paramElement.getOwnerDocument(), (X509Certificate)localObject);
        if (log.isLoggable(Level.FINE)) {
          log.log(Level.FINE, "Found Certificate SN: " + localXMLX509SubjectName.getSubjectName());
        }
        for (int j = 0; j < arrayOfXMLX509SubjectName.length; j++)
        {
          if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Found Element SN:     " + arrayOfXMLX509SubjectName[j].getSubjectName());
          }
          if (localXMLX509SubjectName.equals(arrayOfXMLX509SubjectName[j]))
          {
            if (log.isLoggable(Level.FINE)) {
              log.log(Level.FINE, "match !!! ");
            }
            return (X509Certificate)localObject;
          }
          if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "no match...");
          }
        }
      }
      return null;
    }
    catch (XMLSecurityException localXMLSecurityException)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "XMLSecurityException", localXMLSecurityException);
      }
      throw new KeyResolverException("generic.EmptyMessage", localXMLSecurityException);
    }
  }
  
  public SecretKey engineLookupAndResolveSecretKey(Element paramElement, String paramString, StorageResolver paramStorageResolver)
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\keys\keyresolver\implementations\X509SubjectNameResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */