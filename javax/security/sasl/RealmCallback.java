package javax.security.sasl;

import javax.security.auth.callback.TextInputCallback;

public class RealmCallback
  extends TextInputCallback
{
  private static final long serialVersionUID = -4342673378785456908L;
  
  public RealmCallback(String paramString)
  {
    super(paramString);
  }
  
  public RealmCallback(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\sasl\RealmCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */