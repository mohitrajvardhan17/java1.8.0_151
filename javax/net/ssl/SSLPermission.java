package javax.net.ssl;

import java.security.BasicPermission;

public final class SSLPermission
  extends BasicPermission
{
  private static final long serialVersionUID = -3456898025505876775L;
  
  public SSLPermission(String paramString)
  {
    super(paramString);
  }
  
  public SSLPermission(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\net\ssl\SSLPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */