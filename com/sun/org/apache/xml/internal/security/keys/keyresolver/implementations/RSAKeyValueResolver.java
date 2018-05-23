package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.keyvalues.RSAKeyValue;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.w3c.dom.Element;

public class RSAKeyValueResolver
  extends KeyResolverSpi
{
  private static Logger log = Logger.getLogger(RSAKeyValueResolver.class.getName());
  
  public RSAKeyValueResolver() {}
  
  public PublicKey engineLookupAndResolvePublicKey(Element paramElement, String paramString, StorageResolver paramStorageResolver)
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Can I resolve " + paramElement.getTagName());
    }
    if (paramElement == null) {
      return null;
    }
    boolean bool = XMLUtils.elementIsInSignatureSpace(paramElement, "KeyValue");
    Element localElement = null;
    if (bool) {
      localElement = XMLUtils.selectDsNode(paramElement.getFirstChild(), "RSAKeyValue", 0);
    } else if (XMLUtils.elementIsInSignatureSpace(paramElement, "RSAKeyValue")) {
      localElement = paramElement;
    }
    if (localElement == null) {
      return null;
    }
    try
    {
      RSAKeyValue localRSAKeyValue = new RSAKeyValue(localElement, paramString);
      return localRSAKeyValue.getPublicKey();
    }
    catch (XMLSecurityException localXMLSecurityException)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "XMLSecurityException", localXMLSecurityException);
      }
    }
    return null;
  }
  
  public X509Certificate engineLookupResolveX509Certificate(Element paramElement, String paramString, StorageResolver paramStorageResolver)
  {
    return null;
  }
  
  public SecretKey engineLookupAndResolveSecretKey(Element paramElement, String paramString, StorageResolver paramStorageResolver)
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\keys\keyresolver\implementations\RSAKeyValueResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */