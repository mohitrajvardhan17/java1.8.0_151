package sun.awt;

import java.awt.AWTPermission;
import sun.security.util.PermissionFactory;

public class AWTPermissionFactory
  implements PermissionFactory<AWTPermission>
{
  public AWTPermissionFactory() {}
  
  public AWTPermission newPermission(String paramString)
  {
    return new AWTPermission(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\AWTPermissionFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */