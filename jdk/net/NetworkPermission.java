package jdk.net;

import java.security.BasicPermission;
import jdk.Exported;

@Exported
public final class NetworkPermission
  extends BasicPermission
{
  private static final long serialVersionUID = -2012939586906722291L;
  
  public NetworkPermission(String paramString)
  {
    super(paramString);
  }
  
  public NetworkPermission(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\net\NetworkPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */