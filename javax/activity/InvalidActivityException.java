package javax.activity;

import java.rmi.RemoteException;

public class InvalidActivityException
  extends RemoteException
{
  public InvalidActivityException() {}
  
  public InvalidActivityException(String paramString)
  {
    super(paramString);
  }
  
  public InvalidActivityException(Throwable paramThrowable)
  {
    this("", paramThrowable);
  }
  
  public InvalidActivityException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\activity\InvalidActivityException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */