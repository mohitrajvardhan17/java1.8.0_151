package org.omg.CORBA;

public final class ACTIVITY_REQUIRED
  extends SystemException
{
  public ACTIVITY_REQUIRED()
  {
    this("");
  }
  
  public ACTIVITY_REQUIRED(String paramString)
  {
    this(paramString, 0, CompletionStatus.COMPLETED_NO);
  }
  
  public ACTIVITY_REQUIRED(int paramInt, CompletionStatus paramCompletionStatus)
  {
    this("", paramInt, paramCompletionStatus);
  }
  
  public ACTIVITY_REQUIRED(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
  {
    super(paramString, paramInt, paramCompletionStatus);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\ACTIVITY_REQUIRED.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */