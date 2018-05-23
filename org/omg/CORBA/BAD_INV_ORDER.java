package org.omg.CORBA;

public final class BAD_INV_ORDER
  extends SystemException
{
  public BAD_INV_ORDER()
  {
    this("");
  }
  
  public BAD_INV_ORDER(String paramString)
  {
    this(paramString, 0, CompletionStatus.COMPLETED_NO);
  }
  
  public BAD_INV_ORDER(int paramInt, CompletionStatus paramCompletionStatus)
  {
    this("", paramInt, paramCompletionStatus);
  }
  
  public BAD_INV_ORDER(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
  {
    super(paramString, paramInt, paramCompletionStatus);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\BAD_INV_ORDER.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */