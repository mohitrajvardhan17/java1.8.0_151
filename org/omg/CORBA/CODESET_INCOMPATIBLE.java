package org.omg.CORBA;

public final class CODESET_INCOMPATIBLE
  extends SystemException
{
  public CODESET_INCOMPATIBLE()
  {
    this("");
  }
  
  public CODESET_INCOMPATIBLE(String paramString)
  {
    this(paramString, 0, CompletionStatus.COMPLETED_NO);
  }
  
  public CODESET_INCOMPATIBLE(int paramInt, CompletionStatus paramCompletionStatus)
  {
    this("", paramInt, paramCompletionStatus);
  }
  
  public CODESET_INCOMPATIBLE(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
  {
    super(paramString, paramInt, paramCompletionStatus);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\CODESET_INCOMPATIBLE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */