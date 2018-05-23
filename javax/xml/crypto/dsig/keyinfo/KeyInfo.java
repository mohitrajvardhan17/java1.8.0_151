package javax.xml.crypto.dsig.keyinfo;

import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;

public abstract interface KeyInfo
  extends XMLStructure
{
  public abstract List getContent();
  
  public abstract String getId();
  
  public abstract void marshal(XMLStructure paramXMLStructure, XMLCryptoContext paramXMLCryptoContext)
    throws MarshalException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\crypto\dsig\keyinfo\KeyInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */