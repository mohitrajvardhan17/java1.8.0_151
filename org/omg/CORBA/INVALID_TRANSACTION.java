package org.omg.CORBA;

public final class INVALID_TRANSACTION
  extends SystemException
{
  public INVALID_TRANSACTION()
  {
    this("");
  }
  
  public INVALID_TRANSACTION(String paramString)
  {
    this(paramString, 0, CompletionStatus.COMPLETED_NO);
  }
  
  public INVALID_TRANSACTION(int paramInt, CompletionStatus paramCompletionStatus)
  {
    this("", paramInt, paramCompletionStatus);
  }
  
  public INVALID_TRANSACTION(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
  {
    super(paramString, paramInt, paramCompletionStatus);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\INVALID_TRANSACTION.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */