package com.sun.net.httpserver;

import javax.net.ssl.SSLContext;
import jdk.Exported;

@Exported
public class HttpsConfigurator
{
  private SSLContext context;
  
  public HttpsConfigurator(SSLContext paramSSLContext)
  {
    if (paramSSLContext == null) {
      throw new NullPointerException("null SSLContext");
    }
    context = paramSSLContext;
  }
  
  public SSLContext getSSLContext()
  {
    return context;
  }
  
  public void configure(HttpsParameters paramHttpsParameters)
  {
    paramHttpsParameters.setSSLParameters(getSSLContext().getDefaultSSLParameters());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\net\httpserver\HttpsConfigurator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */