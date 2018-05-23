package com.sun.net.ssl.internal.www.protocol.https;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

public class Handler
  extends sun.net.www.protocol.https.Handler
{
  public Handler() {}
  
  public Handler(String paramString, int paramInt)
  {
    super(paramString, paramInt);
  }
  
  protected URLConnection openConnection(URL paramURL)
    throws IOException
  {
    return openConnection(paramURL, (Proxy)null);
  }
  
  protected URLConnection openConnection(URL paramURL, Proxy paramProxy)
    throws IOException
  {
    return new HttpsURLConnectionOldImpl(paramURL, paramProxy, this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\net\ssl\internal\www\protocol\https\Handler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */