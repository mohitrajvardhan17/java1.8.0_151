package sun.misc;

import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.util.PropertyPermission;
import sun.security.util.SecurityConstants;

class PathPermissions
  extends PermissionCollection
{
  private static final long serialVersionUID = 8133287259134945693L;
  private File[] path;
  private Permissions perms;
  URL codeBase;
  
  PathPermissions(File[] paramArrayOfFile)
  {
    path = paramArrayOfFile;
    perms = null;
    codeBase = null;
  }
  
  URL getCodeBase()
  {
    return codeBase;
  }
  
  public void add(Permission paramPermission)
  {
    throw new SecurityException("attempt to add a permission");
  }
  
  private synchronized void init()
  {
    if (perms != null) {
      return;
    }
    perms = new Permissions();
    perms.add(SecurityConstants.CREATE_CLASSLOADER_PERMISSION);
    perms.add(new PropertyPermission("java.*", "read"));
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        for (int i = 0; i < path.length; i++)
        {
          File localFile = path[i];
          String str;
          try
          {
            str = localFile.getCanonicalPath();
          }
          catch (IOException localIOException)
          {
            str = localFile.getAbsolutePath();
          }
          if (i == 0) {
            codeBase = Launcher.getFileURL(new File(str));
          }
          if (localFile.isDirectory())
          {
            if (str.endsWith(File.separator)) {
              perms.add(new FilePermission(str + "-", "read"));
            } else {
              perms.add(new FilePermission(str + File.separator + "-", "read"));
            }
          }
          else
          {
            int j = str.lastIndexOf(File.separatorChar);
            if (j != -1)
            {
              str = str.substring(0, j + 1) + "-";
              perms.add(new FilePermission(str, "read"));
            }
          }
        }
        return null;
      }
    });
  }
  
  public boolean implies(Permission paramPermission)
  {
    if (perms == null) {
      init();
    }
    return perms.implies(paramPermission);
  }
  
  /* Error */
  public java.util.Enumeration<Permission> elements()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 84	sun/misc/PathPermissions:perms	Ljava/security/Permissions;
    //   4: ifnonnull +7 -> 11
    //   7: aload_0
    //   8: invokespecial 95	sun/misc/PathPermissions:init	()V
    //   11: aload_0
    //   12: getfield 84	sun/misc/PathPermissions:perms	Ljava/security/Permissions;
    //   15: dup
    //   16: astore_1
    //   17: monitorenter
    //   18: aload_0
    //   19: getfield 84	sun/misc/PathPermissions:perms	Ljava/security/Permissions;
    //   22: invokevirtual 93	java/security/Permissions:elements	()Ljava/util/Enumeration;
    //   25: aload_1
    //   26: monitorexit
    //   27: areturn
    //   28: astore_2
    //   29: aload_1
    //   30: monitorexit
    //   31: aload_2
    //   32: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	33	0	this	PathPermissions
    //   16	14	1	Ljava/lang/Object;	Object
    //   28	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   18	27	28	finally
    //   28	31	28	finally
  }
  
  public String toString()
  {
    if (perms == null) {
      init();
    }
    return perms.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\PathPermissions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */