package com.sun.org.apache.xml.internal.security.keys.content.x509;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import java.io.ByteArrayInputStream;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLX509Certificate
  extends SignatureElementProxy
  implements XMLX509DataContent
{
  public static final String JCA_CERT_ID = "X.509";
  
  public XMLX509Certificate(Element paramElement, String paramString)
    throws XMLSecurityException
  {
    super(paramElement, paramString);
  }
  
  public XMLX509Certificate(Document paramDocument, byte[] paramArrayOfByte)
  {
    super(paramDocument);
    addBase64Text(paramArrayOfByte);
  }
  
  public XMLX509Certificate(Document paramDocument, X509Certificate paramX509Certificate)
    throws XMLSecurityException
  {
    super(paramDocument);
    try
    {
      addBase64Text(paramX509Certificate.getEncoded());
    }
    catch (CertificateEncodingException localCertificateEncodingException)
    {
      throw new XMLSecurityException("empty", localCertificateEncodingException);
    }
  }
  
  public byte[] getCertificateBytes()
    throws XMLSecurityException
  {
    return getBytesFromTextChild();
  }
  
  public X509Certificate getX509Certificate()
    throws XMLSecurityException
  {
    try
    {
      byte[] arrayOfByte = getCertificateBytes();
      CertificateFactory localCertificateFactory = CertificateFactory.getInstance("X.509");
      X509Certificate localX509Certificate = (X509Certificate)localCertificateFactory.generateCertificate(new ByteArrayInputStream(arrayOfByte));
      if (localX509Certificate != null) {
        return localX509Certificate;
      }
      return null;
    }
    catch (CertificateException localCertificateException)
    {
      throw new XMLSecurityException("empty", localCertificateException);
    }
  }
  
  public PublicKey getPublicKey()
    throws XMLSecurityException
  {
    X509Certificate localX509Certificate = getX509Certificate();
    if (localX509Certificate != null) {
      return localX509Certificate.getPublicKey();
    }
    return null;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof XMLX509Certificate)) {
      return false;
    }
    XMLX509Certificate localXMLX509Certificate = (XMLX509Certificate)paramObject;
    try
    {
      return Arrays.equals(localXMLX509Certificate.getCertificateBytes(), getCertificateBytes());
    }
    catch (XMLSecurityException localXMLSecurityException) {}
    return false;
  }
  
  public int hashCode()
  {
    int i = 17;
    try
    {
      byte[] arrayOfByte = getCertificateBytes();
      for (int j = 0; j < arrayOfByte.length; j++) {
        i = 31 * i + arrayOfByte[j];
      }
    }
    catch (XMLSecurityException localXMLSecurityException)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, localXMLSecurityException.getMessage(), localXMLSecurityException);
      }
    }
    return i;
  }
  
  public String getBaseLocalName()
  {
    return "X509Certificate";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\keys\content\x509\XMLX509Certificate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */