package org.omg.CORBA;

public final class UNKNOWN
  extends SystemException
{
  public UNKNOWN()
  {
    this("");
  }
  
  public UNKNOWN(String paramString)
  {
    this(paramString, 0, CompletionStatus.COMPLETED_NO);
  }
  
  public UNKNOWN(int paramInt, CompletionStatus paramCompletionStatus)
  {
    this("", paramInt, paramCompletionStatus);
  }
  
  public UNKNOWN(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
  {
    super(paramString, paramInt, paramCompletionStatus);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\UNKNOWN.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */