package java.security;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.NoSuchElementException;

public abstract class PermissionCollection
  implements Serializable
{
  private static final long serialVersionUID = -6727011328946861783L;
  private volatile boolean readOnly;
  
  public PermissionCollection() {}
  
  public abstract void add(Permission paramPermission);
  
  public abstract boolean implies(Permission paramPermission);
  
  public abstract Enumeration<Permission> elements();
  
  public void setReadOnly()
  {
    readOnly = true;
  }
  
  public boolean isReadOnly()
  {
    return readOnly;
  }
  
  public String toString()
  {
    Enumeration localEnumeration = elements();
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(super.toString() + " (\n");
    while (localEnumeration.hasMoreElements()) {
      try
      {
        localStringBuilder.append(" ");
        localStringBuilder.append(((Permission)localEnumeration.nextElement()).toString());
        localStringBuilder.append("\n");
      }
      catch (NoSuchElementException localNoSuchElementException) {}
    }
    localStringBuilder.append(")\n");
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\PermissionCollection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */