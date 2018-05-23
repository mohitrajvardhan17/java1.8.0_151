package javax.security.auth.kerberos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.BasicPermission;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.StringTokenizer;

public final class DelegationPermission
  extends BasicPermission
  implements Serializable
{
  private static final long serialVersionUID = 883133252142523922L;
  private transient String subordinate;
  private transient String service;
  
  public DelegationPermission(String paramString)
  {
    super(paramString);
    init(paramString);
  }
  
  public DelegationPermission(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
    init(paramString1);
  }
  
  private void init(String paramString)
  {
    StringTokenizer localStringTokenizer = null;
    if (!paramString.startsWith("\"")) {
      throw new IllegalArgumentException("service principal [" + paramString + "] syntax invalid: improperly quoted");
    }
    localStringTokenizer = new StringTokenizer(paramString, "\"", false);
    subordinate = localStringTokenizer.nextToken();
    if (localStringTokenizer.countTokens() == 2)
    {
      localStringTokenizer.nextToken();
      service = localStringTokenizer.nextToken();
    }
    else if (localStringTokenizer.countTokens() > 0)
    {
      throw new IllegalArgumentException("service principal [" + localStringTokenizer.nextToken() + "] syntax invalid: improperly quoted");
    }
  }
  
  public boolean implies(Permission paramPermission)
  {
    if (!(paramPermission instanceof DelegationPermission)) {
      return false;
    }
    DelegationPermission localDelegationPermission = (DelegationPermission)paramPermission;
    return (subordinate.equals(subordinate)) && (service.equals(service));
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof DelegationPermission)) {
      return false;
    }
    DelegationPermission localDelegationPermission = (DelegationPermission)paramObject;
    return implies(localDelegationPermission);
  }
  
  public int hashCode()
  {
    return getName().hashCode();
  }
  
  public PermissionCollection newPermissionCollection()
  {
    return new KrbDelegationPermissionCollection();
  }
  
  private synchronized void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
  }
  
  private synchronized void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    init(getName());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\auth\kerberos\DelegationPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */