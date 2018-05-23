package javax.security.auth.callback;

import java.io.Serializable;

public class TextInputCallback
  implements Callback, Serializable
{
  private static final long serialVersionUID = -8064222478852811804L;
  private String prompt;
  private String defaultText;
  private String inputText;
  
  public TextInputCallback(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      throw new IllegalArgumentException();
    }
    prompt = paramString;
  }
  
  public TextInputCallback(String paramString1, String paramString2)
  {
    if ((paramString1 == null) || (paramString1.length() == 0) || (paramString2 == null) || (paramString2.length() == 0)) {
      throw new IllegalArgumentException();
    }
    prompt = paramString1;
    defaultText = paramString2;
  }
  
  public String getPrompt()
  {
    return prompt;
  }
  
  public String getDefaultText()
  {
    return defaultText;
  }
  
  public void setText(String paramString)
  {
    inputText = paramString;
  }
  
  public String getText()
  {
    return inputText;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\auth\callback\TextInputCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */