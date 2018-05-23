package com.sun.org.apache.xml.internal.security.keys.content.x509;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLX509SKI
  extends SignatureElementProxy
  implements XMLX509DataContent
{
  private static Logger log = Logger.getLogger(XMLX509SKI.class.getName());
  public static final String SKI_OID = "2.5.29.14";
  
  public XMLX509SKI(Document paramDocument, byte[] paramArrayOfByte)
  {
    super(paramDocument);
    addBase64Text(paramArrayOfByte);
  }
  
  public XMLX509SKI(Document paramDocument, X509Certificate paramX509Certificate)
    throws XMLSecurityException
  {
    super(paramDocument);
    addBase64Text(getSKIBytesFromCert(paramX509Certificate));
  }
  
  public XMLX509SKI(Element paramElement, String paramString)
    throws XMLSecurityException
  {
    super(paramElement, paramString);
  }
  
  public byte[] getSKIBytes()
    throws XMLSecurityException
  {
    return getBytesFromTextChild();
  }
  
  public static byte[] getSKIBytesFromCert(X509Certificate paramX509Certificate)
    throws XMLSecurityException
  {
    if (paramX509Certificate.getVersion() < 3)
    {
      localObject = new Object[] { Integer.valueOf(paramX509Certificate.getVersion()) };
      throw new XMLSecurityException("certificate.noSki.lowVersion", (Object[])localObject);
    }
    Object localObject = paramX509Certificate.getExtensionValue("2.5.29.14");
    if (localObject == null) {
      throw new XMLSecurityException("certificate.noSki.null");
    }
    byte[] arrayOfByte = new byte[localObject.length - 4];
    System.arraycopy(localObject, 4, arrayOfByte, 0, arrayOfByte.length);
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Base64 of SKI is " + Base64.encode(arrayOfByte));
    }
    return arrayOfByte;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof XMLX509SKI)) {
      return false;
    }
    XMLX509SKI localXMLX509SKI = (XMLX509SKI)paramObject;
    try
    {
      return Arrays.equals(localXMLX509SKI.getSKIBytes(), getSKIBytes());
    }
    catch (XMLSecurityException localXMLSecurityException) {}
    return false;
  }
  
  public int hashCode()
  {
    int i = 17;
    try
    {
      byte[] arrayOfByte = getSKIBytes();
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
    return "X509SKI";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\keys\content\x509\XMLX509SKI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */