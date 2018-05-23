package com.sun.net.ssl;

import java.security.cert.X509Certificate;

@Deprecated
public abstract interface X509TrustManager
  extends TrustManager
{
  public abstract boolean isClientTrusted(X509Certificate[] paramArrayOfX509Certificate);
  
  public abstract boolean isServerTrusted(X509Certificate[] paramArrayOfX509Certificate);
  
  public abstract X509Certificate[] getAcceptedIssuers();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\net\ssl\X509TrustManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */