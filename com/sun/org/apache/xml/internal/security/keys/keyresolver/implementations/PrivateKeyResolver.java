package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.X509Data;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509Certificate;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509IssuerSerial;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509SKI;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509SubjectName;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class PrivateKeyResolver
  extends KeyResolverSpi
{
  private static Logger log = Logger.getLogger(PrivateKeyResolver.class.getName());
  private KeyStore keyStore;
  private char[] password;
  
  public PrivateKeyResolver(KeyStore paramKeyStore, char[] paramArrayOfChar)
  {
    keyStore = paramKeyStore;
    password = paramArrayOfChar;
  }
  
  public boolean engineCanResolve(Element paramElement, String paramString, StorageResolver paramStorageResolver)
  {
    return (XMLUtils.elementIsInSignatureSpace(paramElement, "X509Data")) || (XMLUtils.elementIsInSignatureSpace(paramElement, "KeyName"));
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
    return null;
  }
  
  public PrivateKey engineLookupAndResolvePrivateKey(Element paramElement, String paramString, StorageResolver paramStorageResolver)
    throws KeyResolverException
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Can I resolve " + paramElement.getTagName() + "?");
    }
    Object localObject;
    if (XMLUtils.elementIsInSignatureSpace(paramElement, "X509Data"))
    {
      localObject = resolveX509Data(paramElement, paramString);
      if (localObject != null) {
        return (PrivateKey)localObject;
      }
    }
    else if (XMLUtils.elementIsInSignatureSpace(paramElement, "KeyName"))
    {
      log.log(Level.FINE, "Can I resolve KeyName?");
      localObject = paramElement.getFirstChild().getNodeValue();
      try
      {
        Key localKey = keyStore.getKey((String)localObject, password);
        if ((localKey instanceof PrivateKey)) {
          return (PrivateKey)localKey;
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
  
  private PrivateKey resolveX509Data(Element paramElement, String paramString)
  {
    log.log(Level.FINE, "Can I resolve X509Data?");
    try
    {
      X509Data localX509Data = new X509Data(paramElement, paramString);
      int i = localX509Data.lengthSKI();
      Object localObject;
      PrivateKey localPrivateKey;
      for (int j = 0; j < i; j++)
      {
        localObject = localX509Data.itemSKI(j);
        localPrivateKey = resolveX509SKI((XMLX509SKI)localObject);
        if (localPrivateKey != null) {
          return localPrivateKey;
        }
      }
      i = localX509Data.lengthIssuerSerial();
      for (j = 0; j < i; j++)
      {
        localObject = localX509Data.itemIssuerSerial(j);
        localPrivateKey = resolveX509IssuerSerial((XMLX509IssuerSerial)localObject);
        if (localPrivateKey != null) {
          return localPrivateKey;
        }
      }
      i = localX509Data.lengthSubjectName();
      for (j = 0; j < i; j++)
      {
        localObject = localX509Data.itemSubjectName(j);
        localPrivateKey = resolveX509SubjectName((XMLX509SubjectName)localObject);
        if (localPrivateKey != null) {
          return localPrivateKey;
        }
      }
      i = localX509Data.lengthCertificate();
      for (j = 0; j < i; j++)
      {
        localObject = localX509Data.itemCertificate(j);
        localPrivateKey = resolveX509Certificate((XMLX509Certificate)localObject);
        if (localPrivateKey != null) {
          return localPrivateKey;
        }
      }
    }
    catch (XMLSecurityException localXMLSecurityException)
    {
      log.log(Level.FINE, "XMLSecurityException", localXMLSecurityException);
    }
    catch (KeyStoreException localKeyStoreException)
    {
      log.log(Level.FINE, "KeyStoreException", localKeyStoreException);
    }
    return null;
  }
  
  private PrivateKey resolveX509SKI(XMLX509SKI paramXMLX509SKI)
    throws XMLSecurityException, KeyStoreException
  {
    log.log(Level.FINE, "Can I resolve X509SKI?");
    Enumeration localEnumeration = keyStore.aliases();
    while (localEnumeration.hasMoreElements())
    {
      String str = (String)localEnumeration.nextElement();
      if (keyStore.isKeyEntry(str))
      {
        Certificate localCertificate = keyStore.getCertificate(str);
        if ((localCertificate instanceof X509Certificate))
        {
          XMLX509SKI localXMLX509SKI = new XMLX509SKI(paramXMLX509SKI.getDocument(), (X509Certificate)localCertificate);
          if (localXMLX509SKI.equals(paramXMLX509SKI))
          {
            log.log(Level.FINE, "match !!! ");
            try
            {
              Key localKey = keyStore.getKey(str, password);
              if ((localKey instanceof PrivateKey)) {
                return (PrivateKey)localKey;
              }
            }
            catch (Exception localException)
            {
              log.log(Level.FINE, "Cannot recover the key", localException);
            }
          }
        }
      }
    }
    return null;
  }
  
  private PrivateKey resolveX509IssuerSerial(XMLX509IssuerSerial paramXMLX509IssuerSerial)
    throws KeyStoreException
  {
    log.log(Level.FINE, "Can I resolve X509IssuerSerial?");
    Enumeration localEnumeration = keyStore.aliases();
    while (localEnumeration.hasMoreElements())
    {
      String str = (String)localEnumeration.nextElement();
      if (keyStore.isKeyEntry(str))
      {
        Certificate localCertificate = keyStore.getCertificate(str);
        if ((localCertificate instanceof X509Certificate))
        {
          XMLX509IssuerSerial localXMLX509IssuerSerial = new XMLX509IssuerSerial(paramXMLX509IssuerSerial.getDocument(), (X509Certificate)localCertificate);
          if (localXMLX509IssuerSerial.equals(paramXMLX509IssuerSerial))
          {
            log.log(Level.FINE, "match !!! ");
            try
            {
              Key localKey = keyStore.getKey(str, password);
              if ((localKey instanceof PrivateKey)) {
                return (PrivateKey)localKey;
              }
            }
            catch (Exception localException)
            {
              log.log(Level.FINE, "Cannot recover the key", localException);
            }
          }
        }
      }
    }
    return null;
  }
  
  private PrivateKey resolveX509SubjectName(XMLX509SubjectName paramXMLX509SubjectName)
    throws KeyStoreException
  {
    log.log(Level.FINE, "Can I resolve X509SubjectName?");
    Enumeration localEnumeration = keyStore.aliases();
    while (localEnumeration.hasMoreElements())
    {
      String str = (String)localEnumeration.nextElement();
      if (keyStore.isKeyEntry(str))
      {
        Certificate localCertificate = keyStore.getCertificate(str);
        if ((localCertificate instanceof X509Certificate))
        {
          XMLX509SubjectName localXMLX509SubjectName = new XMLX509SubjectName(paramXMLX509SubjectName.getDocument(), (X509Certificate)localCertificate);
          if (localXMLX509SubjectName.equals(paramXMLX509SubjectName))
          {
            log.log(Level.FINE, "match !!! ");
            try
            {
              Key localKey = keyStore.getKey(str, password);
              if ((localKey instanceof PrivateKey)) {
                return (PrivateKey)localKey;
              }
            }
            catch (Exception localException)
            {
              log.log(Level.FINE, "Cannot recover the key", localException);
            }
          }
        }
      }
    }
    return null;
  }
  
  private PrivateKey resolveX509Certificate(XMLX509Certificate paramXMLX509Certificate)
    throws XMLSecurityException, KeyStoreException
  {
    log.log(Level.FINE, "Can I resolve X509Certificate?");
    byte[] arrayOfByte1 = paramXMLX509Certificate.getCertificateBytes();
    Enumeration localEnumeration = keyStore.aliases();
    while (localEnumeration.hasMoreElements())
    {
      String str = (String)localEnumeration.nextElement();
      if (keyStore.isKeyEntry(str))
      {
        Certificate localCertificate = keyStore.getCertificate(str);
        if ((localCertificate instanceof X509Certificate))
        {
          byte[] arrayOfByte2 = null;
          try
          {
            arrayOfByte2 = localCertificate.getEncoded();
          }
          catch (CertificateEncodingException localCertificateEncodingException) {}
          if ((arrayOfByte2 != null) && (Arrays.equals(arrayOfByte2, arrayOfByte1)))
          {
            log.log(Level.FINE, "match !!! ");
            try
            {
              Key localKey = keyStore.getKey(str, password);
              if ((localKey instanceof PrivateKey)) {
                return (PrivateKey)localKey;
              }
            }
            catch (Exception localException)
            {
              log.log(Level.FINE, "Cannot recover the key", localException);
            }
          }
        }
      }
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\keys\keyresolver\implementations\PrivateKeyResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */