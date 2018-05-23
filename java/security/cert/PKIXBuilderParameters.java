package java.security.cert;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Set;

public class PKIXBuilderParameters
  extends PKIXParameters
{
  private int maxPathLength = 5;
  
  public PKIXBuilderParameters(Set<TrustAnchor> paramSet, CertSelector paramCertSelector)
    throws InvalidAlgorithmParameterException
  {
    super(paramSet);
    setTargetCertConstraints(paramCertSelector);
  }
  
  public PKIXBuilderParameters(KeyStore paramKeyStore, CertSelector paramCertSelector)
    throws KeyStoreException, InvalidAlgorithmParameterException
  {
    super(paramKeyStore);
    setTargetCertConstraints(paramCertSelector);
  }
  
  public void setMaxPathLength(int paramInt)
  {
    if (paramInt < -1) {
      throw new InvalidParameterException("the maximum path length parameter can not be less than -1");
    }
    maxPathLength = paramInt;
  }
  
  public int getMaxPathLength()
  {
    return maxPathLength;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("[\n");
    localStringBuffer.append(super.toString());
    localStringBuffer.append("  Maximum Path Length: " + maxPathLength + "\n");
    localStringBuffer.append("]\n");
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\cert\PKIXBuilderParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */