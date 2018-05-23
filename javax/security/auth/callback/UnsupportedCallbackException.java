package javax.security.auth.callback;

public class UnsupportedCallbackException
  extends Exception
{
  private static final long serialVersionUID = -6873556327655666839L;
  private Callback callback;
  
  public UnsupportedCallbackException(Callback paramCallback)
  {
    callback = paramCallback;
  }
  
  public UnsupportedCallbackException(Callback paramCallback, String paramString)
  {
    super(paramString);
    callback = paramCallback;
  }
  
  public Callback getCallback()
  {
    return callback;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\auth\callback\UnsupportedCallbackException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */