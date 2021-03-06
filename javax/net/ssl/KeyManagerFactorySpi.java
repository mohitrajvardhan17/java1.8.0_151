package javax.net.ssl;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

public abstract class KeyManagerFactorySpi
{
  public KeyManagerFactorySpi() {}
  
  protected abstract void engineInit(KeyStore paramKeyStore, char[] paramArrayOfChar)
    throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException;
  
  protected abstract void engineInit(ManagerFactoryParameters paramManagerFactoryParameters)
    throws InvalidAlgorithmParameterException;
  
  protected abstract KeyManager[] engineGetKeyManagers();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\net\ssl\KeyManagerFactorySpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */