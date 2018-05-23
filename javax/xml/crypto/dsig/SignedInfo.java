package javax.xml.crypto.dsig;

import java.io.InputStream;
import java.util.List;
import javax.xml.crypto.XMLStructure;

public abstract interface SignedInfo
  extends XMLStructure
{
  public abstract CanonicalizationMethod getCanonicalizationMethod();
  
  public abstract SignatureMethod getSignatureMethod();
  
  public abstract List getReferences();
  
  public abstract String getId();
  
  public abstract InputStream getCanonicalizedData();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\crypto\dsig\SignedInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */