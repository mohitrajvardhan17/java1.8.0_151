package org.omg.CORBA;

public final class WrongTransaction
  extends UserException
{
  public WrongTransaction()
  {
    super(WrongTransactionHelper.id());
  }
  
  public WrongTransaction(String paramString)
  {
    super(WrongTransactionHelper.id() + "  " + paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\WrongTransaction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */