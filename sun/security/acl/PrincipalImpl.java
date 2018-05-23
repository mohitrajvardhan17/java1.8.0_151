package sun.security.acl;

import java.security.Principal;

public class PrincipalImpl
  implements Principal
{
  private String user;
  
  public PrincipalImpl(String paramString)
  {
    user = paramString;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof PrincipalImpl))
    {
      PrincipalImpl localPrincipalImpl = (PrincipalImpl)paramObject;
      return user.equals(localPrincipalImpl.toString());
    }
    return false;
  }
  
  public String toString()
  {
    return user;
  }
  
  public int hashCode()
  {
    return user.hashCode();
  }
  
  public String getName()
  {
    return user;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\acl\PrincipalImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */