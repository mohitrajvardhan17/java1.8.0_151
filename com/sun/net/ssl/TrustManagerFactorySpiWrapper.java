package com.sun.net.ssl;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

final class TrustManagerFactorySpiWrapper
  extends TrustManagerFactorySpi
{
  private TrustManagerFactory theTrustManagerFactory;
  
  TrustManagerFactorySpiWrapper(String paramString, Provider paramProvider)
    throws NoSuchAlgorithmException
  {
    theTrustManagerFactory = TrustManagerFactory.getInstance(paramString, paramProvider);
  }
  
  protected void engineInit(KeyStore paramKeyStore)
    throws KeyStoreException
  {
    theTrustManagerFactory.init(paramKeyStore);
  }
  
  protected TrustManager[] engineGetTrustManagers()
  {
    javax.net.ssl.TrustManager[] arrayOfTrustManager = theTrustManagerFactory.getTrustManagers();
    TrustManager[] arrayOfTrustManager1 = new TrustManager[arrayOfTrustManager.length];
    int j = 0;
    int i = 0;
    while (j < arrayOfTrustManager.length)
    {
      if (!(arrayOfTrustManager[j] instanceof TrustManager))
      {
        if ((arrayOfTrustManager[j] instanceof X509TrustManager))
        {
          arrayOfTrustManager1[i] = new X509TrustManagerComSunWrapper((X509TrustManager)arrayOfTrustManager[j]);
          i++;
        }
      }
      else
      {
        arrayOfTrustManager1[i] = ((TrustManager)arrayOfTrustManager[j]);
        i++;
      }
      j++;
    }
    if (i != j) {
      arrayOfTrustManager1 = (TrustManager[])SSLSecurity.truncateArray(arrayOfTrustManager1, new TrustManager[i]);
    }
    return arrayOfTrustManager1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\net\ssl\TrustManagerFactorySpiWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */