package com.sun.security.auth;

import java.io.Serializable;
import java.security.Principal;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import jdk.Exported;

@Exported
public final class LdapPrincipal
  implements Principal, Serializable
{
  private static final long serialVersionUID = 6820120005580754861L;
  private final String nameString;
  private final LdapName name;
  
  public LdapPrincipal(String paramString)
    throws InvalidNameException
  {
    if (paramString == null) {
      throw new NullPointerException("null name is illegal");
    }
    name = getLdapName(paramString);
    nameString = paramString;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof LdapPrincipal)) {
      try
      {
        return name.equals(getLdapName(((LdapPrincipal)paramObject).getName()));
      }
      catch (InvalidNameException localInvalidNameException)
      {
        return false;
      }
    }
    return false;
  }
  
  public int hashCode()
  {
    return name.hashCode();
  }
  
  public String getName()
  {
    return nameString;
  }
  
  public String toString()
  {
    return name.toString();
  }
  
  private LdapName getLdapName(String paramString)
    throws InvalidNameException
  {
    return new LdapName(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\auth\LdapPrincipal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */