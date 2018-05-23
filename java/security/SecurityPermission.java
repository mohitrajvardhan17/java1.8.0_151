package java.security;

public final class SecurityPermission
  extends BasicPermission
{
  private static final long serialVersionUID = 5236109936224050470L;
  
  public SecurityPermission(String paramString)
  {
    super(paramString);
  }
  
  public SecurityPermission(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\SecurityPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */