package com.sun.jmx.snmp.IPAcl;

import java.io.Serializable;
import java.security.acl.Permission;

class PermissionImpl
  implements Permission, Serializable
{
  private static final long serialVersionUID = 4478110422746916589L;
  private String perm = null;
  
  public PermissionImpl(String paramString)
  {
    perm = paramString;
  }
  
  public int hashCode()
  {
    return super.hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof PermissionImpl)) {
      return perm.equals(((PermissionImpl)paramObject).getString());
    }
    return false;
  }
  
  public String toString()
  {
    return perm;
  }
  
  public String getString()
  {
    return perm;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\IPAcl\PermissionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */