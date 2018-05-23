package com.sun.jmx.snmp.IPAcl;

import java.io.Serializable;
import java.security.Principal;
import java.security.acl.LastOwnerException;
import java.security.acl.NotOwnerException;
import java.security.acl.Owner;
import java.util.Vector;

class OwnerImpl
  implements Owner, Serializable
{
  private static final long serialVersionUID = -576066072046319874L;
  private Vector<Principal> ownerList = null;
  
  public OwnerImpl() {}
  
  public OwnerImpl(PrincipalImpl paramPrincipalImpl)
  {
    ownerList.addElement(paramPrincipalImpl);
  }
  
  public boolean addOwner(Principal paramPrincipal1, Principal paramPrincipal2)
    throws NotOwnerException
  {
    if (!ownerList.contains(paramPrincipal1)) {
      throw new NotOwnerException();
    }
    if (ownerList.contains(paramPrincipal2)) {
      return false;
    }
    ownerList.addElement(paramPrincipal2);
    return true;
  }
  
  public boolean deleteOwner(Principal paramPrincipal1, Principal paramPrincipal2)
    throws NotOwnerException, LastOwnerException
  {
    if (!ownerList.contains(paramPrincipal1)) {
      throw new NotOwnerException();
    }
    if (!ownerList.contains(paramPrincipal2)) {
      return false;
    }
    if (ownerList.size() == 1) {
      throw new LastOwnerException();
    }
    ownerList.removeElement(paramPrincipal2);
    return true;
  }
  
  public boolean isOwner(Principal paramPrincipal)
  {
    return ownerList.contains(paramPrincipal);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\IPAcl\OwnerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */