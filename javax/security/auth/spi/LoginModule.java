package javax.security.auth.spi;

import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;

public abstract interface LoginModule
{
  public abstract void initialize(Subject paramSubject, CallbackHandler paramCallbackHandler, Map<String, ?> paramMap1, Map<String, ?> paramMap2);
  
  public abstract boolean login()
    throws LoginException;
  
  public abstract boolean commit()
    throws LoginException;
  
  public abstract boolean abort()
    throws LoginException;
  
  public abstract boolean logout()
    throws LoginException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\auth\spi\LoginModule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */