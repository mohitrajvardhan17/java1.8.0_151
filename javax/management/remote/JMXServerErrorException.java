package javax.management.remote;

import java.io.IOException;

public class JMXServerErrorException
  extends IOException
{
  private static final long serialVersionUID = 3996732239558744666L;
  private final Error cause;
  
  public JMXServerErrorException(String paramString, Error paramError)
  {
    super(paramString);
    cause = paramError;
  }
  
  public Throwable getCause()
  {
    return cause;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\remote\JMXServerErrorException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */