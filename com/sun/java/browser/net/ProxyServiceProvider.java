package com.sun.java.browser.net;

import java.net.URL;

public abstract interface ProxyServiceProvider
{
  public abstract ProxyInfo[] getProxyInfo(URL paramURL);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\browser\net\ProxyServiceProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */