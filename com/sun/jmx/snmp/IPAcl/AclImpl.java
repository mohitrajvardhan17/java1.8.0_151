package com.sun.jmx.snmp.IPAcl;

import java.io.Serializable;
import java.security.Principal;
import java.security.acl.Acl;
import java.security.acl.AclEntry;
import java.security.acl.NotOwnerException;
import java.security.acl.Permission;
import java.util.Enumeration;
import java.util.Vector;

class AclImpl
  extends OwnerImpl
  implements Acl, Serializable
{
  private static final long serialVersionUID = -2250957591085270029L;
  private Vector<AclEntry> entryList = null;
  private String aclName = null;
  
  public AclImpl(PrincipalImpl paramPrincipalImpl, String paramString)
  {
    super(paramPrincipalImpl);
    aclName = paramString;
  }
  
  public void setName(Principal paramPrincipal, String paramString)
    throws NotOwnerException
  {
    if (!isOwner(paramPrincipal)) {
      throw new NotOwnerException();
    }
    aclName = paramString;
  }
  
  public String getName()
  {
    return aclName;
  }
  
  public boolean addEntry(Principal paramPrincipal, AclEntry paramAclEntry)
    throws NotOwnerException
  {
    if (!isOwner(paramPrincipal)) {
      throw new NotOwnerException();
    }
    if (entryList.contains(paramAclEntry)) {
      return false;
    }
    entryList.addElement(paramAclEntry);
    return true;
  }
  
  public boolean removeEntry(Principal paramPrincipal, AclEntry paramAclEntry)
    throws NotOwnerException
  {
    if (!isOwner(paramPrincipal)) {
      throw new NotOwnerException();
    }
    return entryList.removeElement(paramAclEntry);
  }
  
  public void removeAll(Principal paramPrincipal)
    throws NotOwnerException
  {
    if (!isOwner(paramPrincipal)) {
      throw new NotOwnerException();
    }
    entryList.removeAllElements();
  }
  
  public Enumeration<Permission> getPermissions(Principal paramPrincipal)
  {
    Vector localVector = new Vector();
    Enumeration localEnumeration = entryList.elements();
    while (localEnumeration.hasMoreElements())
    {
      AclEntry localAclEntry = (AclEntry)localEnumeration.nextElement();
      if (localAclEntry.getPrincipal().equals(paramPrincipal)) {
        return localAclEntry.permissions();
      }
    }
    return localVector.elements();
  }
  
  public Enumeration<AclEntry> entries()
  {
    return entryList.elements();
  }
  
  public boolean checkPermission(Principal paramPrincipal, Permission paramPermission)
  {
    Enumeration localEnumeration = entryList.elements();
    while (localEnumeration.hasMoreElements())
    {
      AclEntry localAclEntry = (AclEntry)localEnumeration.nextElement();
      if ((localAclEntry.getPrincipal().equals(paramPrincipal)) && (localAclEntry.checkPermission(paramPermission))) {
        return true;
      }
    }
    return false;
  }
  
  public boolean checkPermission(Principal paramPrincipal, String paramString, Permission paramPermission)
  {
    Enumeration localEnumeration = entryList.elements();
    while (localEnumeration.hasMoreElements())
    {
      AclEntryImpl localAclEntryImpl = (AclEntryImpl)localEnumeration.nextElement();
      if ((localAclEntryImpl.getPrincipal().equals(paramPrincipal)) && (localAclEntryImpl.checkPermission(paramPermission)) && (localAclEntryImpl.checkCommunity(paramString))) {
        return true;
      }
    }
    return false;
  }
  
  public boolean checkCommunity(String paramString)
  {
    Enumeration localEnumeration = entryList.elements();
    while (localEnumeration.hasMoreElements())
    {
      AclEntryImpl localAclEntryImpl = (AclEntryImpl)localEnumeration.nextElement();
      if (localAclEntryImpl.checkCommunity(paramString)) {
        return true;
      }
    }
    return false;
  }
  
  public String toString()
  {
    return "AclImpl: " + getName();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\IPAcl\AclImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */