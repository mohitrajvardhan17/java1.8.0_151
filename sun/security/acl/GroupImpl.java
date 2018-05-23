package sun.security.acl;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Enumeration;
import java.util.Vector;

public class GroupImpl
  implements Group
{
  private Vector<Principal> groupMembers = new Vector(50, 100);
  private String group;
  
  public GroupImpl(String paramString)
  {
    group = paramString;
  }
  
  public boolean addMember(Principal paramPrincipal)
  {
    if (groupMembers.contains(paramPrincipal)) {
      return false;
    }
    if (group.equals(paramPrincipal.toString())) {
      throw new IllegalArgumentException();
    }
    groupMembers.addElement(paramPrincipal);
    return true;
  }
  
  public boolean removeMember(Principal paramPrincipal)
  {
    return groupMembers.removeElement(paramPrincipal);
  }
  
  public Enumeration<? extends Principal> members()
  {
    return groupMembers.elements();
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof Group)) {
      return false;
    }
    Group localGroup = (Group)paramObject;
    return group.equals(localGroup.toString());
  }
  
  public boolean equals(Group paramGroup)
  {
    return equals(paramGroup);
  }
  
  public String toString()
  {
    return group;
  }
  
  public int hashCode()
  {
    return group.hashCode();
  }
  
  public boolean isMember(Principal paramPrincipal)
  {
    if (groupMembers.contains(paramPrincipal)) {
      return true;
    }
    Vector localVector = new Vector(10);
    return isMemberRecurse(paramPrincipal, localVector);
  }
  
  public String getName()
  {
    return group;
  }
  
  boolean isMemberRecurse(Principal paramPrincipal, Vector<Group> paramVector)
  {
    Enumeration localEnumeration = members();
    while (localEnumeration.hasMoreElements())
    {
      boolean bool = false;
      Principal localPrincipal = (Principal)localEnumeration.nextElement();
      if (localPrincipal.equals(paramPrincipal)) {
        return true;
      }
      Object localObject;
      if ((localPrincipal instanceof GroupImpl))
      {
        localObject = (GroupImpl)localPrincipal;
        paramVector.addElement(this);
        if (!paramVector.contains(localObject)) {
          bool = ((GroupImpl)localObject).isMemberRecurse(paramPrincipal, paramVector);
        }
      }
      else if ((localPrincipal instanceof Group))
      {
        localObject = (Group)localPrincipal;
        if (!paramVector.contains(localObject)) {
          bool = ((Group)localObject).isMember(paramPrincipal);
        }
      }
      if (bool) {
        return bool;
      }
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\acl\GroupImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */