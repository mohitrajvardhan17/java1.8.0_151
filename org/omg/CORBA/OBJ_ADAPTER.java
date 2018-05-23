package org.omg.CORBA;

public final class OBJ_ADAPTER
  extends SystemException
{
  public OBJ_ADAPTER()
  {
    this("");
  }
  
  public OBJ_ADAPTER(String paramString)
  {
    this(paramString, 0, CompletionStatus.COMPLETED_NO);
  }
  
  public OBJ_ADAPTER(int paramInt, CompletionStatus paramCompletionStatus)
  {
    this("", paramInt, paramCompletionStatus);
  }
  
  public OBJ_ADAPTER(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
  {
    super(paramString, paramInt, paramCompletionStatus);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\OBJ_ADAPTER.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */