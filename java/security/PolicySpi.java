package java.security;

public abstract class PolicySpi
{
  public PolicySpi() {}
  
  protected abstract boolean engineImplies(ProtectionDomain paramProtectionDomain, Permission paramPermission);
  
  protected void engineRefresh() {}
  
  protected PermissionCollection engineGetPermissions(CodeSource paramCodeSource)
  {
    return Policy.UNSUPPORTED_EMPTY_COLLECTION;
  }
  
  protected PermissionCollection engineGetPermissions(ProtectionDomain paramProtectionDomain)
  {
    return Policy.UNSUPPORTED_EMPTY_COLLECTION;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\PolicySpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */