package com.sun.net.httpserver;

import java.security.Principal;
import jdk.Exported;

@Exported
public class HttpPrincipal
  implements Principal
{
  private String username;
  private String realm;
  
  public HttpPrincipal(String paramString1, String paramString2)
  {
    if ((paramString1 == null) || (paramString2 == null)) {
      throw new NullPointerException();
    }
    username = paramString1;
    realm = paramString2;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof HttpPrincipal)) {
      return false;
    }
    HttpPrincipal localHttpPrincipal = (HttpPrincipal)paramObject;
    return (username.equals(username)) && (realm.equals(realm));
  }
  
  public String getName()
  {
    return username;
  }
  
  public String getUsername()
  {
    return username;
  }
  
  public String getRealm()
  {
    return realm;
  }
  
  public int hashCode()
  {
    return (username + realm).hashCode();
  }
  
  public String toString()
  {
    return getName();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\net\httpserver\HttpPrincipal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */