package java.security;

import java.util.Set;

public abstract interface AlgorithmConstraints
{
  public abstract boolean permits(Set<CryptoPrimitive> paramSet, String paramString, AlgorithmParameters paramAlgorithmParameters);
  
  public abstract boolean permits(Set<CryptoPrimitive> paramSet, Key paramKey);
  
  public abstract boolean permits(Set<CryptoPrimitive> paramSet, String paramString, Key paramKey, AlgorithmParameters paramAlgorithmParameters);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\AlgorithmConstraints.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */