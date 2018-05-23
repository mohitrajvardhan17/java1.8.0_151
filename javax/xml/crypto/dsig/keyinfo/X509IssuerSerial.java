package javax.xml.crypto.dsig.keyinfo;

import java.math.BigInteger;
import javax.xml.crypto.XMLStructure;

public abstract interface X509IssuerSerial
  extends XMLStructure
{
  public abstract String getIssuerName();
  
  public abstract BigInteger getSerialNumber();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\crypto\dsig\keyinfo\X509IssuerSerial.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */