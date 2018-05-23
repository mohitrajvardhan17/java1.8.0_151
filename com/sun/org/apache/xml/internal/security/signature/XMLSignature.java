package com.sun.org.apache.xml.internal.security.signature;

import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithm;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.KeyInfo;
import com.sun.org.apache.xml.internal.security.keys.content.X509Data;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.sun.org.apache.xml.internal.security.utils.I18n;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.SignerOutputStream;
import com.sun.org.apache.xml.internal.security.utils.UnsyncBufferedOutputStream;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Key;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public final class XMLSignature
  extends SignatureElementProxy
{
  public static final String ALGO_ID_MAC_HMAC_SHA1 = "http://www.w3.org/2000/09/xmldsig#hmac-sha1";
  public static final String ALGO_ID_SIGNATURE_DSA = "http://www.w3.org/2000/09/xmldsig#dsa-sha1";
  public static final String ALGO_ID_SIGNATURE_DSA_SHA256 = "http://www.w3.org/2009/xmldsig11#dsa-sha256";
  public static final String ALGO_ID_SIGNATURE_RSA = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
  public static final String ALGO_ID_SIGNATURE_RSA_SHA1 = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
  public static final String ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5 = "http://www.w3.org/2001/04/xmldsig-more#rsa-md5";
  public static final String ALGO_ID_SIGNATURE_RSA_RIPEMD160 = "http://www.w3.org/2001/04/xmldsig-more#rsa-ripemd160";
  public static final String ALGO_ID_SIGNATURE_RSA_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
  public static final String ALGO_ID_SIGNATURE_RSA_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384";
  public static final String ALGO_ID_SIGNATURE_RSA_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512";
  public static final String ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5 = "http://www.w3.org/2001/04/xmldsig-more#hmac-md5";
  public static final String ALGO_ID_MAC_HMAC_RIPEMD160 = "http://www.w3.org/2001/04/xmldsig-more#hmac-ripemd160";
  public static final String ALGO_ID_MAC_HMAC_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha256";
  public static final String ALGO_ID_MAC_HMAC_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha384";
  public static final String ALGO_ID_MAC_HMAC_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha512";
  public static final String ALGO_ID_SIGNATURE_ECDSA_SHA1 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1";
  public static final String ALGO_ID_SIGNATURE_ECDSA_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256";
  public static final String ALGO_ID_SIGNATURE_ECDSA_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384";
  public static final String ALGO_ID_SIGNATURE_ECDSA_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512";
  private static Logger log = Logger.getLogger(XMLSignature.class.getName());
  private SignedInfo signedInfo;
  private KeyInfo keyInfo;
  private boolean followManifestsDuringValidation = false;
  private Element signatureValueElement;
  private static final int MODE_SIGN = 0;
  private static final int MODE_VERIFY = 1;
  private int state = 0;
  
  public XMLSignature(Document paramDocument, String paramString1, String paramString2)
    throws XMLSecurityException
  {
    this(paramDocument, paramString1, paramString2, 0, "http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
  }
  
  public XMLSignature(Document paramDocument, String paramString1, String paramString2, int paramInt)
    throws XMLSecurityException
  {
    this(paramDocument, paramString1, paramString2, paramInt, "http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
  }
  
  public XMLSignature(Document paramDocument, String paramString1, String paramString2, String paramString3)
    throws XMLSecurityException
  {
    this(paramDocument, paramString1, paramString2, 0, paramString3);
  }
  
  public XMLSignature(Document paramDocument, String paramString1, String paramString2, int paramInt, String paramString3)
    throws XMLSecurityException
  {
    super(paramDocument);
    String str = getDefaultPrefix("http://www.w3.org/2000/09/xmldsig#");
    if ((str == null) || (str.length() == 0)) {
      constructionElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "http://www.w3.org/2000/09/xmldsig#");
    } else {
      constructionElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + str, "http://www.w3.org/2000/09/xmldsig#");
    }
    XMLUtils.addReturnToElement(constructionElement);
    baseURI = paramString1;
    signedInfo = new SignedInfo(doc, paramString2, paramInt, paramString3);
    constructionElement.appendChild(signedInfo.getElement());
    XMLUtils.addReturnToElement(constructionElement);
    signatureValueElement = XMLUtils.createElementInSignatureSpace(doc, "SignatureValue");
    constructionElement.appendChild(signatureValueElement);
    XMLUtils.addReturnToElement(constructionElement);
  }
  
  public XMLSignature(Document paramDocument, String paramString, Element paramElement1, Element paramElement2)
    throws XMLSecurityException
  {
    super(paramDocument);
    String str = getDefaultPrefix("http://www.w3.org/2000/09/xmldsig#");
    if ((str == null) || (str.length() == 0)) {
      constructionElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "http://www.w3.org/2000/09/xmldsig#");
    } else {
      constructionElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + str, "http://www.w3.org/2000/09/xmldsig#");
    }
    XMLUtils.addReturnToElement(constructionElement);
    baseURI = paramString;
    signedInfo = new SignedInfo(doc, paramElement1, paramElement2);
    constructionElement.appendChild(signedInfo.getElement());
    XMLUtils.addReturnToElement(constructionElement);
    signatureValueElement = XMLUtils.createElementInSignatureSpace(doc, "SignatureValue");
    constructionElement.appendChild(signatureValueElement);
    XMLUtils.addReturnToElement(constructionElement);
  }
  
  public XMLSignature(Element paramElement, String paramString)
    throws XMLSignatureException, XMLSecurityException
  {
    this(paramElement, paramString, false);
  }
  
  public XMLSignature(Element paramElement, String paramString, boolean paramBoolean)
    throws XMLSignatureException, XMLSecurityException
  {
    super(paramElement, paramString);
    Element localElement1 = XMLUtils.getNextElement(paramElement.getFirstChild());
    if (localElement1 == null)
    {
      localObject = new Object[] { "SignedInfo", "Signature" };
      throw new XMLSignatureException("xml.WrongContent", (Object[])localObject);
    }
    signedInfo = new SignedInfo(localElement1, paramString, paramBoolean);
    localElement1 = XMLUtils.getNextElement(paramElement.getFirstChild());
    signatureValueElement = XMLUtils.getNextElement(localElement1.getNextSibling());
    if (signatureValueElement == null)
    {
      localObject = new Object[] { "SignatureValue", "Signature" };
      throw new XMLSignatureException("xml.WrongContent", (Object[])localObject);
    }
    Object localObject = signatureValueElement.getAttributeNodeNS(null, "Id");
    if (localObject != null) {
      signatureValueElement.setIdAttributeNode((Attr)localObject, true);
    }
    Element localElement2 = XMLUtils.getNextElement(signatureValueElement.getNextSibling());
    if ((localElement2 != null) && (localElement2.getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#")) && (localElement2.getLocalName().equals("KeyInfo")))
    {
      keyInfo = new KeyInfo(localElement2, paramString);
      keyInfo.setSecureValidation(paramBoolean);
    }
    for (Element localElement3 = XMLUtils.getNextElement(signatureValueElement.getNextSibling()); localElement3 != null; localElement3 = XMLUtils.getNextElement(localElement3.getNextSibling()))
    {
      Attr localAttr = localElement3.getAttributeNodeNS(null, "Id");
      if (localAttr != null) {
        localElement3.setIdAttributeNode(localAttr, true);
      }
      NodeList localNodeList = localElement3.getChildNodes();
      int i = localNodeList.getLength();
      for (int j = 0; j < i; j++)
      {
        Node localNode = localNodeList.item(j);
        if (localNode.getNodeType() == 1)
        {
          Element localElement4 = (Element)localNode;
          String str = localElement4.getLocalName();
          if (str.equals("Manifest")) {
            new Manifest(localElement4, paramString);
          } else if (str.equals("SignatureProperties")) {
            new SignatureProperties(localElement4, paramString);
          }
        }
      }
    }
    state = 1;
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
  
  public SignedInfo getSignedInfo()
  {
    return signedInfo;
  }
  
  public byte[] getSignatureValue()
    throws XMLSignatureException
  {
    try
    {
      return Base64.decode(signatureValueElement);
    }
    catch (Base64DecodingException localBase64DecodingException)
    {
      throw new XMLSignatureException("empty", localBase64DecodingException);
    }
  }
  
  private void setSignatureValueElement(byte[] paramArrayOfByte)
  {
    while (signatureValueElement.hasChildNodes()) {
      signatureValueElement.removeChild(signatureValueElement.getFirstChild());
    }
    String str = Base64.encode(paramArrayOfByte);
    if ((str.length() > 76) && (!XMLUtils.ignoreLineBreaks())) {
      str = "\n" + str + "\n";
    }
    Text localText = doc.createTextNode(str);
    signatureValueElement.appendChild(localText);
  }
  
  public KeyInfo getKeyInfo()
  {
    if ((state == 0) && (keyInfo == null))
    {
      keyInfo = new KeyInfo(doc);
      Element localElement1 = keyInfo.getElement();
      Element localElement2 = XMLUtils.selectDsNode(constructionElement.getFirstChild(), "Object", 0);
      if (localElement2 != null)
      {
        constructionElement.insertBefore(localElement1, localElement2);
        XMLUtils.addReturnBeforeChild(constructionElement, localElement2);
      }
      else
      {
        constructionElement.appendChild(localElement1);
        XMLUtils.addReturnToElement(constructionElement);
      }
    }
    return keyInfo;
  }
  
  public void appendObject(ObjectContainer paramObjectContainer)
    throws XMLSignatureException
  {
    constructionElement.appendChild(paramObjectContainer.getElement());
    XMLUtils.addReturnToElement(constructionElement);
  }
  
  public ObjectContainer getObjectItem(int paramInt)
  {
    Element localElement = XMLUtils.selectDsNode(constructionElement.getFirstChild(), "Object", paramInt);
    try
    {
      return new ObjectContainer(localElement, baseURI);
    }
    catch (XMLSecurityException localXMLSecurityException) {}
    return null;
  }
  
  public int getObjectLength()
  {
    return length("http://www.w3.org/2000/09/xmldsig#", "Object");
  }
  
  public void sign(Key paramKey)
    throws XMLSignatureException
  {
    if ((paramKey instanceof PublicKey)) {
      throw new IllegalArgumentException(I18n.translate("algorithms.operationOnlyVerification"));
    }
    try
    {
      SignedInfo localSignedInfo = getSignedInfo();
      localSignatureAlgorithm = localSignedInfo.getSignatureAlgorithm();
      UnsyncBufferedOutputStream localUnsyncBufferedOutputStream = null;
      try
      {
        localSignatureAlgorithm.initSign(paramKey);
        localSignedInfo.generateDigestValues();
        localUnsyncBufferedOutputStream = new UnsyncBufferedOutputStream(new SignerOutputStream(localSignatureAlgorithm));
        localSignedInfo.signInOctetStream(localUnsyncBufferedOutputStream);
        if (localUnsyncBufferedOutputStream != null) {
          try
          {
            localUnsyncBufferedOutputStream.close();
          }
          catch (IOException localIOException1)
          {
            if (log.isLoggable(Level.FINE)) {
              log.log(Level.FINE, localIOException1.getMessage(), localIOException1);
            }
          }
        }
        setSignatureValueElement(localSignatureAlgorithm.sign());
      }
      catch (XMLSecurityException localXMLSecurityException2)
      {
        throw localXMLSecurityException2;
      }
      finally
      {
        if (localUnsyncBufferedOutputStream != null) {
          try
          {
            localUnsyncBufferedOutputStream.close();
          }
          catch (IOException localIOException2)
          {
            if (log.isLoggable(Level.FINE)) {
              log.log(Level.FINE, localIOException2.getMessage(), localIOException2);
            }
          }
        }
      }
    }
    catch (XMLSignatureException localXMLSignatureException)
    {
      SignatureAlgorithm localSignatureAlgorithm;
      throw localXMLSignatureException;
    }
    catch (CanonicalizationException localCanonicalizationException)
    {
      throw new XMLSignatureException("empty", localCanonicalizationException);
    }
    catch (InvalidCanonicalizerException localInvalidCanonicalizerException)
    {
      throw new XMLSignatureException("empty", localInvalidCanonicalizerException);
    }
    catch (XMLSecurityException localXMLSecurityException1)
    {
      throw new XMLSignatureException("empty", localXMLSecurityException1);
    }
  }
  
  public void addResourceResolver(ResourceResolver paramResourceResolver)
  {
    getSignedInfo().addResourceResolver(paramResourceResolver);
  }
  
  public void addResourceResolver(ResourceResolverSpi paramResourceResolverSpi)
  {
    getSignedInfo().addResourceResolver(paramResourceResolverSpi);
  }
  
  public boolean checkSignatureValue(X509Certificate paramX509Certificate)
    throws XMLSignatureException
  {
    if (paramX509Certificate != null) {
      return checkSignatureValue(paramX509Certificate.getPublicKey());
    }
    Object[] arrayOfObject = { "Didn't get a certificate" };
    throw new XMLSignatureException("empty", arrayOfObject);
  }
  
  public boolean checkSignatureValue(Key paramKey)
    throws XMLSignatureException
  {
    Object localObject;
    if (paramKey == null)
    {
      localObject = new Object[] { "Didn't get a key" };
      throw new XMLSignatureException("empty", (Object[])localObject);
    }
    try
    {
      localObject = getSignedInfo();
      SignatureAlgorithm localSignatureAlgorithm = ((SignedInfo)localObject).getSignatureAlgorithm();
      if (log.isLoggable(Level.FINE))
      {
        log.log(Level.FINE, "signatureMethodURI = " + localSignatureAlgorithm.getAlgorithmURI());
        log.log(Level.FINE, "jceSigAlgorithm    = " + localSignatureAlgorithm.getJCEAlgorithmString());
        log.log(Level.FINE, "jceSigProvider     = " + localSignatureAlgorithm.getJCEProviderName());
        log.log(Level.FINE, "PublicKey = " + paramKey);
      }
      byte[] arrayOfByte = null;
      try
      {
        localSignatureAlgorithm.initVerify(paramKey);
        SignerOutputStream localSignerOutputStream = new SignerOutputStream(localSignatureAlgorithm);
        UnsyncBufferedOutputStream localUnsyncBufferedOutputStream = new UnsyncBufferedOutputStream(localSignerOutputStream);
        ((SignedInfo)localObject).signInOctetStream(localUnsyncBufferedOutputStream);
        localUnsyncBufferedOutputStream.close();
        arrayOfByte = getSignatureValue();
      }
      catch (IOException localIOException)
      {
        if (log.isLoggable(Level.FINE)) {
          log.log(Level.FINE, localIOException.getMessage(), localIOException);
        }
      }
      catch (XMLSecurityException localXMLSecurityException2)
      {
        throw localXMLSecurityException2;
      }
      if (!localSignatureAlgorithm.verify(arrayOfByte))
      {
        log.log(Level.WARNING, "Signature verification failed.");
        return false;
      }
      return ((SignedInfo)localObject).verify(followManifestsDuringValidation);
    }
    catch (XMLSignatureException localXMLSignatureException)
    {
      throw localXMLSignatureException;
    }
    catch (XMLSecurityException localXMLSecurityException1)
    {
      throw new XMLSignatureException("empty", localXMLSecurityException1);
    }
  }
  
  public void addDocument(String paramString1, Transforms paramTransforms, String paramString2, String paramString3, String paramString4)
    throws XMLSignatureException
  {
    signedInfo.addDocument(baseURI, paramString1, paramTransforms, paramString2, paramString3, paramString4);
  }
  
  public void addDocument(String paramString1, Transforms paramTransforms, String paramString2)
    throws XMLSignatureException
  {
    signedInfo.addDocument(baseURI, paramString1, paramTransforms, paramString2, null, null);
  }
  
  public void addDocument(String paramString, Transforms paramTransforms)
    throws XMLSignatureException
  {
    signedInfo.addDocument(baseURI, paramString, paramTransforms, "http://www.w3.org/2000/09/xmldsig#sha1", null, null);
  }
  
  public void addDocument(String paramString)
    throws XMLSignatureException
  {
    signedInfo.addDocument(baseURI, paramString, null, "http://www.w3.org/2000/09/xmldsig#sha1", null, null);
  }
  
  public void addKeyInfo(X509Certificate paramX509Certificate)
    throws XMLSecurityException
  {
    X509Data localX509Data = new X509Data(doc);
    localX509Data.addCertificate(paramX509Certificate);
    getKeyInfo().add(localX509Data);
  }
  
  public void addKeyInfo(PublicKey paramPublicKey)
  {
    getKeyInfo().add(paramPublicKey);
  }
  
  public SecretKey createSecretKey(byte[] paramArrayOfByte)
  {
    return getSignedInfo().createSecretKey(paramArrayOfByte);
  }
  
  public void setFollowNestedManifests(boolean paramBoolean)
  {
    followManifestsDuringValidation = paramBoolean;
  }
  
  public String getBaseLocalName()
  {
    return "Signature";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\signature\XMLSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */