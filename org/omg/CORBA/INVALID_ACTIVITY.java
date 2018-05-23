package org.omg.CORBA;

public final class INVALID_ACTIVITY
  extends SystemException
{
  public INVALID_ACTIVITY()
  {
    this("");
  }
  
  public INVALID_ACTIVITY(String paramString)
  {
    this(paramString, 0, CompletionStatus.COMPLETED_NO);
  }
  
  public INVALID_ACTIVITY(int paramInt, CompletionStatus paramCompletionStatus)
  {
    this("", paramInt, paramCompletionStatus);
  }
  
  public INVALID_ACTIVITY(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
  {
    super(paramString, paramInt, paramCompletionStatus);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\INVALID_ACTIVITY.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */