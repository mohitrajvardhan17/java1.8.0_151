package com.sun.security.auth;

import java.io.Serializable;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.ResourceBundle;
import jdk.Exported;

@Exported(false)
@Deprecated
public class SolarisNumericGroupPrincipal
  implements Principal, Serializable
{
  private static final long serialVersionUID = 2345199581042573224L;
  private static final ResourceBundle rb = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction()
  {
    public ResourceBundle run()
    {
      return ResourceBundle.getBundle("sun.security.util.AuthResources");
    }
  });
  private String name;
  private boolean primaryGroup;
  
  public SolarisNumericGroupPrincipal(String paramString, boolean paramBoolean)
  {
    if (paramString == null) {
      throw new NullPointerException(rb.getString("provided.null.name"));
    }
    name = paramString;
    primaryGroup = paramBoolean;
  }
  
  public SolarisNumericGroupPrincipal(long paramLong, boolean paramBoolean)
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
    return rb.getString("SolarisNumericGroupPrincipal.Supplementary.Group.") + name;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof SolarisNumericGroupPrincipal)) {
      return false;
    }
    SolarisNumericGroupPrincipal localSolarisNumericGroupPrincipal = (SolarisNumericGroupPrincipal)paramObject;
    return (getName().equals(localSolarisNumericGroupPrincipal.getName())) && (isPrimaryGroup() == localSolarisNumericGroupPrincipal.isPrimaryGroup());
  }
  
  public int hashCode()
  {
    return toString().hashCode();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\auth\SolarisNumericGroupPrincipal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */