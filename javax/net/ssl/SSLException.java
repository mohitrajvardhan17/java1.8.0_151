package javax.net.ssl;

import java.io.IOException;

public class SSLException
  extends IOException
{
  private static final long serialVersionUID = 4511006460650708967L;
  
  public SSLException(String paramString)
  {
    super(paramString);
  }
  
  public SSLException(String paramString, Throwable paramThrowable)
  {
    super(paramString);
    initCause(paramThrowable);
  }
  
  public SSLException(Throwable paramThrowable)
  {
    super(paramThrowable == null ? null : paramThrowable.toString());
    initCause(paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\net\ssl\SSLException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */