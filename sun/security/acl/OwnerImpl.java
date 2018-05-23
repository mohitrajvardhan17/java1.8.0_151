package sun.security.acl;

import java.security.Principal;
import java.security.acl.Group;
import java.security.acl.LastOwnerException;
import java.security.acl.NotOwnerException;
import java.security.acl.Owner;
import java.util.Enumeration;

public class OwnerImpl
  implements Owner
{
  private Group ownerGroup = new GroupImpl("AclOwners");
  
  public OwnerImpl(Principal paramPrincipal)
  {
    ownerGroup.addMember(paramPrincipal);
  }
  
  public synchronized boolean addOwner(Principal paramPrincipal1, Principal paramPrincipal2)
    throws NotOwnerException
  {
    if (!isOwner(paramPrincipal1)) {
      throw new NotOwnerException();
    }
    ownerGroup.addMember(paramPrincipal2);
    return false;
  }
  
  public synchronized boolean deleteOwner(Principal paramPrincipal1, Principal paramPrincipal2)
    throws NotOwnerException, LastOwnerException
  {
    if (!isOwner(paramPrincipal1)) {
      throw new NotOwnerException();
    }
    Enumeration localEnumeration = ownerGroup.members();
    Object localObject = localEnumeration.nextElement();
    if (localEnumeration.hasMoreElements()) {
      return ownerGroup.removeMember(paramPrincipal2);
    }
    throw new LastOwnerException();
  }
  
  public synchronized boolean isOwner(Principal paramPrincipal)
  {
    return ownerGroup.isMember(paramPrincipal);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\acl\OwnerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */