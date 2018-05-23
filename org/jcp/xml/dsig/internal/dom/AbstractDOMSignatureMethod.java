package org.jcp.xml.dsig.internal.dom;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLValidateContext;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

abstract class AbstractDOMSignatureMethod
  extends DOMStructure
  implements SignatureMethod
{
  AbstractDOMSignatureMethod() {}
  
  abstract boolean verify(Key paramKey, SignedInfo paramSignedInfo, byte[] paramArrayOfByte, XMLValidateContext paramXMLValidateContext)
    throws InvalidKeyException, SignatureException, XMLSignatureException;
  
  abstract byte[] sign(Key paramKey, SignedInfo paramSignedInfo, XMLSignContext paramXMLSignContext)
    throws InvalidKeyException, XMLSignatureException;
  
  abstract String getJCAAlgorithm();
  
  abstract Type getAlgorithmType();
  
  public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext)
    throws MarshalException
  {
    Document localDocument = DOMUtils.getOwnerDocument(paramNode);
    Element localElement = DOMUtils.createElement(localDocument, "SignatureMethod", "http://www.w3.org/2000/09/xmldsig#", paramString);
    DOMUtils.setAttribute(localElement, "Algorithm", getAlgorithm());
    if (getParameterSpec() != null) {
      marshalParams(localElement, paramString);
    }
    paramNode.appendChild(localElement);
  }
  
  void marshalParams(Element paramElement, String paramString)
    throws MarshalException
  {
    throw new MarshalException("no parameters should be specified for the " + getAlgorithm() + " SignatureMethod algorithm");
  }
  
  SignatureMethodParameterSpec unmarshalParams(Element paramElement)
    throws MarshalException
  {
    throw new MarshalException("no parameters should be specified for the " + getAlgorithm() + " SignatureMethod algorithm");
  }
  
  void checkParams(SignatureMethodParameterSpec paramSignatureMethodParameterSpec)
    throws InvalidAlgorithmParameterException
  {
    if (paramSignatureMethodParameterSpec != null) {
      throw new InvalidAlgorithmParameterException("no parameters should be specified for the " + getAlgorithm() + " SignatureMethod algorithm");
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof SignatureMethod)) {
      return false;
    }
    SignatureMethod localSignatureMethod = (SignatureMethod)paramObject;
    return (getAlgorithm().equals(localSignatureMethod.getAlgorithm())) && (paramsEqual(localSignatureMethod.getParameterSpec()));
  }
  
  public int hashCode()
  {
    int i = 17;
    i = 31 * i + getAlgorithm().hashCode();
    AlgorithmParameterSpec localAlgorithmParameterSpec = getParameterSpec();
    if (localAlgorithmParameterSpec != null) {
      i = 31 * i + localAlgorithmParameterSpec.hashCode();
    }
    return i;
  }
  
  boolean paramsEqual(AlgorithmParameterSpec paramAlgorithmParameterSpec)
  {
    return getParameterSpec() == paramAlgorithmParameterSpec;
  }
  
  static enum Type
  {
    DSA,  RSA,  ECDSA,  HMAC;
    
    private Type() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\jcp\xml\dsig\internal\dom\AbstractDOMSignatureMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */