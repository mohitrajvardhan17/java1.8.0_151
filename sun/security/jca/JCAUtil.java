package sun.security.jca;

import java.security.SecureRandom;

public final class JCAUtil
{
  private static final int ARRAY_SIZE = 4096;
  
  private JCAUtil() {}
  
  public static int getTempArraySize(int paramInt)
  {
    return Math.min(4096, paramInt);
  }
  
  public static SecureRandom getSecureRandom()
  {
    return CachedSecureRandomHolder.instance;
  }
  
  private static class CachedSecureRandomHolder
  {
    public static SecureRandom instance = new SecureRandom();
    
    private CachedSecureRandomHolder() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\jca\JCAUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */