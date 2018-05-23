package sun.security.acl;

import java.security.Principal;

public class WorldGroupImpl
  extends GroupImpl
{
  public WorldGroupImpl(String paramString)
  {
    super(paramString);
  }
  
  public boolean isMember(Principal paramPrincipal)
  {
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\acl\WorldGroupImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */