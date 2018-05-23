package java.security.interfaces;

import java.math.BigInteger;
import java.security.PublicKey;

public abstract interface RSAPublicKey
  extends PublicKey, RSAKey
{
  public static final long serialVersionUID = -8727434096241101194L;
  
  public abstract BigInteger getPublicExponent();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\interfaces\RSAPublicKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */