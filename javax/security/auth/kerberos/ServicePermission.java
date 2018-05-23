package javax.security.auth.kerberos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.Permission;
import java.security.PermissionCollection;

public final class ServicePermission
  extends Permission
  implements Serializable
{
  private static final long serialVersionUID = -1227585031618624935L;
  private static final int INITIATE = 1;
  private static final int ACCEPT = 2;
  private static final int ALL = 3;
  private static final int NONE = 0;
  private transient int mask;
  private String actions;
  
  public ServicePermission(String paramString1, String paramString2)
  {
    super(paramString1);
    init(paramString1, getMask(paramString2));
  }
  
  private void init(String paramString, int paramInt)
  {
    if (paramString == null) {
      throw new NullPointerException("service principal can't be null");
    }
    if ((paramInt & 0x3) != paramInt) {
      throw new IllegalArgumentException("invalid actions mask");
    }
    mask = paramInt;
  }
  
  public boolean implies(Permission paramPermission)
  {
    if (!(paramPermission instanceof ServicePermission)) {
      return false;
    }
    ServicePermission localServicePermission = (ServicePermission)paramPermission;
    return ((mask & mask) == mask) && (impliesIgnoreMask(localServicePermission));
  }
  
  boolean impliesIgnoreMask(ServicePermission paramServicePermission)
  {
    return (getName().equals("*")) || (getName().equals(paramServicePermission.getName())) || ((paramServicePermission.getName().startsWith("@")) && (getName().endsWith(paramServicePermission.getName())));
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof ServicePermission)) {
      return false;
    }
    ServicePermission localServicePermission = (ServicePermission)paramObject;
    return ((mask & mask) == mask) && (getName().equals(localServicePermission.getName()));
  }
  
  public int hashCode()
  {
    return getName().hashCode() ^ mask;
  }
  
  private static String getActions(int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    if ((paramInt & 0x1) == 1)
    {
      if (i != 0) {
        localStringBuilder.append(',');
      } else {
        i = 1;
      }
      localStringBuilder.append("initiate");
    }
    if ((paramInt & 0x2) == 2)
    {
      if (i != 0) {
        localStringBuilder.append(',');
      } else {
        i = 1;
      }
      localStringBuilder.append("accept");
    }
    return localStringBuilder.toString();
  }
  
  public String getActions()
  {
    if (actions == null) {
      actions = getActions(mask);
    }
    return actions;
  }
  
  public PermissionCollection newPermissionCollection()
  {
    return new KrbServicePermissionCollection();
  }
  
  int getMask()
  {
    return mask;
  }
  
  private static int getMask(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("action can't be null");
    }
    if (paramString.equals("")) {
      throw new IllegalArgumentException("action can't be empty");
    }
    int i = 0;
    char[] arrayOfChar = paramString.toCharArray();
    if ((arrayOfChar.length == 1) && (arrayOfChar[0] == '-')) {
      return i;
    }
    int j = arrayOfChar.length - 1;
    while (j != -1)
    {
      int k;
      while ((j != -1) && (((k = arrayOfChar[j]) == ' ') || (k == 13) || (k == 10) || (k == 12) || (k == 9))) {
        j--;
      }
      int m;
      if ((j >= 7) && ((arrayOfChar[(j - 7)] == 'i') || (arrayOfChar[(j - 7)] == 'I')) && ((arrayOfChar[(j - 6)] == 'n') || (arrayOfChar[(j - 6)] == 'N')) && ((arrayOfChar[(j - 5)] == 'i') || (arrayOfChar[(j - 5)] == 'I')) && ((arrayOfChar[(j - 4)] == 't') || (arrayOfChar[(j - 4)] == 'T')) && ((arrayOfChar[(j - 3)] == 'i') || (arrayOfChar[(j - 3)] == 'I')) && ((arrayOfChar[(j - 2)] == 'a') || (arrayOfChar[(j - 2)] == 'A')) && ((arrayOfChar[(j - 1)] == 't') || (arrayOfChar[(j - 1)] == 'T')) && ((arrayOfChar[j] == 'e') || (arrayOfChar[j] == 'E')))
      {
        m = 8;
        i |= 0x1;
      }
      else if ((j >= 5) && ((arrayOfChar[(j - 5)] == 'a') || (arrayOfChar[(j - 5)] == 'A')) && ((arrayOfChar[(j - 4)] == 'c') || (arrayOfChar[(j - 4)] == 'C')) && ((arrayOfChar[(j - 3)] == 'c') || (arrayOfChar[(j - 3)] == 'C')) && ((arrayOfChar[(j - 2)] == 'e') || (arrayOfChar[(j - 2)] == 'E')) && ((arrayOfChar[(j - 1)] == 'p') || (arrayOfChar[(j - 1)] == 'P')) && ((arrayOfChar[j] == 't') || (arrayOfChar[j] == 'T')))
      {
        m = 6;
        i |= 0x2;
      }
      else
      {
        throw new IllegalArgumentException("invalid permission: " + paramString);
      }
      int n = 0;
      while ((j >= m) && (n == 0))
      {
        switch (arrayOfChar[(j - m)])
        {
        case ',': 
          n = 1;
          break;
        case '\t': 
        case '\n': 
        case '\f': 
        case '\r': 
        case ' ': 
          break;
        default: 
          throw new IllegalArgumentException("invalid permission: " + paramString);
        }
        j--;
      }
      j -= m;
    }
    return i;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    if (actions == null) {
      getActions();
    }
    paramObjectOutputStream.defaultWriteObject();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    init(getName(), getMask(actions));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\auth\kerberos\ServicePermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */