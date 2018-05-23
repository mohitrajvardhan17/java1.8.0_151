package com.sun.net.httpserver;

import javax.net.ssl.SSLSession;
import jdk.Exported;

@Exported
public abstract class HttpsExchange
  extends HttpExchange
{
  protected HttpsExchange() {}
  
  public abstract SSLSession getSSLSession();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\net\httpserver\HttpsExchange.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */