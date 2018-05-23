package org.omg.CORBA;

public final class INITIALIZE
  extends SystemException
{
  public INITIALIZE()
  {
    this("");
  }
  
  public INITIALIZE(String paramString)
  {
    this(paramString, 0, CompletionStatus.COMPLETED_NO);
  }
  
  public INITIALIZE(int paramInt, CompletionStatus paramCompletionStatus)
  {
    this("", paramInt, paramCompletionStatus);
  }
  
  public INITIALIZE(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
  {
    super(paramString, paramInt, paramCompletionStatus);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\INITIALIZE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */