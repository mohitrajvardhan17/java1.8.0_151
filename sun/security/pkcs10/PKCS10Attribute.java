package sun.security.pkcs10;

import java.io.IOException;
import java.io.OutputStream;
import sun.security.pkcs.PKCS9Attribute;
import sun.security.util.DerEncoder;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class PKCS10Attribute
  implements DerEncoder
{
  protected ObjectIdentifier attributeId = null;
  protected Object attributeValue = null;
  
  public PKCS10Attribute(DerValue paramDerValue)
    throws IOException
  {
    PKCS9Attribute localPKCS9Attribute = new PKCS9Attribute(paramDerValue);
    attributeId = localPKCS9Attribute.getOID();
    attributeValue = localPKCS9Attribute.getValue();
  }
  
  public PKCS10Attribute(ObjectIdentifier paramObjectIdentifier, Object paramObject)
  {
    attributeId = paramObjectIdentifier;
    attributeValue = paramObject;
  }
  
  public PKCS10Attribute(PKCS9Attribute paramPKCS9Attribute)
  {
    attributeId = paramPKCS9Attribute.getOID();
    attributeValue = paramPKCS9Attribute.getValue();
  }
  
  public void derEncode(OutputStream paramOutputStream)
    throws IOException
  {
    PKCS9Attribute localPKCS9Attribute = new PKCS9Attribute(attributeId, attributeValue);
    localPKCS9Attribute.derEncode(paramOutputStream);
  }
  
  public ObjectIdentifier getAttributeId()
  {
    return attributeId;
  }
  
  public Object getAttributeValue()
  {
    return attributeValue;
  }
  
  public String toString()
  {
    return attributeValue.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\pkcs10\PKCS10Attribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */