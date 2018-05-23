package java.security;

public class AccessControlException
  extends SecurityException
{
  private static final long serialVersionUID = 5138225684096988535L;
  private Permission perm;
  
  public AccessControlException(String paramString)
  {
    super(paramString);
  }
  
  public AccessControlException(String paramString, Permission paramPermission)
  {
    super(paramString);
    perm = paramPermission;
  }
  
  public Permission getPermission()
  {
    return perm;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\AccessControlException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */