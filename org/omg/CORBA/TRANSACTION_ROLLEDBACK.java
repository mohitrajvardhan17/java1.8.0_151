package org.omg.CORBA;

public final class TRANSACTION_ROLLEDBACK
  extends SystemException
{
  public TRANSACTION_ROLLEDBACK()
  {
    this("");
  }
  
  public TRANSACTION_ROLLEDBACK(String paramString)
  {
    this(paramString, 0, CompletionStatus.COMPLETED_NO);
  }
  
  public TRANSACTION_ROLLEDBACK(int paramInt, CompletionStatus paramCompletionStatus)
  {
    this("", paramInt, paramCompletionStatus);
  }
  
  public TRANSACTION_ROLLEDBACK(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
  {
    super(paramString, paramInt, paramCompletionStatus);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\TRANSACTION_ROLLEDBACK.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */