package javax.xml.crypto.dsig;

import java.util.List;
import javax.xml.crypto.XMLStructure;

public abstract interface XMLObject
  extends XMLStructure
{
  public static final String TYPE = "http://www.w3.org/2000/09/xmldsig#Object";
  
  public abstract List getContent();
  
  public abstract String getId();
  
  public abstract String getMimeType();
  
  public abstract String getEncoding();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\crypto\dsig\XMLObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */