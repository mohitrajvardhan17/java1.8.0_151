package sun.net.www.protocol.http.spnego;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Arrays;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import sun.net.www.protocol.http.HttpCallerInfo;

public class NegotiateCallbackHandler
  implements CallbackHandler
{
  private String username;
  private char[] password;
  private boolean answered;
  private final HttpCallerInfo hci;
  
  public NegotiateCallbackHandler(HttpCallerInfo paramHttpCallerInfo)
  {
    hci = paramHttpCallerInfo;
  }
  
  private void getAnswer()
  {
    if (!answered)
    {
      answered = true;
      PasswordAuthentication localPasswordAuthentication = Authenticator.requestPasswordAuthentication(hci.host, hci.addr, hci.port, hci.protocol, hci.prompt, hci.scheme, hci.url, hci.authType);
      if (localPasswordAuthentication != null)
      {
        username = localPasswordAuthentication.getUserName();
        password = localPasswordAuthentication.getPassword();
      }
    }
  }
  
  public void handle(Callback[] paramArrayOfCallback)
    throws UnsupportedCallbackException, IOException
  {
    for (int i = 0; i < paramArrayOfCallback.length; i++)
    {
      Callback localCallback = paramArrayOfCallback[i];
      if ((localCallback instanceof NameCallback))
      {
        getAnswer();
        ((NameCallback)localCallback).setName(username);
      }
      else if ((localCallback instanceof PasswordCallback))
      {
        getAnswer();
        ((PasswordCallback)localCallback).setPassword(password);
        if (password != null) {
          Arrays.fill(password, ' ');
        }
      }
      else
      {
        throw new UnsupportedCallbackException(localCallback, "Call back not supported");
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\protocol\http\spnego\NegotiateCallbackHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */