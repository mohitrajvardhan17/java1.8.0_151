package sun.corba;

import java.security.BasicPermission;

public final class BridgePermission
  extends BasicPermission
{
  public BridgePermission(String paramString)
  {
    super(paramString);
  }
  
  public BridgePermission(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\corba\BridgePermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */