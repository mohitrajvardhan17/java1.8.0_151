package javax.xml.crypto.dsig.keyinfo;

import java.util.List;
import javax.xml.crypto.XMLStructure;

public abstract interface X509Data
  extends XMLStructure
{
  public static final String TYPE = "http://www.w3.org/2000/09/xmldsig#X509Data";
  public static final String RAW_X509_CERTIFICATE_TYPE = "http://www.w3.org/2000/09/xmldsig#rawX509Certificate";
  
  public abstract List getContent();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\crypto\dsig\keyinfo\X509Data.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */