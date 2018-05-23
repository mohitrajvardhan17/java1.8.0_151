package sun.security.krb5;

import java.security.SecureRandom;

public final class Confounder
{
  private static SecureRandom srand = new SecureRandom();
  
  private Confounder() {}
  
  public static byte[] bytes(int paramInt)
  {
    byte[] arrayOfByte = new byte[paramInt];
    srand.nextBytes(arrayOfByte);
    return arrayOfByte;
  }
  
  public static int intValue()
  {
    return srand.nextInt();
  }
  
  public static long longValue()
  {
    return srand.nextLong();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\Confounder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */