package org.omg.CORBA;

public final class REBIND
  extends SystemException
{
  public REBIND()
  {
    this("");
  }
  
  public REBIND(String paramString)
  {
    this(paramString, 0, CompletionStatus.COMPLETED_NO);
  }
  
  public REBIND(int paramInt, CompletionStatus paramCompletionStatus)
  {
    this("", paramInt, paramCompletionStatus);
  }
  
  public REBIND(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
  {
    super(paramString, paramInt, paramCompletionStatus);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\REBIND.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */