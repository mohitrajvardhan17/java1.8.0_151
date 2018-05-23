package javax.xml.crypto.dsig;

import java.util.List;
import javax.xml.crypto.XMLStructure;

public abstract interface SignatureProperty
  extends XMLStructure
{
  public abstract String getTarget();
  
  public abstract String getId();
  
  public abstract List getContent();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\crypto\dsig\SignatureProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */