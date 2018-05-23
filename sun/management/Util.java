package sun.management;

import java.lang.management.ManagementPermission;
import java.util.List;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public class Util
{
  private static final String[] EMPTY_STRING_ARRAY = new String[0];
  private static ManagementPermission monitorPermission = new ManagementPermission("monitor");
  private static ManagementPermission controlPermission = new ManagementPermission("control");
  
  private Util() {}
  
  static RuntimeException newException(Exception paramException)
  {
    throw new RuntimeException(paramException);
  }
  
  static String[] toStringArray(List<String> paramList)
  {
    return (String[])paramList.toArray(EMPTY_STRING_ARRAY);
  }
  
  public static ObjectName newObjectName(String paramString1, String paramString2)
  {
    return newObjectName(paramString1 + ",name=" + paramString2);
  }
  
  public static ObjectName newObjectName(String paramString)
  {
    try
    {
      return ObjectName.getInstance(paramString);
    }
    catch (MalformedObjectNameException localMalformedObjectNameException)
    {
      throw new IllegalArgumentException(localMalformedObjectNameException);
    }
  }
  
  static void checkAccess(ManagementPermission paramManagementPermission)
    throws SecurityException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(paramManagementPermission);
    }
  }
  
  static void checkMonitorAccess()
    throws SecurityException
  {
    checkAccess(monitorPermission);
  }
  
  static void checkControlAccess()
    throws SecurityException
  {
    checkAccess(controlPermission);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */