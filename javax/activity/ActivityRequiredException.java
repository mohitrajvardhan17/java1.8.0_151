package javax.activity;

import java.rmi.RemoteException;

public class ActivityRequiredException
  extends RemoteException
{
  public ActivityRequiredException() {}
  
  public ActivityRequiredException(String paramString)
  {
    super(paramString);
  }
  
  public ActivityRequiredException(Throwable paramThrowable)
  {
    this("", paramThrowable);
  }
  
  public ActivityRequiredException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\activity\ActivityRequiredException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */