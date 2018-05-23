package javax.xml.crypto.dsig;

import java.util.List;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;

public abstract interface XMLSignature
  extends XMLStructure
{
  public static final String XMLNS = "http://www.w3.org/2000/09/xmldsig#";
  
  public abstract boolean validate(XMLValidateContext paramXMLValidateContext)
    throws XMLSignatureException;
  
  public abstract KeyInfo getKeyInfo();
  
  public abstract SignedInfo getSignedInfo();
  
  public abstract List getObjects();
  
  public abstract String getId();
  
  public abstract SignatureValue getSignatureValue();
  
  public abstract void sign(XMLSignContext paramXMLSignContext)
    throws MarshalException, XMLSignatureException;
  
  public abstract KeySelectorResult getKeySelectorResult();
  
  public static abstract interface SignatureValue
    extends XMLStructure
  {
    public abstract String getId();
    
    public abstract byte[] getValue();
    
    public abstract boolean validate(XMLValidateContext paramXMLValidateContext)
      throws XMLSignatureException;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\crypto\dsig\XMLSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */