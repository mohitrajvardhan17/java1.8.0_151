package java.net;

import java.io.InterruptedIOException;

public class SocketTimeoutException
  extends InterruptedIOException
{
  private static final long serialVersionUID = -8846654841826352300L;
  
  public SocketTimeoutException(String paramString)
  {
    super(paramString);
  }
  
  public SocketTimeoutException() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\SocketTimeoutException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */