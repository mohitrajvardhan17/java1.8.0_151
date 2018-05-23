package javax.management.remote;

import java.security.BasicPermission;

public final class SubjectDelegationPermission
  extends BasicPermission
{
  private static final long serialVersionUID = 1481618113008682343L;
  
  public SubjectDelegationPermission(String paramString)
  {
    super(paramString);
  }
  
  public SubjectDelegationPermission(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
    if (paramString2 != null) {
      throw new IllegalArgumentException("Non-null actions");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\remote\SubjectDelegationPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */