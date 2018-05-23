package com.sun.security.auth;

import java.io.Serializable;
import java.security.Principal;
import java.text.MessageFormat;
import jdk.Exported;
import sun.security.util.ResourcesMgr;

@Exported
public class UnixNumericGroupPrincipal
  implements Principal, Serializable
{
  private static final long serialVersionUID = 3941535899328403223L;
  private String name;
  private boolean primaryGroup;
  
  public UnixNumericGroupPrincipal(String paramString, boolean paramBoolean)
  {
    if (paramString == null)
    {
      MessageFormat localMessageFormat = new MessageFormat(ResourcesMgr.getString("invalid.null.input.value", "sun.security.util.AuthResources"));
      Object[] arrayOfObject = { "name" };
      throw new NullPointerException(localMessageFormat.format(arrayOfObject));
    }
    name = paramString;
    primaryGroup = paramBoolean;
  }
  
  public UnixNumericGroupPrincipal(long paramLong, boolean paramBoolean)
  {
    name = new Long(paramLong).toString();
    primaryGroup = paramBoolean;
  }
  
  public String getName()
  {
    return name;
  }
  
  public long longValue()
  {
    return new Long(name).longValue();
  }
  
  public boolean isPrimaryGroup()
  {
    return primaryGroup;
  }
  
  public String toString()
  {
    if (primaryGroup)
    {
      localMessageFormat = new MessageFormat(ResourcesMgr.getString("UnixNumericGroupPrincipal.Primary.Group.name", "sun.security.util.AuthResources"));
      arrayOfObject = new Object[] { name };
      return localMessageFormat.format(arrayOfObject);
    }
    MessageFormat localMessageFormat = new MessageFormat(ResourcesMgr.getString("UnixNumericGroupPrincipal.Supplementary.Group.name", "sun.security.util.AuthResources"));
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
    if (!(paramObject instanceof UnixNumericGroupPrincipal)) {
      return false;
    }
    UnixNumericGroupPrincipal localUnixNumericGroupPrincipal = (UnixNumericGroupPrincipal)paramObject;
    return (getName().equals(localUnixNumericGroupPrincipal.getName())) && (isPrimaryGroup() == localUnixNumericGroupPrincipal.isPrimaryGroup());
  }
  
  public int hashCode()
  {
    return toString().hashCode();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\auth\UnixNumericGroupPrincipal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */