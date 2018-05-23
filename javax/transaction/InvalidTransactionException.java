package javax.transaction;

import java.rmi.RemoteException;

public class InvalidTransactionException
  extends RemoteException
{
  public InvalidTransactionException() {}
  
  public InvalidTransactionException(String paramString)
  {
    super(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\transaction\InvalidTransactionException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */