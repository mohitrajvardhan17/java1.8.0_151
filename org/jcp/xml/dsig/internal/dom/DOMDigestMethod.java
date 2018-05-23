package org.jcp.xml.dsig.internal.dom;

import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.spec.DigestMethodParameterSpec;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class DOMDigestMethod
  extends DOMStructure
  implements DigestMethod
{
  static final String SHA384 = "http://www.w3.org/2001/04/xmldsig-more#sha384";
  private DigestMethodParameterSpec params;
  
  DOMDigestMethod(AlgorithmParameterSpec paramAlgorithmParameterSpec)
    throws InvalidAlgorithmParameterException
  {
    if ((paramAlgorithmParameterSpec != null) && (!(paramAlgorithmParameterSpec instanceof DigestMethodParameterSpec))) {
      throw new InvalidAlgorithmParameterException("params must be of type DigestMethodParameterSpec");
    }
    checkParams((DigestMethodParameterSpec)paramAlgorithmParameterSpec);
    params = ((DigestMethodParameterSpec)paramAlgorithmParameterSpec);
  }
  
  DOMDigestMethod(Element paramElement)
    throws MarshalException
  {
    Element localElement = DOMUtils.getFirstChildElement(paramElement);
    if (localElement != null) {
      params = unmarshalParams(localElement);
    }
    try
    {
      checkParams(params);
    }
    catch (InvalidAlgorithmParameterException localInvalidAlgorithmParameterException)
    {
      throw new MarshalException(localInvalidAlgorithmParameterException);
    }
  }
  
  static DigestMethod unmarshal(Element paramElement)
    throws MarshalException
  {
    String str = DOMUtils.getAttributeValue(paramElement, "Algorithm");
    if (str.equals("http://www.w3.org/2000/09/xmldsig#sha1")) {
      return new SHA1(paramElement);
    }
    if (str.equals("http://www.w3.org/2001/04/xmlenc#sha256")) {
      return new SHA256(paramElement);
    }
    if (str.equals("http://www.w3.org/2001/04/xmldsig-more#sha384")) {
      return new SHA384(paramElement);
    }
    if (str.equals("http://www.w3.org/2001/04/xmlenc#sha512")) {
      return new SHA512(paramElement);
    }
    throw new MarshalException("unsupported DigestMethod algorithm: " + str);
  }
  
  void checkParams(DigestMethodParameterSpec paramDigestMethodParameterSpec)
    throws InvalidAlgorithmParameterException
  {
    if (paramDigestMethodParameterSpec != null) {
      throw new InvalidAlgorithmParameterException("no parameters should be specified for the " + getMessageDigestAlgorithm() + " DigestMethod algorithm");
    }
  }
  
  public final AlgorithmParameterSpec getParameterSpec()
  {
    return params;
  }
  
  DigestMethodParameterSpec unmarshalParams(Element paramElement)
    throws MarshalException
  {
    throw new MarshalException("no parameters should be specified for the " + getMessageDigestAlgorithm() + " DigestMethod algorithm");
  }
  
  public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext)
    throws MarshalException
  {
    Document localDocument = DOMUtils.getOwnerDocument(paramNode);
    Element localElement = DOMUtils.createElement(localDocument, "DigestMethod", "http://www.w3.org/2000/09/xmldsig#", paramString);
    DOMUtils.setAttribute(localElement, "Algorithm", getAlgorithm());
    if (params != null) {
      marshalParams(localElement, paramString);
    }
    paramNode.appendChild(localElement);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof DigestMethod)) {
      return false;
    }
    DigestMethod localDigestMethod = (DigestMethod)paramObject;
    boolean bool = params == null ? false : localDigestMethod.getParameterSpec() == null ? true : params.equals(localDigestMethod.getParameterSpec());
    return (getAlgorithm().equals(localDigestMethod.getAlgorithm())) && (bool);
  }
  
  public int hashCode()
  {
    int i = 17;
    if (params != null) {
      i = 31 * i + params.hashCode();
    }
    i = 31 * i + getAlgorithm().hashCode();
    return i;
  }
  
  void marshalParams(Element paramElement, String paramString)
    throws MarshalException
  {
    throw new MarshalException("no parameters should be specified for the " + getMessageDigestAlgorithm() + " DigestMethod algorithm");
  }
  
  abstract String getMessageDigestAlgorithm();
  
  static final class SHA1
    extends DOMDigestMethod
  {
    SHA1(AlgorithmParameterSpec paramAlgorithmParameterSpec)
      throws InvalidAlgorithmParameterException
    {
      super();
    }
    
    SHA1(Element paramElement)
      throws MarshalException
    {
      super();
    }
    
    public String getAlgorithm()
    {
      return "http://www.w3.org/2000/09/xmldsig#sha1";
    }
    
    String getMessageDigestAlgorithm()
    {
      return "SHA-1";
    }
  }
  
  static final class SHA256
    extends DOMDigestMethod
  {
    SHA256(AlgorithmParameterSpec paramAlgorithmParameterSpec)
      throws InvalidAlgorithmParameterException
    {
      super();
    }
    
    SHA256(Element paramElement)
      throws MarshalException
    {
      super();
    }
    
    public String getAlgorithm()
    {
      return "http://www.w3.org/2001/04/xmlenc#sha256";
    }
    
    String getMessageDigestAlgorithm()
    {
      return "SHA-256";
    }
  }
  
  static final class SHA384
    extends DOMDigestMethod
  {
    SHA384(AlgorithmParameterSpec paramAlgorithmParameterSpec)
      throws InvalidAlgorithmParameterException
    {
      super();
    }
    
    SHA384(Element paramElement)
      throws MarshalException
    {
      super();
    }
    
    public String getAlgorithm()
    {
      return "http://www.w3.org/2001/04/xmldsig-more#sha384";
    }
    
    String getMessageDigestAlgorithm()
    {
      return "SHA-384";
    }
  }
  
  static final class SHA512
    extends DOMDigestMethod
  {
    SHA512(AlgorithmParameterSpec paramAlgorithmParameterSpec)
      throws InvalidAlgorithmParameterException
    {
      super();
    }
    
    SHA512(Element paramElement)
      throws MarshalException
    {
      super();
    }
    
    public String getAlgorithm()
    {
      return "http://www.w3.org/2001/04/xmlenc#sha512";
    }
    
    String getMessageDigestAlgorithm()
    {
      return "SHA-512";
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMDigestMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */