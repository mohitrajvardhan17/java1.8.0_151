package com.sun.net.httpserver;

import java.util.Base64;
import java.util.Base64.Decoder;
import jdk.Exported;

@Exported
public abstract class BasicAuthenticator
  extends Authenticator
{
  protected String realm;
  
  public BasicAuthenticator(String paramString)
  {
    realm = paramString;
  }
  
  public String getRealm()
  {
    return realm;
  }
  
  public Authenticator.Result authenticate(HttpExchange paramHttpExchange)
  {
    Headers localHeaders1 = paramHttpExchange.getRequestHeaders();
    String str1 = localHeaders1.getFirst("Authorization");
    if (str1 == null)
    {
      Headers localHeaders2 = paramHttpExchange.getResponseHeaders();
      localHeaders2.set("WWW-Authenticate", "Basic realm=\"" + realm + "\"");
      return new Authenticator.Retry(401);
    }
    int i = str1.indexOf(' ');
    if ((i == -1) || (!str1.substring(0, i).equals("Basic"))) {
      return new Authenticator.Failure(401);
    }
    byte[] arrayOfByte = Base64.getDecoder().decode(str1.substring(i + 1));
    String str2 = new String(arrayOfByte);
    int j = str2.indexOf(':');
    String str3 = str2.substring(0, j);
    String str4 = str2.substring(j + 1);
    if (checkCredentials(str3, str4)) {
      return new Authenticator.Success(new HttpPrincipal(str3, realm));
    }
    Headers localHeaders3 = paramHttpExchange.getResponseHeaders();
    localHeaders3.set("WWW-Authenticate", "Basic realm=\"" + realm + "\"");
    return new Authenticator.Failure(401);
  }
  
  public abstract boolean checkCredentials(String paramString1, String paramString2);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\net\httpserver\BasicAuthenticator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */