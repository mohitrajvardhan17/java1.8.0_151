package java.security.cert;

import java.security.PublicKey;

public class PKIXCertPathValidatorResult
  implements CertPathValidatorResult
{
  private TrustAnchor trustAnchor;
  private PolicyNode policyTree;
  private PublicKey subjectPublicKey;
  
  public PKIXCertPathValidatorResult(TrustAnchor paramTrustAnchor, PolicyNode paramPolicyNode, PublicKey paramPublicKey)
  {
    if (paramPublicKey == null) {
      throw new NullPointerException("subjectPublicKey must be non-null");
    }
    if (paramTrustAnchor == null) {
      throw new NullPointerException("trustAnchor must be non-null");
    }
    trustAnchor = paramTrustAnchor;
    policyTree = paramPolicyNode;
    subjectPublicKey = paramPublicKey;
  }
  
  public TrustAnchor getTrustAnchor()
  {
    return trustAnchor;
  }
  
  public PolicyNode getPolicyTree()
  {
    return policyTree;
  }
  
  public PublicKey getPublicKey()
  {
    return subjectPublicKey;
  }
  
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException.toString(), localCloneNotSupportedException);
    }
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("PKIXCertPathValidatorResult: [\n");
    localStringBuffer.append("  Trust Anchor: " + trustAnchor.toString() + "\n");
    localStringBuffer.append("  Policy Tree: " + String.valueOf(policyTree) + "\n");
    localStringBuffer.append("  Subject Public Key: " + subjectPublicKey + "\n");
    localStringBuffer.append("]");
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\cert\PKIXCertPathValidatorResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */