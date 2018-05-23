package java.lang.reflect;

import java.security.BasicPermission;

public final class ReflectPermission
  extends BasicPermission
{
  private static final long serialVersionUID = 7412737110241507485L;
  
  public ReflectPermission(String paramString)
  {
    super(paramString);
  }
  
  public ReflectPermission(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\reflect\ReflectPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */