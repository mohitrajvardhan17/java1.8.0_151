package sun.security.acl;

import java.security.Principal;
import java.security.acl.Acl;
import java.security.acl.AclEntry;
import java.security.acl.Group;
import java.security.acl.NotOwnerException;
import java.security.acl.Permission;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class AclImpl
  extends OwnerImpl
  implements Acl
{
  private Hashtable<Principal, AclEntry> allowedUsersTable = new Hashtable(23);
  private Hashtable<Principal, AclEntry> allowedGroupsTable = new Hashtable(23);
  private Hashtable<Principal, AclEntry> deniedUsersTable = new Hashtable(23);
  private Hashtable<Principal, AclEntry> deniedGroupsTable = new Hashtable(23);
  private String aclName = null;
  private Vector<Permission> zeroSet = new Vector(1, 1);
  
  public AclImpl(Principal paramPrincipal, String paramString)
  {
    super(paramPrincipal);
    try
    {
      setName(paramPrincipal, paramString);
    }
    catch (Exception localException) {}
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
  
  public synchronized boolean addEntry(Principal paramPrincipal, AclEntry paramAclEntry)
    throws NotOwnerException
  {
    if (!isOwner(paramPrincipal)) {
      throw new NotOwnerException();
    }
    Hashtable localHashtable = findTable(paramAclEntry);
    Principal localPrincipal = paramAclEntry.getPrincipal();
    if (localHashtable.get(localPrincipal) != null) {
      return false;
    }
    localHashtable.put(localPrincipal, paramAclEntry);
    return true;
  }
  
  public synchronized boolean removeEntry(Principal paramPrincipal, AclEntry paramAclEntry)
    throws NotOwnerException
  {
    if (!isOwner(paramPrincipal)) {
      throw new NotOwnerException();
    }
    Hashtable localHashtable = findTable(paramAclEntry);
    Principal localPrincipal = paramAclEntry.getPrincipal();
    AclEntry localAclEntry = (AclEntry)localHashtable.remove(localPrincipal);
    return localAclEntry != null;
  }
  
  public synchronized Enumeration<Permission> getPermissions(Principal paramPrincipal)
  {
    Enumeration localEnumeration3 = subtract(getGroupPositive(paramPrincipal), getGroupNegative(paramPrincipal));
    Enumeration localEnumeration4 = subtract(getGroupNegative(paramPrincipal), getGroupPositive(paramPrincipal));
    Enumeration localEnumeration1 = subtract(getIndividualPositive(paramPrincipal), getIndividualNegative(paramPrincipal));
    Enumeration localEnumeration2 = subtract(getIndividualNegative(paramPrincipal), getIndividualPositive(paramPrincipal));
    Enumeration localEnumeration5 = subtract(localEnumeration3, localEnumeration2);
    Enumeration localEnumeration6 = union(localEnumeration1, localEnumeration5);
    localEnumeration1 = subtract(getIndividualPositive(paramPrincipal), getIndividualNegative(paramPrincipal));
    localEnumeration2 = subtract(getIndividualNegative(paramPrincipal), getIndividualPositive(paramPrincipal));
    localEnumeration5 = subtract(localEnumeration4, localEnumeration1);
    Enumeration localEnumeration7 = union(localEnumeration2, localEnumeration5);
    return subtract(localEnumeration6, localEnumeration7);
  }
  
  public boolean checkPermission(Principal paramPrincipal, Permission paramPermission)
  {
    Enumeration localEnumeration = getPermissions(paramPrincipal);
    while (localEnumeration.hasMoreElements())
    {
      Permission localPermission = (Permission)localEnumeration.nextElement();
      if (localPermission.equals(paramPermission)) {
        return true;
      }
    }
    return false;
  }
  
  public synchronized Enumeration<AclEntry> entries()
  {
    return new AclEnumerator(this, allowedUsersTable, allowedGroupsTable, deniedUsersTable, deniedGroupsTable);
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    Enumeration localEnumeration = entries();
    while (localEnumeration.hasMoreElements())
    {
      AclEntry localAclEntry = (AclEntry)localEnumeration.nextElement();
      localStringBuffer.append(localAclEntry.toString().trim());
      localStringBuffer.append("\n");
    }
    return localStringBuffer.toString();
  }
  
  private Hashtable<Principal, AclEntry> findTable(AclEntry paramAclEntry)
  {
    Hashtable localHashtable = null;
    Principal localPrincipal = paramAclEntry.getPrincipal();
    if ((localPrincipal instanceof Group))
    {
      if (paramAclEntry.isNegative()) {
        localHashtable = deniedGroupsTable;
      } else {
        localHashtable = allowedGroupsTable;
      }
    }
    else if (paramAclEntry.isNegative()) {
      localHashtable = deniedUsersTable;
    } else {
      localHashtable = allowedUsersTable;
    }
    return localHashtable;
  }
  
  private static Enumeration<Permission> union(Enumeration<Permission> paramEnumeration1, Enumeration<Permission> paramEnumeration2)
  {
    Vector localVector = new Vector(20, 20);
    while (paramEnumeration1.hasMoreElements()) {
      localVector.addElement(paramEnumeration1.nextElement());
    }
    while (paramEnumeration2.hasMoreElements())
    {
      Permission localPermission = (Permission)paramEnumeration2.nextElement();
      if (!localVector.contains(localPermission)) {
        localVector.addElement(localPermission);
      }
    }
    return localVector.elements();
  }
  
  private Enumeration<Permission> subtract(Enumeration<Permission> paramEnumeration1, Enumeration<Permission> paramEnumeration2)
  {
    Vector localVector = new Vector(20, 20);
    while (paramEnumeration1.hasMoreElements()) {
      localVector.addElement(paramEnumeration1.nextElement());
    }
    while (paramEnumeration2.hasMoreElements())
    {
      Permission localPermission = (Permission)paramEnumeration2.nextElement();
      if (localVector.contains(localPermission)) {
        localVector.removeElement(localPermission);
      }
    }
    return localVector.elements();
  }
  
  private Enumeration<Permission> getGroupPositive(Principal paramPrincipal)
  {
    Enumeration localEnumeration1 = zeroSet.elements();
    Enumeration localEnumeration2 = allowedGroupsTable.keys();
    while (localEnumeration2.hasMoreElements())
    {
      Group localGroup = (Group)localEnumeration2.nextElement();
      if (localGroup.isMember(paramPrincipal))
      {
        AclEntry localAclEntry = (AclEntry)allowedGroupsTable.get(localGroup);
        localEnumeration1 = union(localAclEntry.permissions(), localEnumeration1);
      }
    }
    return localEnumeration1;
  }
  
  private Enumeration<Permission> getGroupNegative(Principal paramPrincipal)
  {
    Enumeration localEnumeration1 = zeroSet.elements();
    Enumeration localEnumeration2 = deniedGroupsTable.keys();
    while (localEnumeration2.hasMoreElements())
    {
      Group localGroup = (Group)localEnumeration2.nextElement();
      if (localGroup.isMember(paramPrincipal))
      {
        AclEntry localAclEntry = (AclEntry)deniedGroupsTable.get(localGroup);
        localEnumeration1 = union(localAclEntry.permissions(), localEnumeration1);
      }
    }
    return localEnumeration1;
  }
  
  private Enumeration<Permission> getIndividualPositive(Principal paramPrincipal)
  {
    Enumeration localEnumeration = zeroSet.elements();
    AclEntry localAclEntry = (AclEntry)allowedUsersTable.get(paramPrincipal);
    if (localAclEntry != null) {
      localEnumeration = localAclEntry.permissions();
    }
    return localEnumeration;
  }
  
  private Enumeration<Permission> getIndividualNegative(Principal paramPrincipal)
  {
    Enumeration localEnumeration = zeroSet.elements();
    AclEntry localAclEntry = (AclEntry)deniedUsersTable.get(paramPrincipal);
    if (localAclEntry != null) {
      localEnumeration = localAclEntry.permissions();
    }
    return localEnumeration;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\acl\AclImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */