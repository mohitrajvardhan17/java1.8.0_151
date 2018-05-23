package javax.transaction;

import java.rmi.RemoteException;

public class TransactionRolledbackException
  extends RemoteException
{
  public TransactionRolledbackException() {}
  
  public TransactionRolledbackException(String paramString)
  {
    super(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\transaction\TransactionRolledbackException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */