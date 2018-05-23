package sun.security.x509;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class CertificatePolicySet
{
  private final Vector<CertificatePolicyId> ids;
  
  public CertificatePolicySet(Vector<CertificatePolicyId> paramVector)
  {
    ids = paramVector;
  }
  
  public CertificatePolicySet(DerInputStream paramDerInputStream)
    throws IOException
  {
    ids = new Vector();
    DerValue[] arrayOfDerValue = paramDerInputStream.getSequence(5);
    for (int i = 0; i < arrayOfDerValue.length; i++)
    {
      CertificatePolicyId localCertificatePolicyId = new CertificatePolicyId(arrayOfDerValue[i]);
      ids.addElement(localCertificatePolicyId);
    }
  }
  
  public String toString()
  {
    String str = "CertificatePolicySet:[\n" + ids.toString() + "]\n";
    return str;
  }
  
  public void encode(DerOutputStream paramDerOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    for (int i = 0; i < ids.size(); i++) {
      ((CertificatePolicyId)ids.elementAt(i)).encode(localDerOutputStream);
    }
    paramDerOutputStream.write((byte)48, localDerOutputStream);
  }
  
  public List<CertificatePolicyId> getCertPolicyIds()
  {
    return Collections.unmodifiableList(ids);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\CertificatePolicySet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */