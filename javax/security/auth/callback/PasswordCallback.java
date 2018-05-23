package javax.security.auth.callback;

import java.io.Serializable;

public class PasswordCallback
  implements Callback, Serializable
{
  private static final long serialVersionUID = 2267422647454909926L;
  private String prompt;
  private boolean echoOn;
  private char[] inputPassword;
  
  public PasswordCallback(String paramString, boolean paramBoolean)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      throw new IllegalArgumentException();
    }
    prompt = paramString;
    echoOn = paramBoolean;
  }
  
  public String getPrompt()
  {
    return prompt;
  }
  
  public boolean isEchoOn()
  {
    return echoOn;
  }
  
  public void setPassword(char[] paramArrayOfChar)
  {
    inputPassword = (paramArrayOfChar == null ? null : (char[])paramArrayOfChar.clone());
  }
  
  public char[] getPassword()
  {
    return inputPassword == null ? null : (char[])inputPassword.clone();
  }
  
  public void clearPassword()
  {
    if (inputPassword != null) {
      for (int i = 0; i < inputPassword.length; i++) {
        inputPassword[i] = ' ';
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\auth\callback\PasswordCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */