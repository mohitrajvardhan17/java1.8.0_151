package javax.xml.crypto.dsig;

import java.security.spec.AlgorithmParameterSpec;
import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.XMLStructure;

public abstract interface SignatureMethod
  extends XMLStructure, AlgorithmMethod
{
  public static final String DSA_SHA1 = "http://www.w3.org/2000/09/xmldsig#dsa-sha1";
  public static final String RSA_SHA1 = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
  public static final String HMAC_SHA1 = "http://www.w3.org/2000/09/xmldsig#hmac-sha1";
  
  public abstract AlgorithmParameterSpec getParameterSpec();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\crypto\dsig\SignatureMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */