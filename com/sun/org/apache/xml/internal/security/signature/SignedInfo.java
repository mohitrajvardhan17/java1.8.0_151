package com.sun.org.apache.xml.internal.security.signature;

import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithm;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.transforms.params.InclusiveNamespaces;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class SignedInfo
  extends Manifest
{
  private SignatureAlgorithm signatureAlgorithm = null;
  private byte[] c14nizedBytes = null;
  private Element c14nMethod;
  private Element signatureMethod;
  
  public SignedInfo(Document paramDocument)
    throws XMLSecurityException
  {
    this(paramDocument, "http://www.w3.org/2000/09/xmldsig#dsa-sha1", "http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
  }
  
  public SignedInfo(Document paramDocument, String paramString1, String paramString2)
    throws XMLSecurityException
  {
    this(paramDocument, paramString1, 0, paramString2);
  }
  
  public SignedInfo(Document paramDocument, String paramString1, int paramInt, String paramString2)
    throws XMLSecurityException
  {
    super(paramDocument);
    c14nMethod = XMLUtils.createElementInSignatureSpace(doc, "CanonicalizationMethod");
    c14nMethod.setAttributeNS(null, "Algorithm", paramString2);
    constructionElement.appendChild(c14nMethod);
    XMLUtils.addReturnToElement(constructionElement);
    if (paramInt > 0) {
      signatureAlgorithm = new SignatureAlgorithm(doc, paramString1, paramInt);
    } else {
      signatureAlgorithm = new SignatureAlgorithm(doc, paramString1);
    }
    signatureMethod = signatureAlgorithm.getElement();
    constructionElement.appendChild(signatureMethod);
    XMLUtils.addReturnToElement(constructionElement);
  }
  
  public SignedInfo(Document paramDocument, Element paramElement1, Element paramElement2)
    throws XMLSecurityException
  {
    super(paramDocument);
    c14nMethod = paramElement2;
    constructionElement.appendChild(c14nMethod);
    XMLUtils.addReturnToElement(constructionElement);
    signatureAlgorithm = new SignatureAlgorithm(paramElement1, null);
    signatureMethod = signatureAlgorithm.getElement();
    constructionElement.appendChild(signatureMethod);
    XMLUtils.addReturnToElement(constructionElement);
  }
  
  public SignedInfo(Element paramElement, String paramString)
    throws XMLSecurityException
  {
    this(paramElement, paramString, false);
  }
  
  public SignedInfo(Element paramElement, String paramString, boolean paramBoolean)
    throws XMLSecurityException
  {
    super(reparseSignedInfoElem(paramElement), paramString, paramBoolean);
    c14nMethod = XMLUtils.getNextElement(paramElement.getFirstChild());
    signatureMethod = XMLUtils.getNextElement(c14nMethod.getNextSibling());
    signatureAlgorithm = new SignatureAlgorithm(signatureMethod, getBaseURI(), paramBoolean);
  }
  
  private static Element reparseSignedInfoElem(Element paramElement)
    throws XMLSecurityException
  {
    Element localElement = XMLUtils.getNextElement(paramElement.getFirstChild());
    String str = localElement.getAttributeNS(null, "Algorithm");
    if ((!str.equals("http://www.w3.org/TR/2001/REC-xml-c14n-20010315")) && (!str.equals("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments")) && (!str.equals("http://www.w3.org/2001/10/xml-exc-c14n#")) && (!str.equals("http://www.w3.org/2001/10/xml-exc-c14n#WithComments")) && (!str.equals("http://www.w3.org/2006/12/xml-c14n11")) && (!str.equals("http://www.w3.org/2006/12/xml-c14n11#WithComments"))) {
      try
      {
        Canonicalizer localCanonicalizer = Canonicalizer.getInstance(str);
        byte[] arrayOfByte = localCanonicalizer.canonicalizeSubtree(paramElement);
        DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
        localDocumentBuilderFactory.setNamespaceAware(true);
        localDocumentBuilderFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE.booleanValue());
        DocumentBuilder localDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
        Document localDocument = localDocumentBuilder.parse(new ByteArrayInputStream(arrayOfByte));
        Node localNode = paramElement.getOwnerDocument().importNode(localDocument.getDocumentElement(), true);
        paramElement.getParentNode().replaceChild(localNode, paramElement);
        return (Element)localNode;
      }
      catch (ParserConfigurationException localParserConfigurationException)
      {
        throw new XMLSecurityException("empty", localParserConfigurationException);
      }
      catch (IOException localIOException)
      {
        throw new XMLSecurityException("empty", localIOException);
      }
      catch (SAXException localSAXException)
      {
        throw new XMLSecurityException("empty", localSAXException);
      }
    }
    return paramElement;
  }
  
  public boolean verify()
    throws MissingResourceFailureException, XMLSecurityException
  {
    return super.verifyReferences(false);
  }
  
  public boolean verify(boolean paramBoolean)
    throws MissingResourceFailureException, XMLSecurityException
  {
    return super.verifyReferences(paramBoolean);
  }
  
  public byte[] getCanonicalizedOctetStream()
    throws CanonicalizationException, InvalidCanonicalizerException, XMLSecurityException
  {
    if (c14nizedBytes == null)
    {
      Canonicalizer localCanonicalizer = Canonicalizer.getInstance(getCanonicalizationMethodURI());
      c14nizedBytes = localCanonicalizer.canonicalizeSubtree(constructionElement);
    }
    return (byte[])c14nizedBytes.clone();
  }
  
  public void signInOctetStream(OutputStream paramOutputStream)
    throws CanonicalizationException, InvalidCanonicalizerException, XMLSecurityException
  {
    if (c14nizedBytes == null)
    {
      Canonicalizer localCanonicalizer = Canonicalizer.getInstance(getCanonicalizationMethodURI());
      localCanonicalizer.setWriter(paramOutputStream);
      String str = getInclusiveNamespaces();
      if (str == null) {
        localCanonicalizer.canonicalizeSubtree(constructionElement);
      } else {
        localCanonicalizer.canonicalizeSubtree(constructionElement, str);
      }
    }
    else
    {
      try
      {
        paramOutputStream.write(c14nizedBytes);
      }
      catch (IOException localIOException)
      {
        throw new RuntimeException(localIOException);
      }
    }
  }
  
  public String getCanonicalizationMethodURI()
  {
    return c14nMethod.getAttributeNS(null, "Algorithm");
  }
  
  public String getSignatureMethodURI()
  {
    Element localElement = getSignatureMethodElement();
    if (localElement != null) {
      return localElement.getAttributeNS(null, "Algorithm");
    }
    return null;
  }
  
  public Element getSignatureMethodElement()
  {
    return signatureMethod;
  }
  
  public SecretKey createSecretKey(byte[] paramArrayOfByte)
  {
    return new SecretKeySpec(paramArrayOfByte, signatureAlgorithm.getJCEAlgorithmString());
  }
  
  protected SignatureAlgorithm getSignatureAlgorithm()
  {
    return signatureAlgorithm;
  }
  
  public String getBaseLocalName()
  {
    return "SignedInfo";
  }
  
  public String getInclusiveNamespaces()
  {
    String str1 = c14nMethod.getAttributeNS(null, "Algorithm");
    if ((!str1.equals("http://www.w3.org/2001/10/xml-exc-c14n#")) && (!str1.equals("http://www.w3.org/2001/10/xml-exc-c14n#WithComments"))) {
      return null;
    }
    Element localElement = XMLUtils.getNextElement(c14nMethod.getFirstChild());
    if (localElement != null) {
      try
      {
        String str2 = new InclusiveNamespaces(localElement, "http://www.w3.org/2001/10/xml-exc-c14n#").getInclusiveNamespaces();
        return str2;
      }
      catch (XMLSecurityException localXMLSecurityException)
      {
        return null;
      }
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\signature\SignedInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */