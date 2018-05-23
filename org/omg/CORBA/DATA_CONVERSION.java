package org.omg.CORBA;

public final class DATA_CONVERSION
  extends SystemException
{
  public DATA_CONVERSION()
  {
    this("");
  }
  
  public DATA_CONVERSION(String paramString)
  {
    this(paramString, 0, CompletionStatus.COMPLETED_NO);
  }
  
  public DATA_CONVERSION(int paramInt, CompletionStatus paramCompletionStatus)
  {
    this("", paramInt, paramCompletionStatus);
  }
  
  public DATA_CONVERSION(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
  {
    super(paramString, paramInt, paramCompletionStatus);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\DATA_CONVERSION.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */