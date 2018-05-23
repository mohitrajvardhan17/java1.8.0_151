package javax.security.auth.callback;

import java.io.IOException;

public abstract interface CallbackHandler
{
  public abstract void handle(Callback[] paramArrayOfCallback)
    throws IOException, UnsupportedCallbackException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\auth\callback\CallbackHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */