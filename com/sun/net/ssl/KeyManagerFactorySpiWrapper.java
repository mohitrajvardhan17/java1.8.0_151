package com.sun.net.ssl;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.UnrecoverableKeyException;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.X509KeyManager;

final class KeyManagerFactorySpiWrapper
  extends KeyManagerFactorySpi
{
  private KeyManagerFactory theKeyManagerFactory;
  
  KeyManagerFactorySpiWrapper(String paramString, Provider paramProvider)
    throws NoSuchAlgorithmException
  {
    theKeyManagerFactory = KeyManagerFactory.getInstance(paramString, paramProvider);
  }
  
  protected void engineInit(KeyStore paramKeyStore, char[] paramArrayOfChar)
    throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException
  {
    theKeyManagerFactory.init(paramKeyStore, paramArrayOfChar);
  }
  
  protected KeyManager[] engineGetKeyManagers()
  {
    javax.net.ssl.KeyManager[] arrayOfKeyManager = theKeyManagerFactory.getKeyManagers();
    KeyManager[] arrayOfKeyManager1 = new KeyManager[arrayOfKeyManager.length];
    int j = 0;
    int i = 0;
    while (j < arrayOfKeyManager.length)
    {
      if (!(arrayOfKeyManager[j] instanceof KeyManager))
      {
        if ((arrayOfKeyManager[j] instanceof X509KeyManager))
        {
          arrayOfKeyManager1[i] = new X509KeyManagerComSunWrapper((X509KeyManager)arrayOfKeyManager[j]);
          i++;
        }
      }
      else
      {
        arrayOfKeyManager1[i] = ((KeyManager)arrayOfKeyManager[j]);
        i++;
      }
      j++;
    }
    if (i != j) {
      arrayOfKeyManager1 = (KeyManager[])SSLSecurity.truncateArray(arrayOfKeyManager1, new KeyManager[i]);
    }
    return arrayOfKeyManager1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\net\ssl\KeyManagerFactorySpiWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */