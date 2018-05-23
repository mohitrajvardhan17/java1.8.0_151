package org.omg.CORBA;

public final class INTF_REPOS
  extends SystemException
{
  public INTF_REPOS()
  {
    this("");
  }
  
  public INTF_REPOS(String paramString)
  {
    this(paramString, 0, CompletionStatus.COMPLETED_NO);
  }
  
  public INTF_REPOS(int paramInt, CompletionStatus paramCompletionStatus)
  {
    this("", paramInt, paramCompletionStatus);
  }
  
  public INTF_REPOS(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
  {
    super(paramString, paramInt, paramCompletionStatus);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\INTF_REPOS.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */