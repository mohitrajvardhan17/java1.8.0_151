package sun.security.krb5.internal.crypto;

import sun.security.krb5.Confounder;

public class Nonce
{
  public Nonce() {}
  
  public static synchronized int value()
  {
    return Confounder.intValue() & 0x7FFFFFFF;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\crypto\Nonce.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */