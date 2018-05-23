package com.sun.security.auth;

import java.io.Serializable;
import java.security.Principal;
import jdk.Exported;

@Exported
public final class UserPrincipal
  implements Principal, Serializable
{
  private static final long serialVersionUID = 892106070870210969L;
  private final String name;
  
  public UserPrincipal(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("null name is illegal");
    }
    name = paramString;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof UserPrincipal)) {
      return name.equals(((UserPrincipal)paramObject).getName());
    }
    return false;
  }
  
  public int hashCode()
  {
    return name.hashCode();
  }
  
  public String getName()
  {
    return name;
  }
  
  public String toString()
  {
    return name;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\auth\UserPrincipal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */