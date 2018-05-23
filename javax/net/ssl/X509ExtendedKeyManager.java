package javax.net.ssl;

import java.security.Principal;

public abstract class X509ExtendedKeyManager
  implements X509KeyManager
{
  protected X509ExtendedKeyManager() {}
  
  public String chooseEngineClientAlias(String[] paramArrayOfString, Principal[] paramArrayOfPrincipal, SSLEngine paramSSLEngine)
  {
    return null;
  }
  
  public String chooseEngineServerAlias(String paramString, Principal[] paramArrayOfPrincipal, SSLEngine paramSSLEngine)
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\net\ssl\X509ExtendedKeyManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */