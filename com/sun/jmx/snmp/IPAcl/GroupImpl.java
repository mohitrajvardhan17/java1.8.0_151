package com.sun.jmx.snmp.IPAcl;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Enumeration;
import java.util.Vector;

class GroupImpl
  extends PrincipalImpl
  implements Group, Serializable
{
  private static final long serialVersionUID = -7777387035032541168L;
  
  public GroupImpl()
    throws UnknownHostException
  {}
  
  public GroupImpl(String paramString)
    throws UnknownHostException
  {
    super(paramString);
  }
  
  public boolean addMember(Principal paramPrincipal)
  {
    return true;
  }
  
  public int hashCode()
  {
    return super.hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    if (((paramObject instanceof PrincipalImpl)) || ((paramObject instanceof GroupImpl))) {
      return (super.hashCode() & paramObject.hashCode()) == paramObject.hashCode();
    }
    return false;
  }
  
  public boolean isMember(Principal paramPrincipal)
  {
    return (paramPrincipal.hashCode() & super.hashCode()) == paramPrincipal.hashCode();
  }
  
  public Enumeration<? extends Principal> members()
  {
    Vector localVector = new Vector(1);
    localVector.addElement(this);
    return localVector.elements();
  }
  
  public boolean removeMember(Principal paramPrincipal)
  {
    return true;
  }
  
  public String toString()
  {
    return "GroupImpl :" + super.getAddress().toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\IPAcl\GroupImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */