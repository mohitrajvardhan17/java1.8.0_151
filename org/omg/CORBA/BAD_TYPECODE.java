package org.omg.CORBA;

public final class BAD_TYPECODE
  extends SystemException
{
  public BAD_TYPECODE()
  {
    this("");
  }
  
  public BAD_TYPECODE(String paramString)
  {
    this(paramString, 0, CompletionStatus.COMPLETED_NO);
  }
  
  public BAD_TYPECODE(int paramInt, CompletionStatus paramCompletionStatus)
  {
    this("", paramInt, paramCompletionStatus);
  }
  
  public BAD_TYPECODE(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
  {
    super(paramString, paramInt, paramCompletionStatus);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\BAD_TYPECODE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */