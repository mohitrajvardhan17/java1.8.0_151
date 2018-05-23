package javax.net.ssl;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public abstract interface X509KeyManager
  extends KeyManager
{
  public abstract String[] getClientAliases(String paramString, Principal[] paramArrayOfPrincipal);
  
  public abstract String chooseClientAlias(String[] paramArrayOfString, Principal[] paramArrayOfPrincipal, Socket paramSocket);
  
  public abstract String[] getServerAliases(String paramString, Principal[] paramArrayOfPrincipal);
  
  public abstract String chooseServerAlias(String paramString, Principal[] paramArrayOfPrincipal, Socket paramSocket);
  
  public abstract X509Certificate[] getCertificateChain(String paramString);
  
  public abstract PrivateKey getPrivateKey(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\net\ssl\X509KeyManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */