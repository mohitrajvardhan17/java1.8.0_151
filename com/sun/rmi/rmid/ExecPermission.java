package com.sun.rmi.rmid;

import java.io.FilePermission;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Enumeration;
import java.util.Vector;

public final class ExecPermission
  extends Permission
{
  private static final long serialVersionUID = -6208470287358147919L;
  private transient FilePermission fp;
  
  public ExecPermission(String paramString)
  {
    super(paramString);
    init(paramString);
  }
  
  public ExecPermission(String paramString1, String paramString2)
  {
    this(paramString1);
  }
  
  public boolean implies(Permission paramPermission)
  {
    if (!(paramPermission instanceof ExecPermission)) {
      return false;
    }
    ExecPermission localExecPermission = (ExecPermission)paramPermission;
    return fp.implies(fp);
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof ExecPermission)) {
      return false;
    }
    ExecPermission localExecPermission = (ExecPermission)paramObject;
    return fp.equals(fp);
  }
  
  public int hashCode()
  {
    return fp.hashCode();
  }
  
  public String getActions()
  {
    return "";
  }
  
  public PermissionCollection newPermissionCollection()
  {
    return new ExecPermissionCollection();
  }
  
  private synchronized void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    init(getName());
  }
  
  private void init(String paramString)
  {
    fp = new FilePermission(paramString, "execute");
  }
  
  private static class ExecPermissionCollection
    extends PermissionCollection
    implements Serializable
  {
    private Vector<Permission> permissions = new Vector();
    private static final long serialVersionUID = -3352558508888368273L;
    
    public ExecPermissionCollection() {}
    
    public void add(Permission paramPermission)
    {
      if (!(paramPermission instanceof ExecPermission)) {
        throw new IllegalArgumentException("invalid permission: " + paramPermission);
      }
      if (isReadOnly()) {
        throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");
      }
      permissions.addElement(paramPermission);
    }
    
    public boolean implies(Permission paramPermission)
    {
      if (!(paramPermission instanceof ExecPermission)) {
        return false;
      }
      Enumeration localEnumeration = permissions.elements();
      while (localEnumeration.hasMoreElements())
      {
        ExecPermission localExecPermission = (ExecPermission)localEnumeration.nextElement();
        if (localExecPermission.implies(paramPermission)) {
          return true;
        }
      }
      return false;
    }
    
    public Enumeration<Permission> elements()
    {
      return permissions.elements();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\rmi\rmid\ExecPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */