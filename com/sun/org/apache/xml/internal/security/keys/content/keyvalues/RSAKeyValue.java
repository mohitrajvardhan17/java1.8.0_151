package com.sun.org.apache.xml.internal.security.keys.content.keyvalues;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.I18n;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class RSAKeyValue
  extends SignatureElementProxy
  implements KeyValueContent
{
  public RSAKeyValue(Element paramElement, String paramString)
    throws XMLSecurityException
  {
    super(paramElement, paramString);
  }
  
  public RSAKeyValue(Document paramDocument, BigInteger paramBigInteger1, BigInteger paramBigInteger2)
  {
    super(paramDocument);
    XMLUtils.addReturnToElement(constructionElement);
    addBigIntegerElement(paramBigInteger1, "Modulus");
    addBigIntegerElement(paramBigInteger2, "Exponent");
  }
  
  public RSAKeyValue(Document paramDocument, Key paramKey)
    throws IllegalArgumentException
  {
    super(paramDocument);
    XMLUtils.addReturnToElement(constructionElement);
    if ((paramKey instanceof RSAPublicKey))
    {
      addBigIntegerElement(((RSAPublicKey)paramKey).getModulus(), "Modulus");
      addBigIntegerElement(((RSAPublicKey)paramKey).getPublicExponent(), "Exponent");
    }
    else
    {
      Object[] arrayOfObject = { "RSAKeyValue", paramKey.getClass().getName() };
      throw new IllegalArgumentException(I18n.translate("KeyValue.IllegalArgument", arrayOfObject));
    }
  }
  
  public PublicKey getPublicKey()
    throws XMLSecurityException
  {
    try
    {
      KeyFactory localKeyFactory = KeyFactory.getInstance("RSA");
      RSAPublicKeySpec localRSAPublicKeySpec = new RSAPublicKeySpec(getBigIntegerFromChildElement("Modulus", "http://www.w3.org/2000/09/xmldsig#"), getBigIntegerFromChildElement("Exponent", "http://www.w3.org/2000/09/xmldsig#"));
      PublicKey localPublicKey = localKeyFactory.generatePublic(localRSAPublicKeySpec);
      return localPublicKey;
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new XMLSecurityException("empty", localNoSuchAlgorithmException);
    }
    catch (InvalidKeySpecException localInvalidKeySpecException)
    {
      throw new XMLSecurityException("empty", localInvalidKeySpecException);
    }
  }
  
  public String getBaseLocalName()
  {
    return "RSAKeyValue";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\keys\content\keyvalues\RSAKeyValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */