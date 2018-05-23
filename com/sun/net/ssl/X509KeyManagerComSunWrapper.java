package com.sun.net.ssl;

import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

final class X509KeyManagerComSunWrapper
  implements X509KeyManager
{
  private javax.net.ssl.X509KeyManager theX509KeyManager;
  
  X509KeyManagerComSunWrapper(javax.net.ssl.X509KeyManager paramX509KeyManager)
  {
    theX509KeyManager = paramX509KeyManager;
  }
  
  public String[] getClientAliases(String paramString, Principal[] paramArrayOfPrincipal)
  {
    return theX509KeyManager.getClientAliases(paramString, paramArrayOfPrincipal);
  }
  
  public String chooseClientAlias(String paramString, Principal[] paramArrayOfPrincipal)
  {
    String[] arrayOfString = { paramString };
    return theX509KeyManager.chooseClientAlias(arrayOfString, paramArrayOfPrincipal, null);
  }
  
  public String[] getServerAliases(String paramString, Principal[] paramArrayOfPrincipal)
  {
    return theX509KeyManager.getServerAliases(paramString, paramArrayOfPrincipal);
  }
  
  public String chooseServerAlias(String paramString, Principal[] paramArrayOfPrincipal)
  {
    return theX509KeyManager.chooseServerAlias(paramString, paramArrayOfPrincipal, null);
  }
  
  public X509Certificate[] getCertificateChain(String paramString)
  {
    return theX509KeyManager.getCertificateChain(paramString);
  }
  
  public PrivateKey getPrivateKey(String paramString)
  {
    return theX509KeyManager.getPrivateKey(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\net\ssl\X509KeyManagerComSunWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */