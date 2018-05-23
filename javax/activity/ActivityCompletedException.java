package javax.activity;

import java.rmi.RemoteException;

public class ActivityCompletedException
  extends RemoteException
{
  public ActivityCompletedException() {}
  
  public ActivityCompletedException(String paramString)
  {
    super(paramString);
  }
  
  public ActivityCompletedException(Throwable paramThrowable)
  {
    this("", paramThrowable);
  }
  
  public ActivityCompletedException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\activity\ActivityCompletedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */