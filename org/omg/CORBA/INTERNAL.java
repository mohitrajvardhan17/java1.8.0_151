package org.omg.CORBA;

public final class INTERNAL
  extends SystemException
{
  public INTERNAL()
  {
    this("");
  }
  
  public INTERNAL(String paramString)
  {
    this(paramString, 0, CompletionStatus.COMPLETED_NO);
  }
  
  public INTERNAL(int paramInt, CompletionStatus paramCompletionStatus)
  {
    this("", paramInt, paramCompletionStatus);
  }
  
  public INTERNAL(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
  {
    super(paramString, paramInt, paramCompletionStatus);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\INTERNAL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */