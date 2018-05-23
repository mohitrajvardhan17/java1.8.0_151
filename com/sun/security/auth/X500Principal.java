package com.sun.security.auth;

import java.io.IOException;
import java.io.NotActiveException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.ResourceBundle;
import jdk.Exported;
import sun.security.x509.X500Name;

@Exported(false)
@Deprecated
public class X500Principal
  implements Principal, Serializable
{
  private static final long serialVersionUID = -8222422609431628648L;
  private static final ResourceBundle rb = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction()
  {
    public ResourceBundle run()
    {
      return ResourceBundle.getBundle("sun.security.util.AuthResources");
    }
  });
  private String name;
  private transient X500Name thisX500Name;
  
  public X500Principal(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException(rb.getString("provided.null.name"));
    }
    try
    {
      thisX500Name = new X500Name(paramString);
    }
    catch (Exception localException)
    {
      throw new IllegalArgumentException(localException.toString());
    }
    name = paramString;
  }
  
  public String getName()
  {
    return thisX500Name.getName();
  }
  
  public String toString()
  {
    return thisX500Name.toString();
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof X500Principal))
    {
      X500Principal localX500Principal = (X500Principal)paramObject;
      try
      {
        X500Name localX500Name = new X500Name(localX500Principal.getName());
        return thisX500Name.equals(localX500Name);
      }
      catch (Exception localException)
      {
        return false;
      }
    }
    if ((paramObject instanceof Principal)) {
      return paramObject.equals(thisX500Name);
    }
    return false;
  }
  
  public int hashCode()
  {
    return thisX500Name.hashCode();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, NotActiveException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    thisX500Name = new X500Name(name);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\auth\X500Principal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */