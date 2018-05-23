package javax.security.auth;

import java.security.BasicPermission;

public final class AuthPermission
  extends BasicPermission
{
  private static final long serialVersionUID = 5806031445061587174L;
  
  public AuthPermission(String paramString)
  {
    super("createLoginContext".equals(paramString) ? "createLoginContext.*" : paramString);
  }
  
  public AuthPermission(String paramString1, String paramString2)
  {
    super("createLoginContext".equals(paramString1) ? "createLoginContext.*" : paramString1, paramString2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\auth\AuthPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */