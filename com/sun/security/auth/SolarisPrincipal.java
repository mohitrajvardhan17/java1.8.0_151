package com.sun.security.auth;

import java.io.Serializable;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.ResourceBundle;
import jdk.Exported;

@Exported(false)
@Deprecated
public class SolarisPrincipal
  implements Principal, Serializable
{
  private static final long serialVersionUID = -7840670002439379038L;
  private static final ResourceBundle rb = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction()
  {
    public ResourceBundle run()
    {
      return ResourceBundle.getBundle("sun.security.util.AuthResources");
    }
  });
  private String name;
  
  public SolarisPrincipal(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException(rb.getString("provided.null.name"));
    }
    name = paramString;
  }
  
  public String getName()
  {
    return name;
  }
  
  public String toString()
  {
    return rb.getString("SolarisPrincipal.") + name;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof SolarisPrincipal)) {
      return false;
    }
    SolarisPrincipal localSolarisPrincipal = (SolarisPrincipal)paramObject;
    return getName().equals(localSolarisPrincipal.getName());
  }
  
  public int hashCode()
  {
    return name.hashCode();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\auth\SolarisPrincipal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */