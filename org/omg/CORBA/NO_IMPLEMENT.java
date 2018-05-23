package org.omg.CORBA;

public final class NO_IMPLEMENT
  extends SystemException
{
  public NO_IMPLEMENT()
  {
    this("");
  }
  
  public NO_IMPLEMENT(String paramString)
  {
    this(paramString, 0, CompletionStatus.COMPLETED_NO);
  }
  
  public NO_IMPLEMENT(int paramInt, CompletionStatus paramCompletionStatus)
  {
    this("", paramInt, paramCompletionStatus);
  }
  
  public NO_IMPLEMENT(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
  {
    super(paramString, paramInt, paramCompletionStatus);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\NO_IMPLEMENT.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */