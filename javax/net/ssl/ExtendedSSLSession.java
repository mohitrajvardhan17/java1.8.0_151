package javax.net.ssl;

import java.util.List;

public abstract class ExtendedSSLSession
  implements SSLSession
{
  public ExtendedSSLSession() {}
  
  public abstract String[] getLocalSupportedSignatureAlgorithms();
  
  public abstract String[] getPeerSupportedSignatureAlgorithms();
  
  public List<SNIServerName> getRequestedServerNames()
  {
    throw new UnsupportedOperationException();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\net\ssl\ExtendedSSLSession.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */