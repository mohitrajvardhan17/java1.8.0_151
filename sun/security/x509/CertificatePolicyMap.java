package sun.security.x509;

import java.io.IOException;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class CertificatePolicyMap
{
  private CertificatePolicyId issuerDomain;
  private CertificatePolicyId subjectDomain;
  
  public CertificatePolicyMap(CertificatePolicyId paramCertificatePolicyId1, CertificatePolicyId paramCertificatePolicyId2)
  {
    issuerDomain = paramCertificatePolicyId1;
    subjectDomain = paramCertificatePolicyId2;
  }
  
  public CertificatePolicyMap(DerValue paramDerValue)
    throws IOException
  {
    if (tag != 48) {
      throw new IOException("Invalid encoding for CertificatePolicyMap");
    }
    issuerDomain = new CertificatePolicyId(data.getDerValue());
    subjectDomain = new CertificatePolicyId(data.getDerValue());
  }
  
  public CertificatePolicyId getIssuerIdentifier()
  {
    return issuerDomain;
  }
  
  public CertificatePolicyId getSubjectIdentifier()
  {
    return subjectDomain;
  }
  
  public String toString()
  {
    String str = "CertificatePolicyMap: [\nIssuerDomain:" + issuerDomain.toString() + "SubjectDomain:" + subjectDomain.toString() + "]\n";
    return str;
  }
  
  public void encode(DerOutputStream paramDerOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    issuerDomain.encode(localDerOutputStream);
    subjectDomain.encode(localDerOutputStream);
    paramDerOutputStream.write((byte)48, localDerOutputStream);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\CertificatePolicyMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */