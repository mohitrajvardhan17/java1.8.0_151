package org.omg.CORBA;

public final class NO_MEMORY
  extends SystemException
{
  public NO_MEMORY()
  {
    this("");
  }
  
  public NO_MEMORY(String paramString)
  {
    this(paramString, 0, CompletionStatus.COMPLETED_NO);
  }
  
  public NO_MEMORY(int paramInt, CompletionStatus paramCompletionStatus)
  {
    this("", paramInt, paramCompletionStatus);
  }
  
  public NO_MEMORY(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
  {
    super(paramString, paramInt, paramCompletionStatus);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\NO_MEMORY.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */