package com.sun.jmx.snmp.IPAcl;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.security.Principal;
import java.security.acl.AclEntry;
import java.security.acl.Permission;
import java.util.Enumeration;
import java.util.Vector;

class AclEntryImpl
  implements AclEntry, Serializable
{
  private static final long serialVersionUID = -5047185131260073216L;
  private Principal princ = null;
  private boolean neg = false;
  private Vector<Permission> permList = null;
  private Vector<String> commList = null;
  
  private AclEntryImpl(AclEntryImpl paramAclEntryImpl)
    throws UnknownHostException
  {
    setPrincipal(paramAclEntryImpl.getPrincipal());
    permList = new Vector();
    commList = new Vector();
    Enumeration localEnumeration = paramAclEntryImpl.communities();
    while (localEnumeration.hasMoreElements()) {
      addCommunity((String)localEnumeration.nextElement());
    }
    localEnumeration = paramAclEntryImpl.permissions();
    while (localEnumeration.hasMoreElements()) {
      addPermission((Permission)localEnumeration.nextElement());
    }
    if (paramAclEntryImpl.isNegative()) {
      setNegativePermissions();
    }
  }
  
  public AclEntryImpl()
  {
    princ = null;
    permList = new Vector();
    commList = new Vector();
  }
  
  public AclEntryImpl(Principal paramPrincipal)
    throws UnknownHostException
  {
    princ = paramPrincipal;
    permList = new Vector();
    commList = new Vector();
  }
  
  public Object clone()
  {
    AclEntryImpl localAclEntryImpl;
    try
    {
      localAclEntryImpl = new AclEntryImpl(this);
    }
    catch (UnknownHostException localUnknownHostException)
    {
      localAclEntryImpl = null;
    }
    return localAclEntryImpl;
  }
  
  public boolean isNegative()
  {
    return neg;
  }
  
  public boolean addPermission(Permission paramPermission)
  {
    if (permList.contains(paramPermission)) {
      return false;
    }
    permList.addElement(paramPermission);
    return true;
  }
  
  public boolean removePermission(Permission paramPermission)
  {
    if (!permList.contains(paramPermission)) {
      return false;
    }
    permList.removeElement(paramPermission);
    return true;
  }
  
  public boolean checkPermission(Permission paramPermission)
  {
    return permList.contains(paramPermission);
  }
  
  public Enumeration<Permission> permissions()
  {
    return permList.elements();
  }
  
  public void setNegativePermissions()
  {
    neg = true;
  }
  
  public Principal getPrincipal()
  {
    return princ;
  }
  
  public boolean setPrincipal(Principal paramPrincipal)
  {
    if (princ != null) {
      return false;
    }
    princ = paramPrincipal;
    return true;
  }
  
  public String toString()
  {
    return "AclEntry:" + princ.toString();
  }
  
  public Enumeration<String> communities()
  {
    return commList.elements();
  }
  
  public boolean addCommunity(String paramString)
  {
    if (commList.contains(paramString)) {
      return false;
    }
    commList.addElement(paramString);
    return true;
  }
  
  public boolean removeCommunity(String paramString)
  {
    if (!commList.contains(paramString)) {
      return false;
    }
    commList.removeElement(paramString);
    return true;
  }
  
  public boolean checkCommunity(String paramString)
  {
    return commList.contains(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\IPAcl\AclEntryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */