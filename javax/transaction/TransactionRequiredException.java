package javax.transaction;

import java.rmi.RemoteException;

public class TransactionRequiredException
  extends RemoteException
{
  public TransactionRequiredException() {}
  
  public TransactionRequiredException(String paramString)
  {
    super(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\transaction\TransactionRequiredException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */