package javax.management.remote;

import java.io.IOException;

public class JMXProviderException
  extends IOException
{
  private static final long serialVersionUID = -3166703627550447198L;
  private Throwable cause = null;
  
  public JMXProviderException() {}
  
  public JMXProviderException(String paramString)
  {
    super(paramString);
  }
  
  public JMXProviderException(String paramString, Throwable paramThrowable)
  {
    super(paramString);
    cause = paramThrowable;
  }
  
  public Throwable getCause()
  {
    return cause;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\remote\JMXProviderException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */