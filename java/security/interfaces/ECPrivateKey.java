package java.security.interfaces;

import java.math.BigInteger;
import java.security.PrivateKey;

public abstract interface ECPrivateKey
  extends PrivateKey, ECKey
{
  public static final long serialVersionUID = -7896394956925609184L;
  
  public abstract BigInteger getS();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\interfaces\ECPrivateKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */