package sun.security.util;

import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.Timestamp;
import java.security.cert.X509Certificate;
import java.util.Date;

public class ConstraintsParameters
{
  private final String algorithm;
  private final AlgorithmParameters algParams;
  private final Key publicKey;
  private final X509Certificate cert;
  private final boolean trustedMatch;
  private final Date pkixDate;
  private final Timestamp jarTimestamp;
  private final String variant;
  
  public ConstraintsParameters(X509Certificate paramX509Certificate, boolean paramBoolean, Date paramDate, Timestamp paramTimestamp, String paramString)
  {
    cert = paramX509Certificate;
    trustedMatch = paramBoolean;
    pkixDate = paramDate;
    jarTimestamp = paramTimestamp;
    variant = (paramString == null ? "generic" : paramString);
    algorithm = null;
    algParams = null;
    publicKey = null;
  }
  
  public ConstraintsParameters(String paramString1, AlgorithmParameters paramAlgorithmParameters, Key paramKey, String paramString2)
  {
    algorithm = paramString1;
    algParams = paramAlgorithmParameters;
    publicKey = paramKey;
    cert = null;
    trustedMatch = false;
    pkixDate = null;
    jarTimestamp = null;
    variant = (paramString2 == null ? "generic" : paramString2);
  }
  
  public ConstraintsParameters(X509Certificate paramX509Certificate)
  {
    this(paramX509Certificate, false, null, null, "generic");
  }
  
  public ConstraintsParameters(Timestamp paramTimestamp)
  {
    this(null, false, null, paramTimestamp, "generic");
  }
  
  public String getAlgorithm()
  {
    return algorithm;
  }
  
  public AlgorithmParameters getAlgParams()
  {
    return algParams;
  }
  
  public Key getPublicKey()
  {
    return publicKey;
  }
  
  public boolean isTrustedMatch()
  {
    return trustedMatch;
  }
  
  public X509Certificate getCertificate()
  {
    return cert;
  }
  
  public Date getPKIXParamDate()
  {
    return pkixDate;
  }
  
  public Timestamp getJARTimestamp()
  {
    return jarTimestamp;
  }
  
  public String getVariant()
  {
    return variant;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\util\ConstraintsParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */