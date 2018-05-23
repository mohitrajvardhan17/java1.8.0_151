package java.security;

import java.io.Serializable;

public abstract class SecureRandomSpi
  implements Serializable
{
  private static final long serialVersionUID = -2991854161009191830L;
  
  public SecureRandomSpi() {}
  
  protected abstract void engineSetSeed(byte[] paramArrayOfByte);
  
  protected abstract void engineNextBytes(byte[] paramArrayOfByte);
  
  protected abstract byte[] engineGenerateSeed(int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\SecureRandomSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */