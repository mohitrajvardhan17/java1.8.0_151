package sun.security.x509;

import java.io.IOException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class CertificatePolicyId
{
  private ObjectIdentifier id;
  
  public CertificatePolicyId(ObjectIdentifier paramObjectIdentifier)
  {
    id = paramObjectIdentifier;
  }
  
  public CertificatePolicyId(DerValue paramDerValue)
    throws IOException
  {
    id = paramDerValue.getOID();
  }
  
  public ObjectIdentifier getIdentifier()
  {
    return id;
  }
  
  public String toString()
  {
    String str = "CertificatePolicyId: [" + id.toString() + "]\n";
    return str;
  }
  
  public void encode(DerOutputStream paramDerOutputStream)
    throws IOException
  {
    paramDerOutputStream.putOID(id);
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof CertificatePolicyId)) {
      return id.equals(((CertificatePolicyId)paramObject).getIdentifier());
    }
    return false;
  }
  
  public int hashCode()
  {
    return id.hashCode();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\CertificatePolicyId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */