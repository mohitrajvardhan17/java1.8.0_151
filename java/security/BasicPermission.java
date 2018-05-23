package java.security;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public abstract class BasicPermission
  extends Permission
  implements Serializable
{
  private static final long serialVersionUID = 6279438298436773498L;
  private transient boolean wildcard;
  private transient String path;
  private transient boolean exitVM;
  
  private void init(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("name can't be null");
    }
    int i = paramString.length();
    if (i == 0) {
      throw new IllegalArgumentException("name can't be empty");
    }
    int j = paramString.charAt(i - 1);
    if ((j == 42) && ((i == 1) || (paramString.charAt(i - 2) == '.')))
    {
      wildcard = true;
      if (i == 1) {
        path = "";
      } else {
        path = paramString.substring(0, i - 1);
      }
    }
    else if (paramString.equals("exitVM"))
    {
      wildcard = true;
      path = "exitVM.";
      exitVM = true;
    }
    else
    {
      path = paramString;
    }
  }
  
  public BasicPermission(String paramString)
  {
    super(paramString);
    init(paramString);
  }
  
  public BasicPermission(String paramString1, String paramString2)
  {
    super(paramString1);
    init(paramString1);
  }
  
  public boolean implies(Permission paramPermission)
  {
    if ((paramPermission == null) || (paramPermission.getClass() != getClass())) {
      return false;
    }
    BasicPermission localBasicPermission = (BasicPermission)paramPermission;
    if (wildcard)
    {
      if (wildcard) {
        return path.startsWith(path);
      }
      return (path.length() > path.length()) && (path.startsWith(path));
    }
    if (wildcard) {
      return false;
    }
    return path.equals(path);
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject == null) || (paramObject.getClass() != getClass())) {
      return false;
    }
    BasicPermission localBasicPermission = (BasicPermission)paramObject;
    return getName().equals(localBasicPermission.getName());
  }
  
  public int hashCode()
  {
    return getName().hashCode();
  }
  
  public String getActions()
  {
    return "";
  }
  
  public PermissionCollection newPermissionCollection()
  {
    return new BasicPermissionCollection(getClass());
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    init(getName());
  }
  
  final String getCanonicalName()
  {
    return exitVM ? "exitVM.*" : getName();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\BasicPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */