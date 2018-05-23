package sun.security.util;

import java.security.AlgorithmParameters;
import java.security.CryptoPrimitive;
import java.security.Key;
import java.util.Set;

public class LegacyAlgorithmConstraints
  extends AbstractAlgorithmConstraints
{
  public static final String PROPERTY_TLS_LEGACY_ALGS = "jdk.tls.legacyAlgorithms";
  private final String[] legacyAlgorithms;
  
  public LegacyAlgorithmConstraints(String paramString, AlgorithmDecomposer paramAlgorithmDecomposer)
  {
    super(paramAlgorithmDecomposer);
    legacyAlgorithms = getAlgorithms(paramString);
  }
  
  public final boolean permits(Set<CryptoPrimitive> paramSet, String paramString, AlgorithmParameters paramAlgorithmParameters)
  {
    return checkAlgorithm(legacyAlgorithms, paramString, decomposer);
  }
  
  public final boolean permits(Set<CryptoPrimitive> paramSet, Key paramKey)
  {
    return true;
  }
  
  public final boolean permits(Set<CryptoPrimitive> paramSet, String paramString, Key paramKey, AlgorithmParameters paramAlgorithmParameters)
  {
    return checkAlgorithm(legacyAlgorithms, paramString, decomposer);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\util\LegacyAlgorithmConstraints.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */