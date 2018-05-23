package sun.security.provider;

import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.Vector;

class PolicyPermissions
  extends PermissionCollection
{
  private static final long serialVersionUID = -1954188373270545523L;
  private CodeSource codesource;
  private Permissions perms;
  private AuthPolicyFile policy;
  private boolean notInit;
  private Vector<Permission> additionalPerms;
  
  PolicyPermissions(AuthPolicyFile paramAuthPolicyFile, CodeSource paramCodeSource)
  {
    codesource = paramCodeSource;
    policy = paramAuthPolicyFile;
    perms = null;
    notInit = true;
    additionalPerms = null;
  }
  
  public void add(Permission paramPermission)
  {
    if (isReadOnly()) {
      throw new SecurityException(AuthPolicyFile.rb.getString("attempt.to.add.a.Permission.to.a.readonly.PermissionCollection"));
    }
    if (perms == null)
    {
      if (additionalPerms == null) {
        additionalPerms = new Vector();
      }
      additionalPerms.add(paramPermission);
    }
    else
    {
      perms.add(paramPermission);
    }
  }
  
  private synchronized void init()
  {
    if (notInit)
    {
      if (perms == null) {
        perms = new Permissions();
      }
      if (additionalPerms != null)
      {
        Enumeration localEnumeration = additionalPerms.elements();
        while (localEnumeration.hasMoreElements()) {
          perms.add((Permission)localEnumeration.nextElement());
        }
        additionalPerms = null;
      }
      policy.getPermissions(perms, codesource);
      notInit = false;
    }
  }
  
  public boolean implies(Permission paramPermission)
  {
    if (notInit) {
      init();
    }
    return perms.implies(paramPermission);
  }
  
  public Enumeration<Permission> elements()
  {
    if (notInit) {
      init();
    }
    return perms.elements();
  }
  
  public String toString()
  {
    if (notInit) {
      init();
    }
    return perms.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\PolicyPermissions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */