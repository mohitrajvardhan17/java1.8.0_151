package com.sun.security.auth;

import java.io.Serializable;
import java.security.Principal;
import java.text.MessageFormat;
import jdk.Exported;
import sun.security.util.ResourcesMgr;

@Exported
public class UnixNumericUserPrincipal
  implements Principal, Serializable
{
  private static final long serialVersionUID = -4329764253802397821L;
  private String name;
  
  public UnixNumericUserPrincipal(String paramString)
  {
    if (paramString == null)
    {
      MessageFormat localMessageFormat = new MessageFormat(ResourcesMgr.getString("invalid.null.input.value", "sun.security.util.AuthResources"));
      Object[] arrayOfObject = { "name" };
      throw new NullPointerException(localMessageFormat.format(arrayOfObject));
    }
    name = paramString;
  }
  
  public UnixNumericUserPrincipal(long paramLong)
  {
    name = new Long(paramLong).toString();
  }
  
  public String getName()
  {
    return name;
  }
  
  public long longValue()
  {
    return new Long(name).longValue();
  }
  
  public String toString()
  {
    MessageFormat localMessageFormat = new MessageFormat(ResourcesMgr.getString("UnixNumericUserPrincipal.name", "sun.security.util.AuthResources"));
    Object[] arrayOfObject = { name };
    return localMessageFormat.format(arrayOfObject);
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof UnixNumericUserPrincipal)) {
      return false;
    }
    UnixNumericUserPrincipal localUnixNumericUserPrincipal = (UnixNumericUserPrincipal)paramObject;
    return getName().equals(localUnixNumericUserPrincipal.getName());
  }
  
  public int hashCode()
  {
    return name.hashCode();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\auth\UnixNumericUserPrincipal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */