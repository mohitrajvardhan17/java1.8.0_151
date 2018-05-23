package com.sun.net.httpserver;

import java.net.InetSocketAddress;
import javax.net.ssl.SSLParameters;
import jdk.Exported;

@Exported
public abstract class HttpsParameters
{
  private String[] cipherSuites;
  private String[] protocols;
  private boolean wantClientAuth;
  private boolean needClientAuth;
  
  protected HttpsParameters() {}
  
  public abstract HttpsConfigurator getHttpsConfigurator();
  
  public abstract InetSocketAddress getClientAddress();
  
  public abstract void setSSLParameters(SSLParameters paramSSLParameters);
  
  public String[] getCipherSuites()
  {
    return cipherSuites != null ? (String[])cipherSuites.clone() : null;
  }
  
  public void setCipherSuites(String[] paramArrayOfString)
  {
    cipherSuites = (paramArrayOfString != null ? (String[])paramArrayOfString.clone() : null);
  }
  
  public String[] getProtocols()
  {
    return protocols != null ? (String[])protocols.clone() : null;
  }
  
  public void setProtocols(String[] paramArrayOfString)
  {
    protocols = (paramArrayOfString != null ? (String[])paramArrayOfString.clone() : null);
  }
  
  public boolean getWantClientAuth()
  {
    return wantClientAuth;
  }
  
  public void setWantClientAuth(boolean paramBoolean)
  {
    wantClientAuth = paramBoolean;
  }
  
  public boolean getNeedClientAuth()
  {
    return needClientAuth;
  }
  
  public void setNeedClientAuth(boolean paramBoolean)
  {
    needClientAuth = paramBoolean;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\net\httpserver\HttpsParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */