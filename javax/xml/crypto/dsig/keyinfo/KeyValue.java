package javax.xml.crypto.dsig.keyinfo;

import java.security.KeyException;
import java.security.PublicKey;
import javax.xml.crypto.XMLStructure;

public abstract interface KeyValue
  extends XMLStructure
{
  public static final String DSA_TYPE = "http://www.w3.org/2000/09/xmldsig#DSAKeyValue";
  public static final String RSA_TYPE = "http://www.w3.org/2000/09/xmldsig#RSAKeyValue";
  
  public abstract PublicKey getPublicKey()
    throws KeyException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\crypto\dsig\keyinfo\KeyValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */