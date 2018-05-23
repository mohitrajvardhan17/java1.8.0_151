package com.sun.org.apache.xml.internal.security.keys.content.x509;

import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.Signature11ElementProxy;
import java.security.MessageDigest;
import java.security.cert.X509Certificate;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLX509Digest
  extends Signature11ElementProxy
  implements XMLX509DataContent
{
  public XMLX509Digest(Element paramElement, String paramString)
    throws XMLSecurityException
  {
    super(paramElement, paramString);
  }
  
  public XMLX509Digest(Document paramDocument, byte[] paramArrayOfByte, String paramString)
  {
    super(paramDocument);
    addBase64Text(paramArrayOfByte);
    constructionElement.setAttributeNS(null, "Algorithm", paramString);
  }
  
  public XMLX509Digest(Document paramDocument, X509Certificate paramX509Certificate, String paramString)
    throws XMLSecurityException
  {
    super(paramDocument);
    addBase64Text(getDigestBytesFromCert(paramX509Certificate, paramString));
    constructionElement.setAttributeNS(null, "Algorithm", paramString);
  }
  
  public Attr getAlgorithmAttr()
  {
    return constructionElement.getAttributeNodeNS(null, "Algorithm");
  }
  
  public String getAlgorithm()
  {
    return getAlgorithmAttr().getNodeValue();
  }
  
  public byte[] getDigestBytes()
    throws XMLSecurityException
  {
    return getBytesFromTextChild();
  }
  
  public static byte[] getDigestBytesFromCert(X509Certificate paramX509Certificate, String paramString)
    throws XMLSecurityException
  {
    String str = JCEMapper.translateURItoJCEID(paramString);
    Object localObject;
    if (str == null)
    {
      localObject = new Object[] { paramString };
      throw new XMLSecurityException("XMLX509Digest.UnknownDigestAlgorithm", (Object[])localObject);
    }
    try
    {
      localObject = MessageDigest.getInstance(str);
      return ((MessageDigest)localObject).digest(paramX509Certificate.getEncoded());
    }
    catch (Exception localException)
    {
      Object[] arrayOfObject = { str };
      throw new XMLSecurityException("XMLX509Digest.FailedDigest", arrayOfObject);
    }
  }
  
  public String getBaseLocalName()
  {
    return "X509Digest";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\keys\content\x509\XMLX509Digest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */