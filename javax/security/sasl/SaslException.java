package javax.security.sasl;

import java.io.IOException;

public class SaslException
  extends IOException
{
  private Throwable _exception;
  private static final long serialVersionUID = 4579784287983423626L;
  
  public SaslException() {}
  
  public SaslException(String paramString)
  {
    super(paramString);
  }
  
  public SaslException(String paramString, Throwable paramThrowable)
  {
    super(paramString);
    if (paramThrowable != null) {
      initCause(paramThrowable);
    }
  }
  
  public Throwable getCause()
  {
    return _exception;
  }
  
  public Throwable initCause(Throwable paramThrowable)
  {
    super.initCause(paramThrowable);
    _exception = paramThrowable;
    return this;
  }
  
  public String toString()
  {
    String str = super.toString();
    if ((_exception != null) && (_exception != this)) {
      str = str + " [Caused by " + _exception.toString() + "]";
    }
    return str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\sasl\SaslException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */