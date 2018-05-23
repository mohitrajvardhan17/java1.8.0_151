package com.sun.net.ssl;

import java.security.KeyStore;
import java.security.KeyStoreException;

@Deprecated
public abstract class TrustManagerFactorySpi
{
  public TrustManagerFactorySpi() {}
  
  protected abstract void engineInit(KeyStore paramKeyStore)
    throws KeyStoreException;
  
  protected abstract TrustManager[] engineGetTrustManagers();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\net\ssl\TrustManagerFactorySpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */