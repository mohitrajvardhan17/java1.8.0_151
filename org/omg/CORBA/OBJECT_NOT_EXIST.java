package org.omg.CORBA;

public final class OBJECT_NOT_EXIST
  extends SystemException
{
  public OBJECT_NOT_EXIST()
  {
    this("");
  }
  
  public OBJECT_NOT_EXIST(String paramString)
  {
    this(paramString, 0, CompletionStatus.COMPLETED_NO);
  }
  
  public OBJECT_NOT_EXIST(int paramInt, CompletionStatus paramCompletionStatus)
  {
    this("", paramInt, paramCompletionStatus);
  }
  
  public OBJECT_NOT_EXIST(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
  {
    super(paramString, paramInt, paramCompletionStatus);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\OBJECT_NOT_EXIST.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */