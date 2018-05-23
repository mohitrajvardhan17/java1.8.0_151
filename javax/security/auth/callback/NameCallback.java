package javax.security.auth.callback;

import java.io.Serializable;

public class NameCallback
  implements Callback, Serializable
{
  private static final long serialVersionUID = 3770938795909392253L;
  private String prompt;
  private String defaultName;
  private String inputName;
  
  public NameCallback(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      throw new IllegalArgumentException();
    }
    prompt = paramString;
  }
  
  public NameCallback(String paramString1, String paramString2)
  {
    if ((paramString1 == null) || (paramString1.length() == 0) || (paramString2 == null) || (paramString2.length() == 0)) {
      throw new IllegalArgumentException();
    }
    prompt = paramString1;
    defaultName = paramString2;
  }
  
  public String getPrompt()
  {
    return prompt;
  }
  
  public String getDefaultName()
  {
    return defaultName;
  }
  
  public void setName(String paramString)
  {
    inputName = paramString;
  }
  
  public String getName()
  {
    return inputName;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\auth\callback\NameCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */