package java.security;

import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public abstract class KeyFactorySpi
{
  public KeyFactorySpi() {}
  
  protected abstract PublicKey engineGeneratePublic(KeySpec paramKeySpec)
    throws InvalidKeySpecException;
  
  protected abstract PrivateKey engineGeneratePrivate(KeySpec paramKeySpec)
    throws InvalidKeySpecException;
  
  protected abstract <T extends KeySpec> T engineGetKeySpec(Key paramKey, Class<T> paramClass)
    throws InvalidKeySpecException;
  
  protected abstract Key engineTranslateKey(Key paramKey)
    throws InvalidKeyException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\KeyFactorySpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */