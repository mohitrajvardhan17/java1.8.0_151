package sun.net.www.http;

import java.net.URL;

class KeepAliveKey
{
  private String protocol = null;
  private String host = null;
  private int port = 0;
  private Object obj = null;
  
  public KeepAliveKey(URL paramURL, Object paramObject)
  {
    protocol = paramURL.getProtocol();
    host = paramURL.getHost();
    port = paramURL.getPort();
    obj = paramObject;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof KeepAliveKey)) {
      return false;
    }
    KeepAliveKey localKeepAliveKey = (KeepAliveKey)paramObject;
    return (host.equals(host)) && (port == port) && (protocol.equals(protocol)) && (obj == obj);
  }
  
  public int hashCode()
  {
    String str = protocol + host + port;
    return obj == null ? str.hashCode() : str.hashCode() + obj.hashCode();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\http\KeepAliveKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */