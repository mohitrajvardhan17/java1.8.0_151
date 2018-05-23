package java.security.interfaces;

import java.security.InvalidParameterException;
import java.security.SecureRandom;

public abstract interface DSAKeyPairGenerator
{
  public abstract void initialize(DSAParams paramDSAParams, SecureRandom paramSecureRandom)
    throws InvalidParameterException;
  
  public abstract void initialize(int paramInt, boolean paramBoolean, SecureRandom paramSecureRandom)
    throws InvalidParameterException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\interfaces\DSAKeyPairGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */