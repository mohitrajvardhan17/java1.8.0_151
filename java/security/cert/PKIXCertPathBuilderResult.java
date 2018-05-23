package java.security.cert;

import java.security.PublicKey;

public class PKIXCertPathBuilderResult
  extends PKIXCertPathValidatorResult
  implements CertPathBuilderResult
{
  private CertPath certPath;
  
  public PKIXCertPathBuilderResult(CertPath paramCertPath, TrustAnchor paramTrustAnchor, PolicyNode paramPolicyNode, PublicKey paramPublicKey)
  {
    super(paramTrustAnchor, paramPolicyNode, paramPublicKey);
    if (paramCertPath == null) {
      throw new NullPointerException("certPath must be non-null");
    }
    certPath = paramCertPath;
  }
  
  public CertPath getCertPath()
  {
    return certPath;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("PKIXCertPathBuilderResult: [\n");
    localStringBuffer.append("  Certification Path: " + certPath + "\n");
    localStringBuffer.append("  Trust Anchor: " + getTrustAnchor().toString() + "\n");
    localStringBuffer.append("  Policy Tree: " + String.valueOf(getPolicyTree()) + "\n");
    localStringBuffer.append("  Subject Public Key: " + getPublicKey() + "\n");
    localStringBuffer.append("]");
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\cert\PKIXCertPathBuilderResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */