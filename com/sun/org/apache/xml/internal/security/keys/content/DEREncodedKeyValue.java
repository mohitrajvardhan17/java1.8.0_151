package com.sun.org.apache.xml.internal.security.keys.content;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.Signature11ElementProxy;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DEREncodedKeyValue
  extends Signature11ElementProxy
  implements KeyInfoContent
{
  private static final String[] supportedKeyTypes = { "RSA", "DSA", "EC" };
  
  public DEREncodedKeyValue(Element paramElement, String paramString)
    throws XMLSecurityException
  {
    super(paramElement, paramString);
  }
  
  public DEREncodedKeyValue(Document paramDocument, PublicKey paramPublicKey)
    throws XMLSecurityException
  {
    super(paramDocument);
    addBase64Text(getEncodedDER(paramPublicKey));
  }
  
  public DEREncodedKeyValue(Document paramDocument, byte[] paramArrayOfByte)
  {
    super(paramDocument);
    addBase64Text(paramArrayOfByte);
  }
  
  public void setId(String paramString)
  {
    if (paramString != null)
    {
      constructionElement.setAttributeNS(null, "Id", paramString);
      constructionElement.setIdAttributeNS(null, "Id", true);
    }
    else
    {
      constructionElement.removeAttributeNS(null, "Id");
    }
  }
  
  public String getId()
  {
    return constructionElement.getAttributeNS(null, "Id");
  }
  
  public String getBaseLocalName()
  {
    return "DEREncodedKeyValue";
  }
  
  public PublicKey getPublicKey()
    throws XMLSecurityException
  {
    byte[] arrayOfByte = getBytesFromTextChild();
    for (String str : supportedKeyTypes) {
      try
      {
        KeyFactory localKeyFactory = KeyFactory.getInstance(str);
        X509EncodedKeySpec localX509EncodedKeySpec = new X509EncodedKeySpec(arrayOfByte);
        PublicKey localPublicKey = localKeyFactory.generatePublic(localX509EncodedKeySpec);
        if (localPublicKey != null) {
          return localPublicKey;
        }
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {}catch (InvalidKeySpecException localInvalidKeySpecException) {}
    }
    throw new XMLSecurityException("DEREncodedKeyValue.UnsupportedEncodedKey");
  }
  
  protected byte[] getEncodedDER(PublicKey paramPublicKey)
    throws XMLSecurityException
  {
    try
    {
      KeyFactory localKeyFactory = KeyFactory.getInstance(paramPublicKey.getAlgorithm());
      localObject = (X509EncodedKeySpec)localKeyFactory.getKeySpec(paramPublicKey, X509EncodedKeySpec.class);
      return ((X509EncodedKeySpec)localObject).getEncoded();
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      localObject = new Object[] { paramPublicKey.getAlgorithm(), paramPublicKey.getFormat(), paramPublicKey.getClass().getName() };
      throw new XMLSecurityException("DEREncodedKeyValue.UnsupportedPublicKey", (Object[])localObject, localNoSuchAlgorithmException);
    }
    catch (InvalidKeySpecException localInvalidKeySpecException)
    {
      Object localObject = { paramPublicKey.getAlgorithm(), paramPublicKey.getFormat(), paramPublicKey.getClass().getName() };
      throw new XMLSecurityException("DEREncodedKeyValue.UnsupportedPublicKey", (Object[])localObject, localInvalidKeySpecException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\keys\content\DEREncodedKeyValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */