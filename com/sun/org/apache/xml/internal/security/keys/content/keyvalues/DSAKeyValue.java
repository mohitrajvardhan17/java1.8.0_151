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
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DSAKeyValue
  extends SignatureElementProxy
  implements KeyValueContent
{
  public DSAKeyValue(Element paramElement, String paramString)
    throws XMLSecurityException
  {
    super(paramElement, paramString);
  }
  
  public DSAKeyValue(Document paramDocument, BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4)
  {
    super(paramDocument);
    XMLUtils.addReturnToElement(constructionElement);
    addBigIntegerElement(paramBigInteger1, "P");
    addBigIntegerElement(paramBigInteger2, "Q");
    addBigIntegerElement(paramBigInteger3, "G");
    addBigIntegerElement(paramBigInteger4, "Y");
  }
  
  public DSAKeyValue(Document paramDocument, Key paramKey)
    throws IllegalArgumentException
  {
    super(paramDocument);
    XMLUtils.addReturnToElement(constructionElement);
    if ((paramKey instanceof DSAPublicKey))
    {
      addBigIntegerElement(((DSAPublicKey)paramKey).getParams().getP(), "P");
      addBigIntegerElement(((DSAPublicKey)paramKey).getParams().getQ(), "Q");
      addBigIntegerElement(((DSAPublicKey)paramKey).getParams().getG(), "G");
      addBigIntegerElement(((DSAPublicKey)paramKey).getY(), "Y");
    }
    else
    {
      Object[] arrayOfObject = { "DSAKeyValue", paramKey.getClass().getName() };
      throw new IllegalArgumentException(I18n.translate("KeyValue.IllegalArgument", arrayOfObject));
    }
  }
  
  public PublicKey getPublicKey()
    throws XMLSecurityException
  {
    try
    {
      DSAPublicKeySpec localDSAPublicKeySpec = new DSAPublicKeySpec(getBigIntegerFromChildElement("Y", "http://www.w3.org/2000/09/xmldsig#"), getBigIntegerFromChildElement("P", "http://www.w3.org/2000/09/xmldsig#"), getBigIntegerFromChildElement("Q", "http://www.w3.org/2000/09/xmldsig#"), getBigIntegerFromChildElement("G", "http://www.w3.org/2000/09/xmldsig#"));
      KeyFactory localKeyFactory = KeyFactory.getInstance("DSA");
      PublicKey localPublicKey = localKeyFactory.generatePublic(localDSAPublicKeySpec);
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
    return "DSAKeyValue";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\keys\content\keyvalues\DSAKeyValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */