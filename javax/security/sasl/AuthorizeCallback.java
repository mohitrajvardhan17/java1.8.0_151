package javax.security.sasl;

import java.io.Serializable;
import javax.security.auth.callback.Callback;

public class AuthorizeCallback
  implements Callback, Serializable
{
  private String authenticationID;
  private String authorizationID;
  private String authorizedID;
  private boolean authorized;
  private static final long serialVersionUID = -2353344186490470805L;
  
  public AuthorizeCallback(String paramString1, String paramString2)
  {
    authenticationID = paramString1;
    authorizationID = paramString2;
  }
  
  public String getAuthenticationID()
  {
    return authenticationID;
  }
  
  public String getAuthorizationID()
  {
    return authorizationID;
  }
  
  public boolean isAuthorized()
  {
    return authorized;
  }
  
  public void setAuthorized(boolean paramBoolean)
  {
    authorized = paramBoolean;
  }
  
  public String getAuthorizedID()
  {
    if (!authorized) {
      return null;
    }
    return authorizedID == null ? authorizationID : authorizedID;
  }
  
  public void setAuthorizedID(String paramString)
  {
    authorizedID = paramString;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\sasl\AuthorizeCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */