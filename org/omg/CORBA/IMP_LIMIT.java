package org.omg.CORBA;

public final class IMP_LIMIT
  extends SystemException
{
  public IMP_LIMIT()
  {
    this("");
  }
  
  public IMP_LIMIT(String paramString)
  {
    this(paramString, 0, CompletionStatus.COMPLETED_NO);
  }
  
  public IMP_LIMIT(int paramInt, CompletionStatus paramCompletionStatus)
  {
    this("", paramInt, paramCompletionStatus);
  }
  
  public IMP_LIMIT(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
  {
    super(paramString, paramInt, paramCompletionStatus);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\IMP_LIMIT.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */