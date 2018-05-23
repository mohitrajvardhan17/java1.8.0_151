package java.security.interfaces;

import java.security.PublicKey;
import java.security.spec.ECPoint;

public abstract interface ECPublicKey
  extends PublicKey, ECKey
{
  public static final long serialVersionUID = -3314988629879632826L;
  
  public abstract ECPoint getW();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\interfaces\ECPublicKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */