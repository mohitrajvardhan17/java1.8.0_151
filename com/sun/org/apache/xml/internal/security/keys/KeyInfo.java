package com.sun.org.apache.xml.internal.security.keys;

import com.sun.org.apache.xml.internal.security.encryption.EncryptedKey;
import com.sun.org.apache.xml.internal.security.encryption.XMLCipher;
import com.sun.org.apache.xml.internal.security.encryption.XMLEncryptionException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.DEREncodedKeyValue;
import com.sun.org.apache.xml.internal.security.keys.content.KeyInfoReference;
import com.sun.org.apache.xml.internal.security.keys.content.KeyName;
import com.sun.org.apache.xml.internal.security.keys.content.KeyValue;
import com.sun.org.apache.xml.internal.security.keys.content.MgmtData;
import com.sun.org.apache.xml.internal.security.keys.content.PGPData;
import com.sun.org.apache.xml.internal.security.keys.content.RetrievalMethod;
import com.sun.org.apache.xml.internal.security.keys.content.SPKIData;
import com.sun.org.apache.xml.internal.security.keys.content.X509Data;
import com.sun.org.apache.xml.internal.security.keys.content.keyvalues.DSAKeyValue;
import com.sun.org.apache.xml.internal.security.keys.content.keyvalues.RSAKeyValue;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class KeyInfo
  extends SignatureElementProxy
{
  private static Logger log = Logger.getLogger(KeyInfo.class.getName());
  private List<X509Data> x509Datas = null;
  private List<EncryptedKey> encryptedKeys = null;
  private static final List<StorageResolver> nullList;
  private List<StorageResolver> storageResolvers = nullList;
  private List<KeyResolverSpi> internalKeyResolvers = new ArrayList();
  private boolean secureValidation;
  
  public KeyInfo(Document paramDocument)
  {
    super(paramDocument);
    XMLUtils.addReturnToElement(constructionElement);
  }
  
  public KeyInfo(Element paramElement, String paramString)
    throws XMLSecurityException
  {
    super(paramElement, paramString);
    Attr localAttr = paramElement.getAttributeNodeNS(null, "Id");
    if (localAttr != null) {
      paramElement.setIdAttributeNode(localAttr, true);
    }
  }
  
  public void setSecureValidation(boolean paramBoolean)
  {
    secureValidation = paramBoolean;
  }
  
  public void setId(String paramString)
  {
    if (paramString != null)
    {
      constructionElement.setAttributeNS(null, "Id", paramString);
      constructionElement.setIdAttributeNS(null, "Id", true);
    }
  }
  
  public String getId()
  {
    return constructionElement.getAttributeNS(null, "Id");
  }
  
  public void addKeyName(String paramString)
  {
    add(new KeyName(doc, paramString));
  }
  
  public void add(KeyName paramKeyName)
  {
    constructionElement.appendChild(paramKeyName.getElement());
    XMLUtils.addReturnToElement(constructionElement);
  }
  
  public void addKeyValue(PublicKey paramPublicKey)
  {
    add(new KeyValue(doc, paramPublicKey));
  }
  
  public void addKeyValue(Element paramElement)
  {
    add(new KeyValue(doc, paramElement));
  }
  
  public void add(DSAKeyValue paramDSAKeyValue)
  {
    add(new KeyValue(doc, paramDSAKeyValue));
  }
  
  public void add(RSAKeyValue paramRSAKeyValue)
  {
    add(new KeyValue(doc, paramRSAKeyValue));
  }
  
  public void add(PublicKey paramPublicKey)
  {
    add(new KeyValue(doc, paramPublicKey));
  }
  
  public void add(KeyValue paramKeyValue)
  {
    constructionElement.appendChild(paramKeyValue.getElement());
    XMLUtils.addReturnToElement(constructionElement);
  }
  
  public void addMgmtData(String paramString)
  {
    add(new MgmtData(doc, paramString));
  }
  
  public void add(MgmtData paramMgmtData)
  {
    constructionElement.appendChild(paramMgmtData.getElement());
    XMLUtils.addReturnToElement(constructionElement);
  }
  
  public void add(PGPData paramPGPData)
  {
    constructionElement.appendChild(paramPGPData.getElement());
    XMLUtils.addReturnToElement(constructionElement);
  }
  
  public void addRetrievalMethod(String paramString1, Transforms paramTransforms, String paramString2)
  {
    add(new RetrievalMethod(doc, paramString1, paramTransforms, paramString2));
  }
  
  public void add(RetrievalMethod paramRetrievalMethod)
  {
    constructionElement.appendChild(paramRetrievalMethod.getElement());
    XMLUtils.addReturnToElement(constructionElement);
  }
  
  public void add(SPKIData paramSPKIData)
  {
    constructionElement.appendChild(paramSPKIData.getElement());
    XMLUtils.addReturnToElement(constructionElement);
  }
  
  public void add(X509Data paramX509Data)
  {
    if (x509Datas == null) {
      x509Datas = new ArrayList();
    }
    x509Datas.add(paramX509Data);
    constructionElement.appendChild(paramX509Data.getElement());
    XMLUtils.addReturnToElement(constructionElement);
  }
  
  public void add(EncryptedKey paramEncryptedKey)
    throws XMLEncryptionException
  {
    if (encryptedKeys == null) {
      encryptedKeys = new ArrayList();
    }
    encryptedKeys.add(paramEncryptedKey);
    XMLCipher localXMLCipher = XMLCipher.getInstance();
    constructionElement.appendChild(localXMLCipher.martial(paramEncryptedKey));
  }
  
  public void addDEREncodedKeyValue(PublicKey paramPublicKey)
    throws XMLSecurityException
  {
    add(new DEREncodedKeyValue(doc, paramPublicKey));
  }
  
  public void add(DEREncodedKeyValue paramDEREncodedKeyValue)
  {
    constructionElement.appendChild(paramDEREncodedKeyValue.getElement());
    XMLUtils.addReturnToElement(constructionElement);
  }
  
  public void addKeyInfoReference(String paramString)
    throws XMLSecurityException
  {
    add(new KeyInfoReference(doc, paramString));
  }
  
  public void add(KeyInfoReference paramKeyInfoReference)
  {
    constructionElement.appendChild(paramKeyInfoReference.getElement());
    XMLUtils.addReturnToElement(constructionElement);
  }
  
  public void addUnknownElement(Element paramElement)
  {
    constructionElement.appendChild(paramElement);
    XMLUtils.addReturnToElement(constructionElement);
  }
  
  public int lengthKeyName()
  {
    return length("http://www.w3.org/2000/09/xmldsig#", "KeyName");
  }
  
  public int lengthKeyValue()
  {
    return length("http://www.w3.org/2000/09/xmldsig#", "KeyValue");
  }
  
  public int lengthMgmtData()
  {
    return length("http://www.w3.org/2000/09/xmldsig#", "MgmtData");
  }
  
  public int lengthPGPData()
  {
    return length("http://www.w3.org/2000/09/xmldsig#", "PGPData");
  }
  
  public int lengthRetrievalMethod()
  {
    return length("http://www.w3.org/2000/09/xmldsig#", "RetrievalMethod");
  }
  
  public int lengthSPKIData()
  {
    return length("http://www.w3.org/2000/09/xmldsig#", "SPKIData");
  }
  
  public int lengthX509Data()
  {
    if (x509Datas != null) {
      return x509Datas.size();
    }
    return length("http://www.w3.org/2000/09/xmldsig#", "X509Data");
  }
  
  public int lengthDEREncodedKeyValue()
  {
    return length("http://www.w3.org/2009/xmldsig11#", "DEREncodedKeyValue");
  }
  
  public int lengthKeyInfoReference()
  {
    return length("http://www.w3.org/2009/xmldsig11#", "KeyInfoReference");
  }
  
  public int lengthUnknownElement()
  {
    int i = 0;
    NodeList localNodeList = constructionElement.getChildNodes();
    for (int j = 0; j < localNodeList.getLength(); j++)
    {
      Node localNode = localNodeList.item(j);
      if ((localNode.getNodeType() == 1) && (localNode.getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#"))) {
        i++;
      }
    }
    return i;
  }
  
  public KeyName itemKeyName(int paramInt)
    throws XMLSecurityException
  {
    Element localElement = XMLUtils.selectDsNode(constructionElement.getFirstChild(), "KeyName", paramInt);
    if (localElement != null) {
      return new KeyName(localElement, baseURI);
    }
    return null;
  }
  
  public KeyValue itemKeyValue(int paramInt)
    throws XMLSecurityException
  {
    Element localElement = XMLUtils.selectDsNode(constructionElement.getFirstChild(), "KeyValue", paramInt);
    if (localElement != null) {
      return new KeyValue(localElement, baseURI);
    }
    return null;
  }
  
  public MgmtData itemMgmtData(int paramInt)
    throws XMLSecurityException
  {
    Element localElement = XMLUtils.selectDsNode(constructionElement.getFirstChild(), "MgmtData", paramInt);
    if (localElement != null) {
      return new MgmtData(localElement, baseURI);
    }
    return null;
  }
  
  public PGPData itemPGPData(int paramInt)
    throws XMLSecurityException
  {
    Element localElement = XMLUtils.selectDsNode(constructionElement.getFirstChild(), "PGPData", paramInt);
    if (localElement != null) {
      return new PGPData(localElement, baseURI);
    }
    return null;
  }
  
  public RetrievalMethod itemRetrievalMethod(int paramInt)
    throws XMLSecurityException
  {
    Element localElement = XMLUtils.selectDsNode(constructionElement.getFirstChild(), "RetrievalMethod", paramInt);
    if (localElement != null) {
      return new RetrievalMethod(localElement, baseURI);
    }
    return null;
  }
  
  public SPKIData itemSPKIData(int paramInt)
    throws XMLSecurityException
  {
    Element localElement = XMLUtils.selectDsNode(constructionElement.getFirstChild(), "SPKIData", paramInt);
    if (localElement != null) {
      return new SPKIData(localElement, baseURI);
    }
    return null;
  }
  
  public X509Data itemX509Data(int paramInt)
    throws XMLSecurityException
  {
    if (x509Datas != null) {
      return (X509Data)x509Datas.get(paramInt);
    }
    Element localElement = XMLUtils.selectDsNode(constructionElement.getFirstChild(), "X509Data", paramInt);
    if (localElement != null) {
      return new X509Data(localElement, baseURI);
    }
    return null;
  }
  
  public EncryptedKey itemEncryptedKey(int paramInt)
    throws XMLSecurityException
  {
    if (encryptedKeys != null) {
      return (EncryptedKey)encryptedKeys.get(paramInt);
    }
    Element localElement = XMLUtils.selectXencNode(constructionElement.getFirstChild(), "EncryptedKey", paramInt);
    if (localElement != null)
    {
      XMLCipher localXMLCipher = XMLCipher.getInstance();
      localXMLCipher.init(4, null);
      return localXMLCipher.loadEncryptedKey(localElement);
    }
    return null;
  }
  
  public DEREncodedKeyValue itemDEREncodedKeyValue(int paramInt)
    throws XMLSecurityException
  {
    Element localElement = XMLUtils.selectDs11Node(constructionElement.getFirstChild(), "DEREncodedKeyValue", paramInt);
    if (localElement != null) {
      return new DEREncodedKeyValue(localElement, baseURI);
    }
    return null;
  }
  
  public KeyInfoReference itemKeyInfoReference(int paramInt)
    throws XMLSecurityException
  {
    Element localElement = XMLUtils.selectDs11Node(constructionElement.getFirstChild(), "KeyInfoReference", paramInt);
    if (localElement != null) {
      return new KeyInfoReference(localElement, baseURI);
    }
    return null;
  }
  
  public Element itemUnknownElement(int paramInt)
  {
    NodeList localNodeList = constructionElement.getChildNodes();
    int i = 0;
    for (int j = 0; j < localNodeList.getLength(); j++)
    {
      Node localNode = localNodeList.item(j);
      if ((localNode.getNodeType() == 1) && (localNode.getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#")))
      {
        i++;
        if (i == paramInt) {
          return (Element)localNode;
        }
      }
    }
    return null;
  }
  
  public boolean isEmpty()
  {
    return constructionElement.getFirstChild() == null;
  }
  
  public boolean containsKeyName()
  {
    return lengthKeyName() > 0;
  }
  
  public boolean containsKeyValue()
  {
    return lengthKeyValue() > 0;
  }
  
  public boolean containsMgmtData()
  {
    return lengthMgmtData() > 0;
  }
  
  public boolean containsPGPData()
  {
    return lengthPGPData() > 0;
  }
  
  public boolean containsRetrievalMethod()
  {
    return lengthRetrievalMethod() > 0;
  }
  
  public boolean containsSPKIData()
  {
    return lengthSPKIData() > 0;
  }
  
  public boolean containsUnknownElement()
  {
    return lengthUnknownElement() > 0;
  }
  
  public boolean containsX509Data()
  {
    return lengthX509Data() > 0;
  }
  
  public boolean containsDEREncodedKeyValue()
  {
    return lengthDEREncodedKeyValue() > 0;
  }
  
  public boolean containsKeyInfoReference()
  {
    return lengthKeyInfoReference() > 0;
  }
  
  public PublicKey getPublicKey()
    throws KeyResolverException
  {
    PublicKey localPublicKey = getPublicKeyFromInternalResolvers();
    if (localPublicKey != null)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "I could find a key using the per-KeyInfo key resolvers");
      }
      return localPublicKey;
    }
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "I couldn't find a key using the per-KeyInfo key resolvers");
    }
    localPublicKey = getPublicKeyFromStaticResolvers();
    if (localPublicKey != null)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "I could find a key using the system-wide key resolvers");
      }
      return localPublicKey;
    }
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "I couldn't find a key using the system-wide key resolvers");
    }
    return null;
  }
  
  PublicKey getPublicKeyFromStaticResolvers()
    throws KeyResolverException
  {
    Iterator localIterator1 = KeyResolver.iterator();
    while (localIterator1.hasNext())
    {
      KeyResolverSpi localKeyResolverSpi = (KeyResolverSpi)localIterator1.next();
      localKeyResolverSpi.setSecureValidation(secureValidation);
      Node localNode = constructionElement.getFirstChild();
      String str = getBaseURI();
      while (localNode != null)
      {
        if (localNode.getNodeType() == 1)
        {
          Iterator localIterator2 = storageResolvers.iterator();
          while (localIterator2.hasNext())
          {
            StorageResolver localStorageResolver = (StorageResolver)localIterator2.next();
            PublicKey localPublicKey = localKeyResolverSpi.engineLookupAndResolvePublicKey((Element)localNode, str, localStorageResolver);
            if (localPublicKey != null) {
              return localPublicKey;
            }
          }
        }
        localNode = localNode.getNextSibling();
      }
    }
    return null;
  }
  
  PublicKey getPublicKeyFromInternalResolvers()
    throws KeyResolverException
  {
    Iterator localIterator1 = internalKeyResolvers.iterator();
    while (localIterator1.hasNext())
    {
      KeyResolverSpi localKeyResolverSpi = (KeyResolverSpi)localIterator1.next();
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "Try " + localKeyResolverSpi.getClass().getName());
      }
      localKeyResolverSpi.setSecureValidation(secureValidation);
      Node localNode = constructionElement.getFirstChild();
      String str = getBaseURI();
      while (localNode != null)
      {
        if (localNode.getNodeType() == 1)
        {
          Iterator localIterator2 = storageResolvers.iterator();
          while (localIterator2.hasNext())
          {
            StorageResolver localStorageResolver = (StorageResolver)localIterator2.next();
            PublicKey localPublicKey = localKeyResolverSpi.engineLookupAndResolvePublicKey((Element)localNode, str, localStorageResolver);
            if (localPublicKey != null) {
              return localPublicKey;
            }
          }
        }
        localNode = localNode.getNextSibling();
      }
    }
    return null;
  }
  
  public X509Certificate getX509Certificate()
    throws KeyResolverException
  {
    X509Certificate localX509Certificate = getX509CertificateFromInternalResolvers();
    if (localX509Certificate != null)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "I could find a X509Certificate using the per-KeyInfo key resolvers");
      }
      return localX509Certificate;
    }
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "I couldn't find a X509Certificate using the per-KeyInfo key resolvers");
    }
    localX509Certificate = getX509CertificateFromStaticResolvers();
    if (localX509Certificate != null)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "I could find a X509Certificate using the system-wide key resolvers");
      }
      return localX509Certificate;
    }
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "I couldn't find a X509Certificate using the system-wide key resolvers");
    }
    return null;
  }
  
  X509Certificate getX509CertificateFromStaticResolvers()
    throws KeyResolverException
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Start getX509CertificateFromStaticResolvers() with " + KeyResolver.length() + " resolvers");
    }
    String str = getBaseURI();
    Iterator localIterator = KeyResolver.iterator();
    while (localIterator.hasNext())
    {
      KeyResolverSpi localKeyResolverSpi = (KeyResolverSpi)localIterator.next();
      localKeyResolverSpi.setSecureValidation(secureValidation);
      X509Certificate localX509Certificate = applyCurrentResolver(str, localKeyResolverSpi);
      if (localX509Certificate != null) {
        return localX509Certificate;
      }
    }
    return null;
  }
  
  private X509Certificate applyCurrentResolver(String paramString, KeyResolverSpi paramKeyResolverSpi)
    throws KeyResolverException
  {
    for (Node localNode = constructionElement.getFirstChild(); localNode != null; localNode = localNode.getNextSibling()) {
      if (localNode.getNodeType() == 1)
      {
        Iterator localIterator = storageResolvers.iterator();
        while (localIterator.hasNext())
        {
          StorageResolver localStorageResolver = (StorageResolver)localIterator.next();
          X509Certificate localX509Certificate = paramKeyResolverSpi.engineLookupResolveX509Certificate((Element)localNode, paramString, localStorageResolver);
          if (localX509Certificate != null) {
            return localX509Certificate;
          }
        }
      }
    }
    return null;
  }
  
  X509Certificate getX509CertificateFromInternalResolvers()
    throws KeyResolverException
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Start getX509CertificateFromInternalResolvers() with " + lengthInternalKeyResolver() + " resolvers");
    }
    String str = getBaseURI();
    Iterator localIterator = internalKeyResolvers.iterator();
    while (localIterator.hasNext())
    {
      KeyResolverSpi localKeyResolverSpi = (KeyResolverSpi)localIterator.next();
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "Try " + localKeyResolverSpi.getClass().getName());
      }
      localKeyResolverSpi.setSecureValidation(secureValidation);
      X509Certificate localX509Certificate = applyCurrentResolver(str, localKeyResolverSpi);
      if (localX509Certificate != null) {
        return localX509Certificate;
      }
    }
    return null;
  }
  
  public SecretKey getSecretKey()
    throws KeyResolverException
  {
    SecretKey localSecretKey = getSecretKeyFromInternalResolvers();
    if (localSecretKey != null)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "I could find a secret key using the per-KeyInfo key resolvers");
      }
      return localSecretKey;
    }
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "I couldn't find a secret key using the per-KeyInfo key resolvers");
    }
    localSecretKey = getSecretKeyFromStaticResolvers();
    if (localSecretKey != null)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "I could find a secret key using the system-wide key resolvers");
      }
      return localSecretKey;
    }
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "I couldn't find a secret key using the system-wide key resolvers");
    }
    return null;
  }
  
  SecretKey getSecretKeyFromStaticResolvers()
    throws KeyResolverException
  {
    Iterator localIterator1 = KeyResolver.iterator();
    while (localIterator1.hasNext())
    {
      KeyResolverSpi localKeyResolverSpi = (KeyResolverSpi)localIterator1.next();
      localKeyResolverSpi.setSecureValidation(secureValidation);
      Node localNode = constructionElement.getFirstChild();
      String str = getBaseURI();
      while (localNode != null)
      {
        if (localNode.getNodeType() == 1)
        {
          Iterator localIterator2 = storageResolvers.iterator();
          while (localIterator2.hasNext())
          {
            StorageResolver localStorageResolver = (StorageResolver)localIterator2.next();
            SecretKey localSecretKey = localKeyResolverSpi.engineLookupAndResolveSecretKey((Element)localNode, str, localStorageResolver);
            if (localSecretKey != null) {
              return localSecretKey;
            }
          }
        }
        localNode = localNode.getNextSibling();
      }
    }
    return null;
  }
  
  SecretKey getSecretKeyFromInternalResolvers()
    throws KeyResolverException
  {
    Iterator localIterator1 = internalKeyResolvers.iterator();
    while (localIterator1.hasNext())
    {
      KeyResolverSpi localKeyResolverSpi = (KeyResolverSpi)localIterator1.next();
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "Try " + localKeyResolverSpi.getClass().getName());
      }
      localKeyResolverSpi.setSecureValidation(secureValidation);
      Node localNode = constructionElement.getFirstChild();
      String str = getBaseURI();
      while (localNode != null)
      {
        if (localNode.getNodeType() == 1)
        {
          Iterator localIterator2 = storageResolvers.iterator();
          while (localIterator2.hasNext())
          {
            StorageResolver localStorageResolver = (StorageResolver)localIterator2.next();
            SecretKey localSecretKey = localKeyResolverSpi.engineLookupAndResolveSecretKey((Element)localNode, str, localStorageResolver);
            if (localSecretKey != null) {
              return localSecretKey;
            }
          }
        }
        localNode = localNode.getNextSibling();
      }
    }
    return null;
  }
  
  public PrivateKey getPrivateKey()
    throws KeyResolverException
  {
    PrivateKey localPrivateKey = getPrivateKeyFromInternalResolvers();
    if (localPrivateKey != null)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "I could find a private key using the per-KeyInfo key resolvers");
      }
      return localPrivateKey;
    }
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "I couldn't find a secret key using the per-KeyInfo key resolvers");
    }
    localPrivateKey = getPrivateKeyFromStaticResolvers();
    if (localPrivateKey != null)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "I could find a private key using the system-wide key resolvers");
      }
      return localPrivateKey;
    }
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "I couldn't find a private key using the system-wide key resolvers");
    }
    return null;
  }
  
  PrivateKey getPrivateKeyFromStaticResolvers()
    throws KeyResolverException
  {
    Iterator localIterator = KeyResolver.iterator();
    while (localIterator.hasNext())
    {
      KeyResolverSpi localKeyResolverSpi = (KeyResolverSpi)localIterator.next();
      localKeyResolverSpi.setSecureValidation(secureValidation);
      Node localNode = constructionElement.getFirstChild();
      String str = getBaseURI();
      while (localNode != null)
      {
        if (localNode.getNodeType() == 1)
        {
          PrivateKey localPrivateKey = localKeyResolverSpi.engineLookupAndResolvePrivateKey((Element)localNode, str, null);
          if (localPrivateKey != null) {
            return localPrivateKey;
          }
        }
        localNode = localNode.getNextSibling();
      }
    }
    return null;
  }
  
  PrivateKey getPrivateKeyFromInternalResolvers()
    throws KeyResolverException
  {
    Iterator localIterator = internalKeyResolvers.iterator();
    while (localIterator.hasNext())
    {
      KeyResolverSpi localKeyResolverSpi = (KeyResolverSpi)localIterator.next();
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "Try " + localKeyResolverSpi.getClass().getName());
      }
      localKeyResolverSpi.setSecureValidation(secureValidation);
      Node localNode = constructionElement.getFirstChild();
      String str = getBaseURI();
      while (localNode != null)
      {
        if (localNode.getNodeType() == 1)
        {
          PrivateKey localPrivateKey = localKeyResolverSpi.engineLookupAndResolvePrivateKey((Element)localNode, str, null);
          if (localPrivateKey != null) {
            return localPrivateKey;
          }
        }
        localNode = localNode.getNextSibling();
      }
    }
    return null;
  }
  
  public void registerInternalKeyResolver(KeyResolverSpi paramKeyResolverSpi)
  {
    internalKeyResolvers.add(paramKeyResolverSpi);
  }
  
  int lengthInternalKeyResolver()
  {
    return internalKeyResolvers.size();
  }
  
  KeyResolverSpi itemInternalKeyResolver(int paramInt)
  {
    return (KeyResolverSpi)internalKeyResolvers.get(paramInt);
  }
  
  public void addStorageResolver(StorageResolver paramStorageResolver)
  {
    if (storageResolvers == nullList) {
      storageResolvers = new ArrayList();
    }
    storageResolvers.add(paramStorageResolver);
  }
  
  public String getBaseLocalName()
  {
    return "KeyInfo";
  }
  
  static
  {
    ArrayList localArrayList = new ArrayList(1);
    localArrayList.add(null);
    nullList = Collections.unmodifiableList(localArrayList);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\keys\KeyInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */