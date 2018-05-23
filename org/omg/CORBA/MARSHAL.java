package org.omg.CORBA;

public final class MARSHAL
  extends SystemException
{
  public MARSHAL()
  {
    this("");
  }
  
  public MARSHAL(String paramString)
  {
    this(paramString, 0, CompletionStatus.COMPLETED_NO);
  }
  
  public MARSHAL(int paramInt, CompletionStatus paramCompletionStatus)
  {
    this("", paramInt, paramCompletionStatus);
  }
  
  public MARSHAL(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
  {
    super(paramString, paramInt, paramCompletionStatus);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\MARSHAL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */