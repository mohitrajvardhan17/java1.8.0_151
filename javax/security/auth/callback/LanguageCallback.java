package javax.security.auth.callback;

import java.io.Serializable;
import java.util.Locale;

public class LanguageCallback
  implements Callback, Serializable
{
  private static final long serialVersionUID = 2019050433478903213L;
  private Locale locale;
  
  public LanguageCallback() {}
  
  public void setLocale(Locale paramLocale)
  {
    locale = paramLocale;
  }
  
  public Locale getLocale()
  {
    return locale;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\auth\callback\LanguageCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */