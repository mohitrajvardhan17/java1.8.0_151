package com.sun.net.ssl;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLEngine;

final class X509KeyManagerJavaxWrapper
  implements javax.net.ssl.X509KeyManager
{
  private X509KeyManager theX509KeyManager;
  
  X509KeyManagerJavaxWrapper(X509KeyManager paramX509KeyManager)
  {
    theX509KeyManager = paramX509KeyManager;
  }
  
  public String[] getClientAliases(String paramString, Principal[] paramArrayOfPrincipal)
  {
    return theX509KeyManager.getClientAliases(paramString, paramArrayOfPrincipal);
  }
  
  public String chooseClientAlias(String[] paramArrayOfString, Principal[] paramArrayOfPrincipal, Socket paramSocket)
  {
    if (paramArrayOfString == null) {
      return null;
    }
    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      String str;
      if ((str = theX509KeyManager.chooseClientAlias(paramArrayOfString[i], paramArrayOfPrincipal)) != null) {
        return str;
      }
    }
    return null;
  }
  
  public String chooseEngineClientAlias(String[] paramArrayOfString, Principal[] paramArrayOfPrincipal, SSLEngine paramSSLEngine)
  {
    if (paramArrayOfString == null) {
      return null;
    }
    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      String str;
      if ((str = theX509KeyManager.chooseClientAlias(paramArrayOfString[i], paramArrayOfPrincipal)) != null) {
        return str;
      }
    }
    return null;
  }
  
  public String[] getServerAliases(String paramString, Principal[] paramArrayOfPrincipal)
  {
    return theX509KeyManager.getServerAliases(paramString, paramArrayOfPrincipal);
  }
  
  public String chooseServerAlias(String paramString, Principal[] paramArrayOfPrincipal, Socket paramSocket)
  {
    if (paramString == null) {
      return null;
    }
    return theX509KeyManager.chooseServerAlias(paramString, paramArrayOfPrincipal);
  }
  
  public String chooseEngineServerAlias(String paramString, Principal[] paramArrayOfPrincipal, SSLEngine paramSSLEngine)
  {
    if (paramString == null) {
      return null;
    }
    return theX509KeyManager.chooseServerAlias(paramString, paramArrayOfPrincipal);
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\net\ssl\X509KeyManagerJavaxWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */