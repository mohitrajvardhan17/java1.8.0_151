package javax.xml.crypto.dsig.keyinfo;

import java.util.List;
import javax.xml.crypto.XMLStructure;

public abstract interface PGPData
  extends XMLStructure
{
  public static final String TYPE = "http://www.w3.org/2000/09/xmldsig#PGPData";
  
  public abstract byte[] getKeyId();
  
  public abstract byte[] getKeyPacket();
  
  public abstract List getExternalElements();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\crypto\dsig\keyinfo\PGPData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */