package com.sun.security.auth;

import java.io.Serializable;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.ResourceBundle;
import jdk.Exported;

@Exported(false)
@Deprecated
public class SolarisNumericUserPrincipal
  implements Principal, Serializable
{
  private static final long serialVersionUID = -3178578484679887104L;
  private static final ResourceBundle rb = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction()
  {
    public ResourceBundle run()
    {
      return ResourceBundle.getBundle("sun.security.util.AuthResources");
    }
  });
  private String name;
  
  public SolarisNumericUserPrincipal(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException(rb.getString("provided.null.name"));
    }
    name = paramString;
  }
  
  public SolarisNumericUserPrincipal(long paramLong)
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
    return rb.getString("SolarisNumericUserPrincipal.") + name;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof SolarisNumericUserPrincipal)) {
      return false;
    }
    SolarisNumericUserPrincipal localSolarisNumericUserPrincipal = (SolarisNumericUserPrincipal)paramObject;
    return getName().equals(localSolarisNumericUserPrincipal.getName());
  }
  
  public int hashCode()
  {
    return name.hashCode();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\auth\SolarisNumericUserPrincipal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */