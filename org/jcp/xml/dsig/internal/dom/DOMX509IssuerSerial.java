package org.jcp.xml.dsig.internal.dom;

import java.math.BigInteger;
import javax.security.auth.x500.X500Principal;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.keyinfo.X509IssuerSerial;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DOMX509IssuerSerial
  extends DOMStructure
  implements X509IssuerSerial
{
  private final String issuerName;
  private final BigInteger serialNumber;
  
  public DOMX509IssuerSerial(String paramString, BigInteger paramBigInteger)
  {
    if (paramString == null) {
      throw new NullPointerException("issuerName cannot be null");
    }
    if (paramBigInteger == null) {
      throw new NullPointerException("serialNumber cannot be null");
    }
    new X500Principal(paramString);
    issuerName = paramString;
    serialNumber = paramBigInteger;
  }
  
  public DOMX509IssuerSerial(Element paramElement)
    throws MarshalException
  {
    Element localElement1 = DOMUtils.getFirstChildElement(paramElement, "X509IssuerName");
    Element localElement2 = DOMUtils.getNextSiblingElement(localElement1, "X509SerialNumber");
    issuerName = localElement1.getFirstChild().getNodeValue();
    serialNumber = new BigInteger(localElement2.getFirstChild().getNodeValue());
  }
  
  public String getIssuerName()
  {
    return issuerName;
  }
  
  public BigInteger getSerialNumber()
  {
    return serialNumber;
  }
  
  public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext)
    throws MarshalException
  {
    Document localDocument = DOMUtils.getOwnerDocument(paramNode);
    Element localElement1 = DOMUtils.createElement(localDocument, "X509IssuerSerial", "http://www.w3.org/2000/09/xmldsig#", paramString);
    Element localElement2 = DOMUtils.createElement(localDocument, "X509IssuerName", "http://www.w3.org/2000/09/xmldsig#", paramString);
    Element localElement3 = DOMUtils.createElement(localDocument, "X509SerialNumber", "http://www.w3.org/2000/09/xmldsig#", paramString);
    localElement2.appendChild(localDocument.createTextNode(issuerName));
    localElement3.appendChild(localDocument.createTextNode(serialNumber.toString()));
    localElement1.appendChild(localElement2);
    localElement1.appendChild(localElement3);
    paramNode.appendChild(localElement1);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof X509IssuerSerial)) {
      return false;
    }
    X509IssuerSerial localX509IssuerSerial = (X509IssuerSerial)paramObject;
    return (issuerName.equals(localX509IssuerSerial.getIssuerName())) && (serialNumber.equals(localX509IssuerSerial.getSerialNumber()));
  }
  
  public int hashCode()
  {
    int i = 17;
    i = 31 * i + issuerName.hashCode();
    i = 31 * i + serialNumber.hashCode();
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMX509IssuerSerial.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */