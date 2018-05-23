package java.io;

import java.security.AccessController;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.PrivilegedAction;

public final class FilePermission
  extends Permission
  implements Serializable
{
  private static final int EXECUTE = 1;
  private static final int WRITE = 2;
  private static final int READ = 4;
  private static final int DELETE = 8;
  private static final int READLINK = 16;
  private static final int ALL = 31;
  private static final int NONE = 0;
  private transient int mask;
  private transient boolean directory;
  private transient boolean recursive;
  private String actions;
  private transient String cpath;
  private static final char RECURSIVE_CHAR = '-';
  private static final char WILD_CHAR = '*';
  private static final long serialVersionUID = 7930732926638008763L;
  
  private void init(int paramInt)
  {
    if ((paramInt & 0x1F) != paramInt) {
      throw new IllegalArgumentException("invalid actions mask");
    }
    if (paramInt == 0) {
      throw new IllegalArgumentException("invalid actions mask");
    }
    if ((cpath = getName()) == null) {
      throw new NullPointerException("name can't be null");
    }
    mask = paramInt;
    if (cpath.equals("<<ALL FILES>>"))
    {
      directory = true;
      recursive = true;
      cpath = "";
      return;
    }
    cpath = ((String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public String run()
      {
        try
        {
          String str = cpath;
          if (cpath.endsWith("*"))
          {
            str = str.substring(0, str.length() - 1) + "-";
            str = new File(str).getCanonicalPath();
            return str.substring(0, str.length() - 1) + "*";
          }
          return new File(str).getCanonicalPath();
        }
        catch (IOException localIOException) {}
        return cpath;
      }
    }));
    int i = cpath.length();
    int j = i > 0 ? cpath.charAt(i - 1) : 0;
    if ((j == 45) && (cpath.charAt(i - 2) == File.separatorChar))
    {
      directory = true;
      recursive = true;
      cpath = cpath.substring(0, --i);
    }
    else if ((j == 42) && (cpath.charAt(i - 2) == File.separatorChar))
    {
      directory = true;
      cpath = cpath.substring(0, --i);
    }
  }
  
  public FilePermission(String paramString1, String paramString2)
  {
    super(paramString1);
    init(getMask(paramString2));
  }
  
  FilePermission(String paramString, int paramInt)
  {
    super(paramString);
    init(paramInt);
  }
  
  public boolean implies(Permission paramPermission)
  {
    if (!(paramPermission instanceof FilePermission)) {
      return false;
    }
    FilePermission localFilePermission = (FilePermission)paramPermission;
    return ((mask & mask) == mask) && (impliesIgnoreMask(localFilePermission));
  }
  
  boolean impliesIgnoreMask(FilePermission paramFilePermission)
  {
    if (directory)
    {
      if (recursive)
      {
        if (directory) {
          return (cpath.length() >= cpath.length()) && (cpath.startsWith(cpath));
        }
        return (cpath.length() > cpath.length()) && (cpath.startsWith(cpath));
      }
      if (directory)
      {
        if (recursive) {
          return false;
        }
        return cpath.equals(cpath);
      }
      int i = cpath.lastIndexOf(File.separatorChar);
      if (i == -1) {
        return false;
      }
      return (cpath.length() == i + 1) && (cpath.regionMatches(0, cpath, 0, i + 1));
    }
    if (directory) {
      return false;
    }
    return cpath.equals(cpath);
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof FilePermission)) {
      return false;
    }
    FilePermission localFilePermission = (FilePermission)paramObject;
    return (mask == mask) && (cpath.equals(cpath)) && (directory == directory) && (recursive == recursive);
  }
  
  public int hashCode()
  {
    return 0;
  }
  
  private static int getMask(String paramString)
  {
    int i = 0;
    if (paramString == null) {
      return i;
    }
    if (paramString == "read") {
      return 4;
    }
    if (paramString == "write") {
      return 2;
    }
    if (paramString == "execute") {
      return 1;
    }
    if (paramString == "delete") {
      return 8;
    }
    if (paramString == "readlink") {
      return 16;
    }
    char[] arrayOfChar = paramString.toCharArray();
    int j = arrayOfChar.length - 1;
    if (j < 0) {
      return i;
    }
    while (j != -1)
    {
      int k;
      while ((j != -1) && (((k = arrayOfChar[j]) == ' ') || (k == 13) || (k == 10) || (k == 12) || (k == 9))) {
        j--;
      }
      int m;
      if ((j >= 3) && ((arrayOfChar[(j - 3)] == 'r') || (arrayOfChar[(j - 3)] == 'R')) && ((arrayOfChar[(j - 2)] == 'e') || (arrayOfChar[(j - 2)] == 'E')) && ((arrayOfChar[(j - 1)] == 'a') || (arrayOfChar[(j - 1)] == 'A')) && ((arrayOfChar[j] == 'd') || (arrayOfChar[j] == 'D')))
      {
        m = 4;
        i |= 0x4;
      }
      else if ((j >= 4) && ((arrayOfChar[(j - 4)] == 'w') || (arrayOfChar[(j - 4)] == 'W')) && ((arrayOfChar[(j - 3)] == 'r') || (arrayOfChar[(j - 3)] == 'R')) && ((arrayOfChar[(j - 2)] == 'i') || (arrayOfChar[(j - 2)] == 'I')) && ((arrayOfChar[(j - 1)] == 't') || (arrayOfChar[(j - 1)] == 'T')) && ((arrayOfChar[j] == 'e') || (arrayOfChar[j] == 'E')))
      {
        m = 5;
        i |= 0x2;
      }
      else if ((j >= 6) && ((arrayOfChar[(j - 6)] == 'e') || (arrayOfChar[(j - 6)] == 'E')) && ((arrayOfChar[(j - 5)] == 'x') || (arrayOfChar[(j - 5)] == 'X')) && ((arrayOfChar[(j - 4)] == 'e') || (arrayOfChar[(j - 4)] == 'E')) && ((arrayOfChar[(j - 3)] == 'c') || (arrayOfChar[(j - 3)] == 'C')) && ((arrayOfChar[(j - 2)] == 'u') || (arrayOfChar[(j - 2)] == 'U')) && ((arrayOfChar[(j - 1)] == 't') || (arrayOfChar[(j - 1)] == 'T')) && ((arrayOfChar[j] == 'e') || (arrayOfChar[j] == 'E')))
      {
        m = 7;
        i |= 0x1;
      }
      else if ((j >= 5) && ((arrayOfChar[(j - 5)] == 'd') || (arrayOfChar[(j - 5)] == 'D')) && ((arrayOfChar[(j - 4)] == 'e') || (arrayOfChar[(j - 4)] == 'E')) && ((arrayOfChar[(j - 3)] == 'l') || (arrayOfChar[(j - 3)] == 'L')) && ((arrayOfChar[(j - 2)] == 'e') || (arrayOfChar[(j - 2)] == 'E')) && ((arrayOfChar[(j - 1)] == 't') || (arrayOfChar[(j - 1)] == 'T')) && ((arrayOfChar[j] == 'e') || (arrayOfChar[j] == 'E')))
      {
        m = 6;
        i |= 0x8;
      }
      else if ((j >= 7) && ((arrayOfChar[(j - 7)] == 'r') || (arrayOfChar[(j - 7)] == 'R')) && ((arrayOfChar[(j - 6)] == 'e') || (arrayOfChar[(j - 6)] == 'E')) && ((arrayOfChar[(j - 5)] == 'a') || (arrayOfChar[(j - 5)] == 'A')) && ((arrayOfChar[(j - 4)] == 'd') || (arrayOfChar[(j - 4)] == 'D')) && ((arrayOfChar[(j - 3)] == 'l') || (arrayOfChar[(j - 3)] == 'L')) && ((arrayOfChar[(j - 2)] == 'i') || (arrayOfChar[(j - 2)] == 'I')) && ((arrayOfChar[(j - 1)] == 'n') || (arrayOfChar[(j - 1)] == 'N')) && ((arrayOfChar[j] == 'k') || (arrayOfChar[j] == 'K')))
      {
        m = 8;
        i |= 0x10;
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
  
  int getMask()
  {
    return mask;
  }
  
  private static String getActions(int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    if ((paramInt & 0x4) == 4)
    {
      i = 1;
      localStringBuilder.append("read");
    }
    if ((paramInt & 0x2) == 2)
    {
      if (i != 0) {
        localStringBuilder.append(',');
      } else {
        i = 1;
      }
      localStringBuilder.append("write");
    }
    if ((paramInt & 0x1) == 1)
    {
      if (i != 0) {
        localStringBuilder.append(',');
      } else {
        i = 1;
      }
      localStringBuilder.append("execute");
    }
    if ((paramInt & 0x8) == 8)
    {
      if (i != 0) {
        localStringBuilder.append(',');
      } else {
        i = 1;
      }
      localStringBuilder.append("delete");
    }
    if ((paramInt & 0x10) == 16)
    {
      if (i != 0) {
        localStringBuilder.append(',');
      } else {
        i = 1;
      }
      localStringBuilder.append("readlink");
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
    return new FilePermissionCollection();
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
    init(getMask(actions));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\FilePermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */