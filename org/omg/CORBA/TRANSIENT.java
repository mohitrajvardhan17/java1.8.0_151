package org.omg.CORBA;

public final class TRANSIENT
  extends SystemException
{
  public TRANSIENT()
  {
    this("");
  }
  
  public TRANSIENT(String paramString)
  {
    this(paramString, 0, CompletionStatus.COMPLETED_NO);
  }
  
  public TRANSIENT(int paramInt, CompletionStatus paramCompletionStatus)
  {
    this("", paramInt, paramCompletionStatus);
  }
  
  public TRANSIENT(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
  {
    super(paramString, paramInt, paramCompletionStatus);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\TRANSIENT.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */