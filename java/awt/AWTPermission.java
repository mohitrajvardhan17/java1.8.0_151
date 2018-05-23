package java.awt;

import java.security.BasicPermission;

public final class AWTPermission
  extends BasicPermission
{
  private static final long serialVersionUID = 8890392402588814465L;
  
  public AWTPermission(String paramString)
  {
    super(paramString);
  }
  
  public AWTPermission(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\AWTPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */