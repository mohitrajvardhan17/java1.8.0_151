package java.rmi;

import java.io.IOException;

public class RemoteException
  extends IOException
{
  private static final long serialVersionUID = -5148567311918794206L;
  public Throwable detail;
  
  public RemoteException()
  {
    initCause(null);
  }
  
  public RemoteException(String paramString)
  {
    super(paramString);
    initCause(null);
  }
  
  public RemoteException(String paramString, Throwable paramThrowable)
  {
    super(paramString);
    initCause(null);
    detail = paramThrowable;
  }
  
  public String getMessage()
  {
    if (detail == null) {
      return super.getMessage();
    }
    return super.getMessage() + "; nested exception is: \n\t" + detail.toString();
  }
  
  public Throwable getCause()
  {
    return detail;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\RemoteException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */