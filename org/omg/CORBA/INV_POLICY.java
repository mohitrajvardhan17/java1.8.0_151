package org.omg.CORBA;

public final class INV_POLICY
  extends SystemException
{
  public INV_POLICY()
  {
    this("");
  }
  
  public INV_POLICY(String paramString)
  {
    this(paramString, 0, CompletionStatus.COMPLETED_NO);
  }
  
  public INV_POLICY(int paramInt, CompletionStatus paramCompletionStatus)
  {
    this("", paramInt, paramCompletionStatus);
  }
  
  public INV_POLICY(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
  {
    super(paramString, paramInt, paramCompletionStatus);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\INV_POLICY.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */