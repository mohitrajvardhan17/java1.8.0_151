package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.KeyInfo;
import com.sun.org.apache.xml.internal.security.keys.content.KeyInfoReference;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class KeyInfoReferenceResolver
  extends KeyResolverSpi
{
  private static Logger log = Logger.getLogger(KeyInfoReferenceResolver.class.getName());
  
  public KeyInfoReferenceResolver() {}
  
  public boolean engineCanResolve(Element paramElement, String paramString, StorageResolver paramStorageResolver)
  {
    return XMLUtils.elementIsInSignature11Space(paramElement, "KeyInfoReference");
  }
  
  public PublicKey engineLookupAndResolvePublicKey(Element paramElement, String paramString, StorageResolver paramStorageResolver)
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
      KeyInfo localKeyInfo = resolveReferentKeyInfo(paramElement, paramString, paramStorageResolver);
      if (localKeyInfo != null) {
        return localKeyInfo.getPublicKey();
      }
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
      KeyInfo localKeyInfo = resolveReferentKeyInfo(paramElement, paramString, paramStorageResolver);
      if (localKeyInfo != null) {
        return localKeyInfo.getX509Certificate();
      }
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
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Can I resolve " + paramElement.getTagName());
    }
    if (!engineCanResolve(paramElement, paramString, paramStorageResolver)) {
      return null;
    }
    try
    {
      KeyInfo localKeyInfo = resolveReferentKeyInfo(paramElement, paramString, paramStorageResolver);
      if (localKeyInfo != null) {
        return localKeyInfo.getSecretKey();
      }
    }
    catch (XMLSecurityException localXMLSecurityException)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "XMLSecurityException", localXMLSecurityException);
      }
    }
    return null;
  }
  
  public PrivateKey engineLookupAndResolvePrivateKey(Element paramElement, String paramString, StorageResolver paramStorageResolver)
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
      KeyInfo localKeyInfo = resolveReferentKeyInfo(paramElement, paramString, paramStorageResolver);
      if (localKeyInfo != null) {
        return localKeyInfo.getPrivateKey();
      }
    }
    catch (XMLSecurityException localXMLSecurityException)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "XMLSecurityException", localXMLSecurityException);
      }
    }
    return null;
  }
  
  private KeyInfo resolveReferentKeyInfo(Element paramElement, String paramString, StorageResolver paramStorageResolver)
    throws XMLSecurityException
  {
    KeyInfoReference localKeyInfoReference = new KeyInfoReference(paramElement, paramString);
    Attr localAttr = localKeyInfoReference.getURIAttr();
    XMLSignatureInput localXMLSignatureInput = resolveInput(localAttr, paramString, secureValidation);
    Element localElement = null;
    try
    {
      localElement = obtainReferenceElement(localXMLSignatureInput);
    }
    catch (Exception localException)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "XMLSecurityException", localException);
      }
      return null;
    }
    if (localElement == null)
    {
      log.log(Level.FINE, "De-reference of KeyInfoReference URI returned null: " + localAttr.getValue());
      return null;
    }
    validateReference(localElement);
    KeyInfo localKeyInfo = new KeyInfo(localElement, paramString);
    localKeyInfo.addStorageResolver(paramStorageResolver);
    return localKeyInfo;
  }
  
  private void validateReference(Element paramElement)
    throws XMLSecurityException
  {
    if (!XMLUtils.elementIsInSignatureSpace(paramElement, "KeyInfo"))
    {
      localObject = new Object[] { new QName(paramElement.getNamespaceURI(), paramElement.getLocalName()) };
      throw new XMLSecurityException("KeyInfoReferenceResolver.InvalidReferentElement.WrongType", (Object[])localObject);
    }
    Object localObject = new KeyInfo(paramElement, "");
    if (((KeyInfo)localObject).containsKeyInfoReference())
    {
      if (secureValidation) {
        throw new XMLSecurityException("KeyInfoReferenceResolver.InvalidReferentElement.ReferenceWithSecure");
      }
      throw new XMLSecurityException("KeyInfoReferenceResolver.InvalidReferentElement.ReferenceWithoutSecure");
    }
  }
  
  private XMLSignatureInput resolveInput(Attr paramAttr, String paramString, boolean paramBoolean)
    throws XMLSecurityException
  {
    ResourceResolver localResourceResolver = ResourceResolver.getInstance(paramAttr, paramString, paramBoolean);
    XMLSignatureInput localXMLSignatureInput = localResourceResolver.resolve(paramAttr, paramString, paramBoolean);
    return localXMLSignatureInput;
  }
  
  private Element obtainReferenceElement(XMLSignatureInput paramXMLSignatureInput)
    throws CanonicalizationException, ParserConfigurationException, IOException, SAXException, KeyResolverException
  {
    Element localElement;
    if (paramXMLSignatureInput.isElement())
    {
      localElement = (Element)paramXMLSignatureInput.getSubNode();
    }
    else
    {
      if (paramXMLSignatureInput.isNodeSet())
      {
        log.log(Level.FINE, "De-reference of KeyInfoReference returned an unsupported NodeSet");
        return null;
      }
      byte[] arrayOfByte = paramXMLSignatureInput.getBytes();
      localElement = getDocFromBytes(arrayOfByte);
    }
    return localElement;
  }
  
  private Element getDocFromBytes(byte[] paramArrayOfByte)
    throws KeyResolverException
  {
    try
    {
      DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
      localDocumentBuilderFactory.setNamespaceAware(true);
      localDocumentBuilderFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE.booleanValue());
      DocumentBuilder localDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
      Document localDocument = localDocumentBuilder.parse(new ByteArrayInputStream(paramArrayOfByte));
      return localDocument.getDocumentElement();
    }
    catch (SAXException localSAXException)
    {
      throw new KeyResolverException("empty", localSAXException);
    }
    catch (IOException localIOException)
    {
      throw new KeyResolverException("empty", localIOException);
    }
    catch (ParserConfigurationException localParserConfigurationException)
    {
      throw new KeyResolverException("empty", localParserConfigurationException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\keys\keyresolver\implementations\KeyInfoReferenceResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */