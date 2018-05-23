package sun.net.www.protocol.http.ntlm;

import java.net.URL;

public abstract class NTLMAuthenticationCallback
{
  private static volatile NTLMAuthenticationCallback callback = new DefaultNTLMAuthenticationCallback();
  
  public NTLMAuthenticationCallback() {}
  
  public static void setNTLMAuthenticationCallback(NTLMAuthenticationCallback paramNTLMAuthenticationCallback)
  {
    callback = paramNTLMAuthenticationCallback;
  }
  
  public static NTLMAuthenticationCallback getNTLMAuthenticationCallback()
  {
    return callback;
  }
  
  public abstract boolean isTrustedSite(URL paramURL);
  
  static class DefaultNTLMAuthenticationCallback
    extends NTLMAuthenticationCallback
  {
    DefaultNTLMAuthenticationCallback() {}
    
    public boolean isTrustedSite(URL paramURL)
    {
      return true;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\protocol\http\ntlm\NTLMAuthenticationCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */