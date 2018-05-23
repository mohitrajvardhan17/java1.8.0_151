package sun.misc;

import java.security.PermissionCollection;
import java.security.ProtectionDomain;

public abstract interface JavaSecurityProtectionDomainAccess
{
  public abstract ProtectionDomainCache getProtectionDomainCache();
  
  public abstract boolean getStaticPermissionsField(ProtectionDomain paramProtectionDomain);
  
  public static abstract interface ProtectionDomainCache
  {
    public abstract void put(ProtectionDomain paramProtectionDomain, PermissionCollection paramPermissionCollection);
    
    public abstract PermissionCollection get(ProtectionDomain paramProtectionDomain);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\JavaSecurityProtectionDomainAccess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */