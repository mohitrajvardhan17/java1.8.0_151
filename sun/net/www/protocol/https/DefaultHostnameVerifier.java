package sun.net.www.protocol.https;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public final class DefaultHostnameVerifier
  implements HostnameVerifier
{
  public DefaultHostnameVerifier() {}
  
  public boolean verify(String paramString, SSLSession paramSSLSession)
  {
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\protocol\https\DefaultHostnameVerifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */