package org.omg.CORBA;

public final class INV_IDENT
  extends SystemException
{
  public INV_IDENT()
  {
    this("");
  }
  
  public INV_IDENT(String paramString)
  {
    this(paramString, 0, CompletionStatus.COMPLETED_NO);
  }
  
  public INV_IDENT(int paramInt, CompletionStatus paramCompletionStatus)
  {
    this("", paramInt, paramCompletionStatus);
  }
  
  public INV_IDENT(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
  {
    super(paramString, paramInt, paramCompletionStatus);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\INV_IDENT.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */