package javax.security.auth.login;

import java.security.GeneralSecurityException;

public class LoginException
  extends GeneralSecurityException
{
  private static final long serialVersionUID = -4679091624035232488L;
  
  public LoginException() {}
  
  public LoginException(String paramString)
  {
    super(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\auth\login\LoginException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */